package com.juegito.game.lobby;

import com.juegito.game.character.ClassDTOConverter;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.character.AvailableClassesDTO;
import com.juegito.protocol.dto.lobby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * Gestor del lobby que coordina todas las operaciones.
 * Maneja la comunicación entre el LobbyState y los clientes.
 */
public class LobbyManager {
    private static final Logger logger = LoggerFactory.getLogger(LobbyManager.class);
    private static final long SNAPSHOT_INTERVAL_MS = 300;
    
    private final LobbyState lobbyState;
    private final BiConsumer<String, Message> messageSender;
    private final ScheduledExecutorService scheduler;
    
    private volatile boolean running;
    
    /**
     * @param messageSender Función para enviar mensajes a un jugador específico (playerId, message)
     */
    public LobbyManager(BiConsumer<String, Message> messageSender) {
        this.lobbyState = new LobbyState();
        this.messageSender = messageSender;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.running = false;
        
        logger.info("LobbyManager creado - esperando primer jugador (host)");
    }
    
    /**
     * Inicia el broadcast periódico del lobby snapshot.
     */
    public void start() {
        if (running) {
            return;
        }
        
        running = true;
        scheduler.scheduleAtFixedRate(
            this::broadcastSnapshot,
            SNAPSHOT_INTERVAL_MS,
            SNAPSHOT_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
        
        logger.info("LobbyManager iniciado - broadcasting cada {}ms", SNAPSHOT_INTERVAL_MS);
    }
    
    /**
     * Detiene el lobby manager.
     */
    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("LobbyManager detenido");
    }
    
    /**
     * Agrega un jugador automáticamente al lobby (sin solicitud JOIN_REQUEST).
     * Usado cuando el servidor acepta conexiones directas.
     */
    public void autoAddPlayer(String playerId, String playerName, String ipAddress) {
        // Agregar jugador
        PlayerLobbyData player = lobbyState.autoAddPlayer(playerId, playerName, ipAddress);
        
        logger.info("Jugador {} ({}) agregado automáticamente al lobby", playerName, playerId);
        
        // Enviar snapshot actual al nuevo jugador inmediatamente
        sendSnapshotToPlayer(player.getPlayerId());
        
        // Enviar clases disponibles al nuevo jugador
        sendAvailableClasses(player.getPlayerId());
        
        // Notificar a todos los demás jugadores
        PlayerJoinedDTO notification = new PlayerJoinedDTO(player.toDTO());
        broadcastToOthers(player.getPlayerId(), MessageType.PLAYER_JOINED, notification);
    }
    
    /**
     * Maneja una solicitud de join.
     */
    public void handleJoinRequest(String ipAddress, JoinRequestDTO request, BiConsumer<Boolean, Message> responseCallback) {
        JoinResponseDTO validation = lobbyState.validateJoin(request.getPlayerName(), ipAddress);
        
        if (validation != null) {
            // Validación falló
            Message response = new Message(MessageType.JOIN_RESPONSE, "server", validation);
            responseCallback.accept(false, response);
            logger.info("Join rechazado para {}: {}", request.getPlayerName(), validation.getReason());
            return;
        }
        
        // Agregar jugador
        PlayerLobbyData player = lobbyState.addPlayer(request.getPlayerName(), ipAddress);
        
        // Responder al nuevo jugador
        JoinResponseDTO successResponse = JoinResponseDTO.success(player.getPlayerId());
        Message response = new Message(MessageType.JOIN_RESPONSE, "server", successResponse);
        responseCallback.accept(true, response);
        
        // Notificar a todos los demás jugadores
        PlayerJoinedDTO notification = new PlayerJoinedDTO(player.toDTO());
        broadcastToOthers(player.getPlayerId(), MessageType.PLAYER_JOINED, notification);
        
        // Enviar snapshot actual al nuevo jugador
        sendSnapshotToPlayer(player.getPlayerId());
        
        // Enviar clases disponibles al nuevo jugador
        sendAvailableClasses(player.getPlayerId());
    }
    
    /**
     * Maneja una solicitud de leave.
     */
    public void handleLeaveRequest(String playerId) {
        PlayerLobbyData player = lobbyState.removePlayer(playerId);
        
        if (player != null) {
            // Notificar a todos
            PlayerLeftDTO notification = new PlayerLeftDTO(
                player.getPlayerId(),
                player.getPlayerName(),
                "Player left"
            );
            broadcastToAll(MessageType.PLAYER_LEFT, notification);
        }
    }
    
    /**
     * Maneja un cambio de estado ready.
     */
    public void handleReadyStatusChange(String playerId, ReadyStatusChangeDTO request) {
        handlePlayerUpdate(
            playerId,
            () -> lobbyState.updateReadyStatus(playerId, request.isReady()),
            "READY_CHANGE",
            "No se puede cambiar estado ready"
        );
        // Eliminado checkAndAutoStartGame() - solo el host puede iniciar manualmente
    }
    
    /**
     * Maneja una selección de clase.
     */
    public void handleClassSelection(String playerId, ClassSelectionDTO request) {
        handlePlayerUpdate(
            playerId,
            () -> lobbyState.updateClass(playerId, request.getClassId()),
            "CLASS_SELECTION",
            "Clase inválida"
        );
    }
    
    /**
     * Maneja una selección de color.
     */
    public void handleColorSelection(String playerId, ColorSelectionDTO request) {
        handlePlayerUpdate(
            playerId,
            () -> lobbyState.updateColor(playerId, request.getColor()),
            "COLOR_SELECTION",
            "Color no disponible"
        );
    }
    
    /**
     * Método helper para manejar actualizaciones de jugador siguiendo DRY.
     * Ejecuta una actualización, y si tiene éxito, notifica a todos los clientes.
     */
    private boolean handlePlayerUpdate(String playerId, java.util.function.Supplier<Boolean> updateAction, 
                                       String actionType, String errorMessage) {
        boolean success = updateAction.get();
        
        if (success) {
            PlayerLobbyData player = lobbyState.getPlayer(playerId);
            PlayerUpdatedDTO notification = new PlayerUpdatedDTO(player.toDTO());
            broadcastToAll(MessageType.PLAYER_UPDATED, notification);
        } else {
            sendInvalidAction(playerId, actionType, errorMessage);
        }
        
        return success;
    }
    
    /**
     * Maneja un kick de jugador.
     */
    public void handleKickPlayer(String requesterId, KickPlayerDTO request) {
        if (!lobbyState.getHostId().equals(requesterId)) {
            sendInvalidAction(requesterId, "KICK_PLAYER", "Solo el host puede expulsar jugadores");
            return;
        }
        
        PlayerLobbyData kickedPlayer = lobbyState.removePlayer(request.getPlayerId());
        
        if (kickedPlayer != null) {
            // Notificar al jugador expulsado
            KickedFromLobbyDTO kickNotification = new KickedFromLobbyDTO(request.getReason());
            sendToPlayer(kickedPlayer.getPlayerId(), MessageType.KICKED_FROM_LOBBY, kickNotification);
            
            // Notificar a todos los demás
            PlayerLeftDTO leftNotification = new PlayerLeftDTO(
                kickedPlayer.getPlayerId(),
                kickedPlayer.getPlayerName(),
                "Kicked: " + request.getReason()
            );
            broadcastToAll(MessageType.PLAYER_LEFT, leftNotification);
        }
    }
    
    /**
     * Maneja un cambio de configuración del lobby.
     */
    public void handleChangeLobbySettings(String requesterId, ChangeLobbySettingsDTO request) {
        LobbyConfig newConfig = LobbyConfig.fromDTO(request.getNewSettings());
        boolean success = lobbyState.updateSettings(requesterId, newConfig);
        
        if (!success) {
            sendInvalidAction(requesterId, "CHANGE_SETTINGS", "No tienes permiso para cambiar la configuración");
        }
        // El snapshot periódico propagará los cambios
    }
    
    /**
     * Maneja una solicitud de inicio de partida.
     */
    public void handleStartMatchRequest(String requesterId) {
        String validationError = lobbyState.validateStartMatch(requesterId);
        
        if (validationError != null) {
            sendInvalidAction(requesterId, "START_MATCH", validationError);
            return;
        }
        
        // Cambiar estado a STARTING
        lobbyState.startMatch();
        
        // Generar configuración final
        StartMatchDTO startMatch = lobbyState.createStartMatchMessage();
        
        // Enviar a todos los jugadores
        broadcastToAll(MessageType.START_MATCH, startMatch);
        
        // Transicionar a IN_GAME
        lobbyState.transitionToInGame();
        
        logger.info("Partida iniciada en lobby {}", lobbyState.getLobbyId());
    }
    
    /**
     * Maneja un mensaje de chat.
     */
    public void handleChatMessage(String senderId, ChatMessageRequestDTO request) {
        PlayerLobbyData player = lobbyState.getPlayer(senderId);
        if (player == null) {
            logger.warn("Chat message from unknown player: {}", senderId);
            return;
        }
        
        String message = request.getMessage();
        if (message == null || message.trim().isEmpty() || message.length() > 500) {
            sendInvalidAction(senderId, "CHAT_MESSAGE", "Mensaje inválido");
            return;
        }
        
        // Crear mensaje de chat
        ChatMessageDTO chatMessage = new ChatMessageDTO(
            player.getPlayerId(),
            player.getPlayerName(),
            message.trim()
        );
        
        // Broadcast a todos
        broadcastToAll(MessageType.CHAT_MESSAGE, chatMessage);
        logger.debug("Chat from {}: {}", player.getPlayerName(), message);
    }
    
    /**
     * Maneja un cambio de nombre de jugador.
     */
    public void handleChangePlayerName(String playerId, ChangePlayerNameDTO request) {
        String newName = request.getNewName();
        
        if (newName == null || newName.trim().isEmpty() || newName.length() < 3 || newName.length() > 20) {
            sendInvalidAction(playerId, "CHANGE_NAME", "Nombre debe tener entre 3 y 20 caracteres");
            return;
        }
        
        handlePlayerUpdate(
            playerId,
            () -> lobbyState.updatePlayerName(playerId, newName.trim()),
            "CHANGE_NAME",
            "No se pudo cambiar el nombre"
        );
    }
    
    /**
     * Maneja una desconexión de jugador.
     */
    public void handlePlayerDisconnected(String playerId) {
        // Si es el host, el servidor debe cerrarse
        if (lobbyState.getHostId().equals(playerId)) {
            logger.warn("Host desconectado - cerrando lobby");
            stop();
            return;
        }
        
        // Marcar como desconectado
        lobbyState.markDisconnected(playerId);
        
        // Notificar a todos
        PlayerLobbyData player = lobbyState.getPlayer(playerId);
        if (player != null) {
            PlayerUpdatedDTO notification = new PlayerUpdatedDTO(player.toDTO());
            broadcastToAll(MessageType.PLAYER_UPDATED, notification);
        }
    }
    
    /**
     * Envía el snapshot del lobby a todos los jugadores.
     */
    private void broadcastSnapshot() {
        if (!running) {
            return;
        }
        
        LobbySnapshotDTO snapshot = lobbyState.createSnapshot();
        Message message = new Message(MessageType.LOBBY_SNAPSHOT, "server", snapshot);
        
        for (PlayerLobbyData player : lobbyState.getPlayers()) {
            messageSender.accept(player.getPlayerId(), message);
        }
    }
    
    /**
     * Envía el snapshot a un jugador específico.
     */
    private void sendSnapshotToPlayer(String playerId) {
        LobbySnapshotDTO snapshot = lobbyState.createSnapshot();
        Message message = new Message(MessageType.LOBBY_SNAPSHOT, "server", snapshot);
        messageSender.accept(playerId, message);
    }
    
    /**
     * Envía las clases disponibles a un jugador.
     */
    private void sendAvailableClasses(String playerId) {
        AvailableClassesDTO classes = ClassDTOConverter.toAvailableClassesDTO();
        Message message = new Message(MessageType.AVAILABLE_CLASSES, "server", classes);
        messageSender.accept(playerId, message);
    }
    
    /**
     * Envía un mensaje a todos los jugadores.
     */
    private void broadcastToAll(MessageType type, Object payload) {
        Message message = new Message(type, "server", payload);
        for (PlayerLobbyData player : lobbyState.getPlayers()) {
            messageSender.accept(player.getPlayerId(), message);
        }
    }
    
    /**
     * Envía un mensaje a todos excepto uno.
     */
    private void broadcastToOthers(String excludePlayerId, MessageType type, Object payload) {
        Message message = new Message(type, "server", payload);
        for (PlayerLobbyData player : lobbyState.getPlayers()) {
            if (!player.getPlayerId().equals(excludePlayerId)) {
                messageSender.accept(player.getPlayerId(), message);
            }
        }
    }
    
    /**
     * Envía un mensaje a un jugador específico.
     */
    private void sendToPlayer(String playerId, MessageType type, Object payload) {
        Message message = new Message(type, "server", payload);
        messageSender.accept(playerId, message);
    }
    
    /**
     * Envía una notificación de acción inválida.
     */
    private void sendInvalidAction(String playerId, String action, String reason) {
        InvalidActionDTO invalid = new InvalidActionDTO(action, reason);
        sendToPlayer(playerId, MessageType.INVALID_ACTION, invalid);
        logger.warn("Acción inválida de {}: {} - {}", playerId, action, reason);
    }
    
    // Getters
    
    public LobbyState getLobbyState() {
        return lobbyState;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Obtiene el número total de jugadores.
     */
    public int getPlayerCount() {
        return lobbyState.getPlayerCount();
    }
    
    /**
     * Obtiene el máximo de jugadores.
     */
    public int getMaxPlayers() {
        return lobbyState.getMaxPlayers();
    }
    
    /**
     * Verifica si el lobby está lleno.
     */
    public boolean isFull() {
        return lobbyState.isFull();
    }
    
    /**
     * Obtiene todos los jugadores.
     */
    public Collection<PlayerLobbyData> getPlayers() {
        return lobbyState.getPlayers();
    }
    
    /**
     * Obtiene un jugador específico.
     */
    public PlayerLobbyData getPlayer(String playerId) {
        return lobbyState.getPlayer(playerId);
    }
    
    /**
     * Obtiene el estado actual del lobby.
     */
    public LobbyStatus getStatus() {
        return lobbyState.getStatus();
    }
}
