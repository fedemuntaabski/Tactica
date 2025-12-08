package com.juegito.client.state;

import com.google.gson.JsonObject;
import com.juegito.client.protocol.Message;
import com.juegito.client.protocol.MessageType;
import com.juegito.client.protocol.dto.GameStateDTO;
import com.juegito.client.protocol.dto.LobbyStateDTO;
import com.juegito.client.protocol.dto.PlayerActionDTO;
import com.juegito.client.protocol.dto.PlayerConnectDTO;
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
    private final List<StateChangeListener> listeners;
    
    public ServerUpdateProcessor(ClientGameState gameState) {
        this.gameState = gameState;
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
        LOBBY_UPDATED,
        GAME_STARTING,
        GAME_STATE_UPDATED,
        TURN_STARTED,
        TURN_ENDED,
        ACTION_ACCEPTED,
        ACTION_REJECTED,
        PLAYER_DISCONNECTED,
        ERROR_RECEIVED
    }
}
