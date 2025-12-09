package com.juegito.server;

import com.juegito.game.lobby.LobbyManager;
import com.juegito.model.Player;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.PlayerActionDTO;
import com.juegito.protocol.dto.lobby.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Maneja la comunicación con un cliente individual.
 * Ejecuta en su propio hilo para cada jugador conectado.
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static final long TIMEOUT_MS = 30000; // 30 segundos sin mensajes = desconexión
    
    private final Player player;
    private final GameServer server;
    private final Gson gson;
    private volatile boolean running;
    private final String assignedPlayerId; // ID asignado desde el inicio (player.getPlayerId())
    private volatile long lastMessageTime; // Timestamp del último mensaje recibido
    
    public ClientHandler(Player player, GameServer server) {
        this.player = player;
        this.server = server;
        this.gson = new Gson();
        this.running = true;
        this.assignedPlayerId = player.getPlayerId(); // Asignar desde el inicio
        this.lastMessageTime = System.currentTimeMillis();
    }
    
    @Override
    public void run() {
        logger.info("Client handler started for player {}", player.getPlayerId());
        
        try {
            while (running) {
                String messageJson = player.receiveMessage();
                
                if (messageJson == null) {
                    // Cliente desconectado
                    handleDisconnect();
                    break;
                }
                
                processMessage(messageJson);
                
                // Verificar timeout de inactividad
                long inactiveTime = System.currentTimeMillis() - lastMessageTime;
                if (inactiveTime > TIMEOUT_MS) {
                    logger.warn("Client {} timed out after {}ms inactivity", 
                        player.getPlayerId(), inactiveTime);
                    handleDisconnect();
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Error reading from client {}: {}", player.getPlayerId(), e.getMessage());
            handleDisconnect();
        }
    }
    
    private void processMessage(String messageJson) {
        try {
            lastMessageTime = System.currentTimeMillis(); // Actualizar timestamp
            
            Message message = gson.fromJson(messageJson, Message.class);
            
            if (message == null || message.getType() == null) {
                logger.warn("Received invalid message from {}", player.getPlayerId());
                return;
            }
            
            logger.debug("Received {} from {}", message.getType(), player.getPlayerId());
            
            switch (message.getType()) {
                // Mensajes del lobby
                case JOIN_REQUEST:
                    handleJoinRequest(message);
                    break;
                    
                case LEAVE_LOBBY:
                    handleLeaveLobby(message);
                    break;
                    
                case READY_STATUS_CHANGE:
                    handleReadyStatusChange(message);
                    break;
                    
                case CLASS_SELECTION:
                    handleClassSelection(message);
                    break;
                    
                case COLOR_SELECTION:
                    handleColorSelection(message);
                    break;
                    
                case KICK_PLAYER:
                    handleKickPlayer(message);
                    break;
                    
                case START_MATCH_REQUEST:
                    handleStartMatchRequest(message);
                    break;
                    
                case CHANGE_LOBBY_SETTINGS:
                    handleChangeLobbySettings(message);
                    break;
                
                case CHAT_MESSAGE_REQUEST:
                    handleChatMessage(message);
                    break;
                    
                case CHANGE_PLAYER_NAME:
                    handleChangePlayerName(message);
                    break;
                
                // Mensajes del juego
                case PLAYER_ACTION:
                    handlePlayerAction(message);
                    break;
                    
                case PING:
                    handlePing();
                    break;
                    
                case PLAYER_DISCONNECT:
                    handleDisconnect();
                    break;
                    
                default:
                    logger.warn("Unhandled message type: {}", message.getType());
            }
            
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
    }
    
    private void handlePlayerAction(Message message) {
        PlayerActionDTO action = gson.fromJson(
            gson.toJson(message.getPayload()), 
            PlayerActionDTO.class
        );
        server.handlePlayerAction(player.getPlayerId(), action);
    }
    
    private void handlePing() {
        Message pong = new Message(MessageType.PONG, "server", null);
        player.sendMessage(gson.toJson(pong));
    }
    
    // ========== Handlers de mensajes del lobby ==========
    
    private void handleJoinRequest(Message message) {
        JoinRequestDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            JoinRequestDTO.class
        );
        
        String ipAddress = player.getSocket().getInetAddress().getHostAddress();
        LobbyManager lobbyManager = server.getLobbyManager();
        
        lobbyManager.handleJoinRequest(ipAddress, request, (success, response) -> {
            sendMessage(response);
            if (success) {
                logger.info("Player joined with ID: {}", assignedPlayerId);
            }
        });
    }
    
    private void handleLeaveLobby(Message message) {
        if (assignedPlayerId != null) {
            server.getLobbyManager().handleLeaveRequest(assignedPlayerId);
        }
    }
    
    private void handleReadyStatusChange(Message message) {
        if (assignedPlayerId == null) return;
        
        ReadyStatusChangeDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            ReadyStatusChangeDTO.class
        );
        
        server.getLobbyManager().handleReadyStatusChange(assignedPlayerId, request);
    }
    
    private void handleClassSelection(Message message) {
        if (assignedPlayerId == null) return;
        
        ClassSelectionDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            ClassSelectionDTO.class
        );
        
        server.getLobbyManager().handleClassSelection(assignedPlayerId, request);
    }
    
    private void handleColorSelection(Message message) {
        if (assignedPlayerId == null) return;
        
        ColorSelectionDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            ColorSelectionDTO.class
        );
        
        server.getLobbyManager().handleColorSelection(assignedPlayerId, request);
    }
    
    private void handleKickPlayer(Message message) {
        if (assignedPlayerId == null) return;
        
        KickPlayerDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            KickPlayerDTO.class
        );
        
        server.getLobbyManager().handleKickPlayer(assignedPlayerId, request);
    }
    
    private void handleStartMatchRequest(Message message) {
        if (assignedPlayerId == null) return;
        
        server.getLobbyManager().handleStartMatchRequest(assignedPlayerId);
    }
    
    private void handleChangeLobbySettings(Message message) {
        if (assignedPlayerId == null) return;
        
        ChangeLobbySettingsDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            ChangeLobbySettingsDTO.class
        );
        
        server.getLobbyManager().handleChangeLobbySettings(assignedPlayerId, request);
    }
    
    private void handleChatMessage(Message message) {
        if (assignedPlayerId == null) return;
        
        ChatMessageRequestDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            ChatMessageRequestDTO.class
        );
        
        server.getLobbyManager().handleChatMessage(assignedPlayerId, request);
    }
    
    private void handleChangePlayerName(Message message) {
        if (assignedPlayerId == null) return;
        
        ChangePlayerNameDTO request = gson.fromJson(
            gson.toJson(message.getPayload()),
            ChangePlayerNameDTO.class
        );
        
        server.getLobbyManager().handleChangePlayerName(assignedPlayerId, request);
    }
    
    // ========== Fin de handlers del lobby ==========
    
    private void handleDisconnect() {
        logger.info("Player {} disconnected", player.getPlayerId());
        running = false;
        
        // Notificar al lobby manager si el jugador estaba en el lobby
        if (assignedPlayerId != null) {
            server.getLobbyManager().handlePlayerDisconnected(assignedPlayerId);
        }
        
        server.handlePlayerDisconnect(player.getPlayerId());
    }
    
    /**
     * Envía un mensaje al cliente.
     */
    public void sendMessage(Message message) {
        player.sendMessage(gson.toJson(message));
    }
    
    public void stop() {
        running = false;
    }
}
