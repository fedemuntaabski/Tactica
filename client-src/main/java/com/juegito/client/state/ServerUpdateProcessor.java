package com.juegito.client.state;

import com.google.gson.JsonObject;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.*;
import com.juegito.protocol.dto.lobby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Procesa actualizaciones del servidor y las aplica al estado local.
 * Actúa como puente entre mensajes de red y estado del cliente.
 */
public class ServerUpdateProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ServerUpdateProcessor.class);
    
    private final ClientGameState gameState;
    private final LobbyClientState lobbyState;
    private final List<StateChangeListener> listeners;
    
    public ServerUpdateProcessor(ClientGameState gameState, LobbyClientState lobbyState) {
        this.gameState = gameState;
        this.lobbyState = lobbyState;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Procesa un mensaje recibido del servidor.
     */
    public void processMessage(Message message) {
        if (message == null || message.getType() == null) {
            logger.warn("Received invalid message");
            return;
        }
        
        logger.debug("Processing message: {}", message.getType());
        
        switch (message.getType()) {
            case PLAYER_CONNECT:
                handlePlayerConnect(message);
                break;
            
            // Mensajes de lobby (nuevo sistema)
            case JOIN_RESPONSE:
                handleJoinResponse(message);
                break;
            
            case LOBBY_SNAPSHOT:
                handleLobbySnapshot(message);
                break;
            
            case PLAYER_JOINED:
                handlePlayerJoined(message);
                break;
            
            case PLAYER_LEFT:
                handlePlayerLeft(message);
                break;
            
            case PLAYER_UPDATED:
                handlePlayerUpdated(message);
                break;
            
            case INVALID_ACTION:
                handleInvalidAction(message);
                break;
            
            case START_MATCH:
                handleStartMatch(message);
                break;
            
            case KICKED_FROM_LOBBY:
                handleKickedFromLobby(message);
                break;
            
            case CHAT_MESSAGE:
                handleChatMessage(message);
                break;
                
            // Mensajes de lobby legacy
            case LOBBY_STATE:
                handleLobbyState(message);
                break;
                
            case START_GAME:
                handleStartGame(message);
                break;
                
            case GAME_STATE:
                handleGameState(message);
                break;
                
            case TURN_START:
                handleTurnStart(message);
                break;
                
            case TURN_END:
                handleTurnEnd(message);
                break;
                
            case ACTION_VALID:
                handleActionValid(message);
                break;
                
            case ACTION_INVALID:
                handleActionInvalid(message);
                break;
                
            case MAP_STATE:
                handleMapState(message);
                break;
                
            case MOVEMENT_RESULT:
                handleMovementResult(message);
                break;
                
            case PLAYER_DISCONNECT:
                handlePlayerDisconnect(message);
                break;
                
            case ERROR:
                handleError(message);
                break;
                
            case PONG:
                handlePong(message);
                break;
                
            default:
                logger.warn("Unhandled message type: {}", message.getType());
        }
    }
    
    private void handlePlayerConnect(Message message) {
        PlayerConnectDTO dto = (PlayerConnectDTO) message.getPayload();
        gameState.setPlayerId(dto.getPlayerId());
        gameState.setPlayerName(dto.getPlayerName());
        gameState.setCurrentPhase(ClientGameState.GamePhase.LOBBY);
        
        notifyListeners(StateChangeType.PLAYER_CONNECTED, dto);
        logger.info("Connected as {} ({})", dto.getPlayerName(), dto.getPlayerId());
    }
    
    // Handlers de mensajes del lobby (nuevo sistema)
    
    private void handleJoinResponse(Message message) {
        JoinResponseDTO dto = (JoinResponseDTO) message.getPayload();
        if (dto.isSuccess()) {
            lobbyState.setLocalPlayerId(dto.getPlayerId());
            logger.info("Successfully joined lobby as {}", dto.getPlayerId());
            notifyListeners(StateChangeType.JOINED_LOBBY, dto);
        } else {
            logger.error("Failed to join lobby: {}", dto.getReason());
            notifyListeners(StateChangeType.JOIN_FAILED, dto.getReason());
        }
    }
    
    private void handleLobbySnapshot(Message message) {
        LobbySnapshotDTO dto = (LobbySnapshotDTO) message.getPayload();
        lobbyState.updateFromSnapshot(dto);
        notifyListeners(StateChangeType.LOBBY_SNAPSHOT_UPDATED, dto);
    }
    
    private void handlePlayerJoined(Message message) {
        PlayerJoinedDTO dto = (PlayerJoinedDTO) message.getPayload();
        lobbyState.addPlayer(dto.getPlayer());
        notifyListeners(StateChangeType.LOBBY_PLAYER_JOINED, dto.getPlayer());
    }
    
    private void handlePlayerLeft(Message message) {
        PlayerLeftDTO dto = (PlayerLeftDTO) message.getPayload();
        lobbyState.removePlayer(dto.getPlayerId());
        logger.info("Player {} left: {}", dto.getPlayerName(), dto.getReason());
        notifyListeners(StateChangeType.LOBBY_PLAYER_LEFT, dto);
    }
    
    private void handlePlayerUpdated(Message message) {
        PlayerUpdatedDTO dto = (PlayerUpdatedDTO) message.getPayload();
        lobbyState.updatePlayer(dto.getPlayer());
        notifyListeners(StateChangeType.LOBBY_PLAYER_UPDATED, dto.getPlayer());
    }
    
    private void handleInvalidAction(Message message) {
        InvalidActionDTO dto = (InvalidActionDTO) message.getPayload();
        logger.warn("Invalid action: {} - {}", dto.getAction(), dto.getReason());
        notifyListeners(StateChangeType.LOBBY_INVALID_ACTION, dto);
    }
    
    private void handleStartMatch(Message message) {
        StartMatchDTO dto = (StartMatchDTO) message.getPayload();
        lobbyState.setStatus(LobbyStatus.IN_GAME);
        gameState.setGameStarted(true);
        gameState.setCurrentPhase(ClientGameState.GamePhase.STARTING);
        
        logger.info("Match starting! Seed: {}, Players: {}", dto.getSeed(), dto.getPlayers().size());
        notifyListeners(StateChangeType.MATCH_STARTING, dto);
    }
    
    private void handleKickedFromLobby(Message message) {
        KickedFromLobbyDTO dto = (KickedFromLobbyDTO) message.getPayload();
        logger.error("Kicked from lobby: {}", dto.getReason());
        notifyListeners(StateChangeType.KICKED_FROM_LOBBY, dto.getReason());
    }
    
    private void handleChatMessage(Message message) {
        ChatMessageDTO dto = (ChatMessageDTO) message.getPayload();
        lobbyState.addChatMessage(dto);
        notifyListeners(StateChangeType.LOBBY_CHAT_MESSAGE, dto);
    }
    
    private void handleLobbyState(Message message) {
        LobbyStateDTO dto = (LobbyStateDTO) message.getPayload();
        gameState.updateLobbyState(dto);
        
        notifyListeners(StateChangeType.LOBBY_UPDATED, dto);
    }
    
    private void handleStartGame(Message message) {
        gameState.setGameStarted(true);
        gameState.setCurrentPhase(ClientGameState.GamePhase.STARTING);
        
        notifyListeners(StateChangeType.GAME_STARTING, null);
        logger.info("Game is starting!");
    }
    
    private void handleGameState(Message message) {
        GameStateDTO dto = (GameStateDTO) message.getPayload();
        gameState.updateGameState(dto);
        gameState.setCurrentPhase(ClientGameState.GamePhase.PLAYING);
        
        notifyListeners(StateChangeType.GAME_STATE_UPDATED, dto);
    }
    
    private void handleTurnStart(Message message) {
        notifyListeners(StateChangeType.TURN_STARTED, message.getPayload());
        
        if (gameState.isMyTurn()) {
            logger.info("It's your turn! (Turn {})", gameState.getTurnNumber());
        } else {
            logger.debug("Turn started for player {}", gameState.getCurrentTurnPlayerId());
        }
    }
    
    private void handleTurnEnd(Message message) {
        notifyListeners(StateChangeType.TURN_ENDED, message.getPayload());
    }
    
    private void handleActionValid(Message message) {
        notifyListeners(StateChangeType.ACTION_ACCEPTED, message.getPayload());
        logger.debug("Action accepted by server");
    }
    
    private void handleActionInvalid(Message message) {
        JsonObject payload = (JsonObject) message.getPayload();
        String reason = payload != null && payload.has("reason") 
            ? payload.get("reason").getAsString() 
            : "Unknown reason";
        
        notifyListeners(StateChangeType.ACTION_REJECTED, reason);
        logger.warn("Action rejected: {}", reason);
    }
    
    private void handleMapState(Message message) {
        GameMapDTO mapDTO = (GameMapDTO) message.getPayload();
        gameState.setGameMap(mapDTO);
        notifyListeners(StateChangeType.MAP_UPDATED, mapDTO);
        logger.debug("Map state updated");
    }
    
    private void handleMovementResult(Message message) {
        MovementDTO movementDTO = (MovementDTO) message.getPayload();
        gameState.setLastMovement(movementDTO);
        notifyListeners(StateChangeType.MOVEMENT_EXECUTED, movementDTO);
        
        if (movementDTO.getBiomeEffect() != null) {
            logger.info("Movement effect: {}", movementDTO.getBiomeEffect());
        }
    }
    
    private void handlePlayerDisconnect(Message message) {
        notifyListeners(StateChangeType.PLAYER_DISCONNECTED, message.getPayload());
    }
    
    private void handleError(Message message) {
        notifyListeners(StateChangeType.ERROR_RECEIVED, message.getPayload());
        logger.error("Error from server: {}", message.getPayload());
    }
    
    private void handlePong(Message message) {
        // Respuesta a ping - usado para keepalive
        logger.trace("Pong received");
    }
    
    /**
     * Registra un listener para cambios de estado.
     */
    public void addStateChangeListener(StateChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remueve un listener.
     */
    public void removeStateChangeListener(StateChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(StateChangeType type, Object data) {
        for (StateChangeListener listener : listeners) {
            try {
                listener.onStateChange(type, data);
            } catch (Exception e) {
                logger.error("Error in state change listener: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Interfaz para listeners de cambios de estado.
     */
    public interface StateChangeListener {
        void onStateChange(StateChangeType type, Object data);
    }
    
    /**
     * Tipos de cambios de estado.
     */
    public enum StateChangeType {
        PLAYER_CONNECTED,
        
        // Eventos del lobby (nuevo sistema)
        JOINED_LOBBY,
        JOIN_FAILED,
        LOBBY_SNAPSHOT_UPDATED,
        LOBBY_PLAYER_JOINED,
        LOBBY_PLAYER_LEFT,
        LOBBY_PLAYER_UPDATED,
        LOBBY_INVALID_ACTION,
        MATCH_STARTING,
        KICKED_FROM_LOBBY,
        LOBBY_CHAT_MESSAGE,
        
        // Eventos legacy
        LOBBY_UPDATED,
        GAME_STARTING,
        GAME_STATE_UPDATED,
        TURN_STARTED,
        TURN_ENDED,
        ACTION_ACCEPTED,
        ACTION_REJECTED,
        MAP_UPDATED,
        MOVEMENT_EXECUTED,
        PLAYER_DISCONNECTED,
        ERROR_RECEIVED
    }
}
