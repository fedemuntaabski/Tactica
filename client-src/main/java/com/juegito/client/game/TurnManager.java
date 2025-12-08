package com.juegito.client.game;

import com.juegito.client.protocol.Message;
import com.juegito.client.protocol.MessageType;
import com.juegito.client.protocol.dto.PlayerActionDTO;
import com.juegito.client.state.ClientGameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestiona los turnos del jugador local.
 * Controla cuándo el jugador puede actuar y coordina las acciones.
 */
public class TurnManager {
    private static final Logger logger = LoggerFactory.getLogger(TurnManager.class);
    
    private final ClientGameState gameState;
    private final ActionExecutor actionExecutor;
    
    private boolean waitingForActionResponse;
    
    public TurnManager(ClientGameState gameState, ActionExecutor actionExecutor) {
        this.gameState = gameState;
        this.actionExecutor = actionExecutor;
        this.waitingForActionResponse = false;
    }
    
    /**
     * Verifica si el jugador puede realizar una acción.
     */
    public boolean canPerformAction() {
        return gameState.isMyTurn() 
            && !waitingForActionResponse
            && gameState.getCurrentPhase() == ClientGameState.GamePhase.PLAYING;
    }
    
    /**
     * Ejecuta una acción del jugador.
     */
    public boolean executeAction(String actionType, Object actionData) {
        if (!canPerformAction()) {
            logger.warn("Cannot perform action now");
            return false;
        }
        
        PlayerActionDTO action = new PlayerActionDTO(actionType, actionData);
        Message message = new Message(
            MessageType.PLAYER_ACTION,
            gameState.getPlayerId(),
            action
        );
        
        waitingForActionResponse = true;
        actionExecutor.sendAction(message);
        
        logger.info("Action sent: {}", actionType);
        return true;
    }
    
    /**
     * Notifica que se recibió respuesta de acción.
     */
    public void onActionResponse(boolean accepted) {
        waitingForActionResponse = false;
        
        if (accepted) {
            logger.debug("Action was accepted");
        } else {
            logger.debug("Action was rejected");
        }
    }
    
    /**
     * Obtiene información del turno actual.
     */
    public TurnInfo getCurrentTurnInfo() {
        return new TurnInfo(
            gameState.getTurnNumber(),
            gameState.getCurrentTurnPlayerId(),
            gameState.isMyTurn(),
            canPerformAction()
        );
    }
    
    /**
     * Clase que encapsula información del turno.
     */
    public static class TurnInfo {
        private final int turnNumber;
        private final String currentPlayerId;
        private final boolean isMyTurn;
        private final boolean canAct;
        
        public TurnInfo(int turnNumber, String currentPlayerId, boolean isMyTurn, boolean canAct) {
            this.turnNumber = turnNumber;
            this.currentPlayerId = currentPlayerId;
            this.isMyTurn = isMyTurn;
            this.canAct = canAct;
        }
        
        public int getTurnNumber() {
            return turnNumber;
        }
        
        public String getCurrentPlayerId() {
            return currentPlayerId;
        }
        
        public boolean isMyTurn() {
            return isMyTurn;
        }
        
        public boolean canAct() {
            return canAct;
        }
    }
}
