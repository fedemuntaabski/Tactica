package com.juegito.server;

import com.juegito.game.lobby.LobbyManager;
import com.juegito.model.Player;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.*;
import com.juegito.protocol.dto.lobby.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja la comunicación con un cliente individual.
 * Ejecuta en su propio hilo para cada jugador conectado.
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static final long TIMEOUT_MS = 60000; // 60 segundos sin mensajes = desconexión
    
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
                    
                case REQUEST_RESYNC:
                    handleRequestResync(message);
                    break;
                    
                case RECONNECT_REQUEST:
                    handleReconnectRequest(message);
                    break;
                    
                case PING:
                    handlePing();
                    break;
                    
                case PLAYER_DISCONNECT:
                    handleDisconnect();
                    break;
                
                // FASE 4 - Nuevos mensajes de gameplay
                case ATTACK_REQUEST:
                    handleAttackRequest(message);
                    break;
                    
                case ABILITY_REQUEST:
                    handleAbilityRequest(message);
                    break;
                    
                case EVENT_INTERACTION:
                    handleEventInteraction(message);
                    break;
                    
                default:
                    logger.warn("Unhandled message type: {}", message.getType());
            }
            
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
    }
    
    private void handlePlayerAction(Message message) {
        PlayerActionDTO action = deserializePayload(message, PlayerActionDTO.class);
        server.handlePlayerAction(player.getPlayerId(), action);
    }
    
    private void handlePing() {
        Message pong = new Message(MessageType.PONG, "server", null);
        player.sendMessage(gson.toJson(pong));
    }
    
    // ========== Handlers FASE 4 - Nuevos sistemas ==========
    
    private PlayerActionDTO createActionDTO(String actionType, Map<String, Object> actionData) {
        PlayerActionDTO action = new PlayerActionDTO();
        action.setActionType(actionType);
        action.setActionData(actionData);
        return action;
    }
    
    private void handleAttackRequest(Message message) {
        AttackRequestDTO request = deserializePayload(message, AttackRequestDTO.class);
        if (request != null) {
            Map<String, Object> actionData = new HashMap<>();
            actionData.put("targetId", request.getTargetId());
            actionData.put("attackType", request.getAttackType());
            server.handlePlayerAction(player.getPlayerId(), createActionDTO("ATTACK", actionData));
        }
    }
    
    private void handleAbilityRequest(Message message) {
        AbilityRequestDTO request = deserializePayload(message, AbilityRequestDTO.class);
        if (request != null) {
            Map<String, Object> actionData = new HashMap<>();
            actionData.put("abilityId", request.getAbilityId());
            if (request.getTargetPosition() != null) {
                actionData.put("targetPosition", request.getTargetPosition());
            }
            if (request.getTargetId() != null) {
                actionData.put("targetId", request.getTargetId());
            }
            server.handlePlayerAction(player.getPlayerId(), createActionDTO("USE_ABILITY", actionData));
        }
    }
    
    private void handleEventInteraction(Message message) {
        EventInteractionDTO request = deserializePayload(message, EventInteractionDTO.class);
        if (request != null) {
            server.handleEventInteraction(
                player.getPlayerId(),
                request.getEventId(),
                request.getOptionIndex()
            );
        }
    }
    
    // ========== Handlers de mensajes del lobby ==========
    
    private void handleJoinRequest(Message message) {
        JoinRequestDTO request = deserializePayload(message, JoinRequestDTO.class);
        String ipAddress = player.getSocket().getInetAddress().getHostAddress();
        
        server.getLobbyManager().handleJoinRequest(ipAddress, request, (success, response) -> {
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
        ReadyStatusChangeDTO request = deserializePayload(message, ReadyStatusChangeDTO.class);
        server.getLobbyManager().handleReadyStatusChange(assignedPlayerId, request);
    }
    
    private void handleClassSelection(Message message) {
        if (assignedPlayerId == null) return;
        ClassSelectionDTO request = deserializePayload(message, ClassSelectionDTO.class);
        server.getLobbyManager().handleClassSelection(assignedPlayerId, request);
    }
    
    private void handleColorSelection(Message message) {
        if (assignedPlayerId == null) return;
        ColorSelectionDTO request = deserializePayload(message, ColorSelectionDTO.class);
        server.getLobbyManager().handleColorSelection(assignedPlayerId, request);
    }
    
    private void handleKickPlayer(Message message) {
        if (assignedPlayerId == null) return;
        KickPlayerDTO request = deserializePayload(message, KickPlayerDTO.class);
        server.getLobbyManager().handleKickPlayer(assignedPlayerId, request);
    }
    
    private void handleStartMatchRequest(Message message) {
        if (assignedPlayerId == null) return;
        server.getLobbyManager().handleStartMatchRequest(assignedPlayerId);
    }
    
    private void handleChangeLobbySettings(Message message) {
        if (assignedPlayerId == null) return;
        ChangeLobbySettingsDTO request = deserializePayload(message, ChangeLobbySettingsDTO.class);
        server.getLobbyManager().handleChangeLobbySettings(assignedPlayerId, request);
    }
    
    private void handleChatMessage(Message message) {
        if (assignedPlayerId == null) return;
        ChatMessageRequestDTO request = deserializePayload(message, ChatMessageRequestDTO.class);
        server.getLobbyManager().handleChatMessage(assignedPlayerId, request);
    }
    
    private void handleChangePlayerName(Message message) {
        if (assignedPlayerId == null) return;
        ChangePlayerNameDTO request = deserializePayload(message, ChangePlayerNameDTO.class);
        server.getLobbyManager().handleChangePlayerName(assignedPlayerId, request);
    }
    
    /**
     * Maneja solicitud de resincronización del cliente.
     * KISS: Envía estado completo del juego.
     */
    private void handleRequestResync(Message message) {
        if (assignedPlayerId == null) return;
        
        logger.info("Player {} requested resync", assignedPlayerId);
        server.sendFullResync(assignedPlayerId);
    }
    
    /**
     * Maneja solicitud de reconexión.
     * Permite que un cliente desconectado vuelva a la partida.
     */
    private void handleReconnectRequest(Message message) {
        ReconnectRequestDTO request = deserializePayload(message, ReconnectRequestDTO.class);
        
        logger.info("Reconnection request from {}", request.getPlayerId());
        
        // Validar que la partida sigue activa
        boolean accepted = server.handleReconnect(request.getPlayerId());
        
        if (accepted) {
            ReconnectResponseDTO response = ReconnectResponseDTO.accepted();
            Message responseMsg = new Message(MessageType.RECONNECT_ACCEPTED, "server", response);
            sendMessage(responseMsg);
            
            // Enviar resincronización completa
            server.sendFullResync(request.getPlayerId());
        } else {
            ReconnectResponseDTO response = ReconnectResponseDTO.rejected("Game no longer active");
            Message responseMsg = new Message(MessageType.RECONNECT_REJECTED, "server", response);
            sendMessage(responseMsg);
        }
    }
    
    /**
     * Deserializa el payload de un mensaje a un tipo específico.
     * DRY: Evita repetir la lógica de deserialización en cada handler.
     */
    private <T> T deserializePayload(Message message, Class<T> clazz) {
        return gson.fromJson(gson.toJson(message.getPayload()), clazz);
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
