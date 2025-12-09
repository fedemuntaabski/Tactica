package com.juegito.client.controllers;

import com.juegito.client.events.*;
import com.juegito.client.game.ActionExecutor;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.PlayerLocalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de acciones del jugador.
 * Valida acciones antes de enviarlas al servidor.
 * Implementa KISS: Una responsabilidad - validar y ejecutar acciones.
 */
public class ActionController {
    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);
    
    private final ClientGameState gameState;
    private final PlayerLocalState playerState;
    private final ActionExecutor actionExecutor;
    private final EventBus eventBus;
    
    public ActionController(ClientGameState gameState, PlayerLocalState playerState, 
                           ActionExecutor actionExecutor, EventBus eventBus) {
        this.gameState = gameState;
        this.playerState = playerState;
        this.actionExecutor = actionExecutor;
        this.eventBus = eventBus;
    }
    
    /**
     * Ejecuta una acción de movimiento.
     */
    public boolean executeMovement(int q, int r) {
        if (!validateCommonConditions("MOVE")) {
            return false;
        }
        
        actionExecutor.sendMovementAction(q, r);
        publishActionSent("MOVE");
        return true;
    }
    
    /**
     * Ejecuta una acción de ataque.
     */
    public boolean executeAttack(String targetId) {
        if (!validateCommonConditions("ATTACK")) {
            return false;
        }
        
        actionExecutor.sendAttackAction(targetId, "MELEE");
        logger.info("Attack action sent: targetId={}", targetId);
        publishActionSent("ATTACK");
        return true;
    }
    
    /**
     * Ejecuta uso de item.
     */
    public boolean executeUseItem(String itemId) {
        if (!validateAction("USE_ITEM")) {
            return false;
        }
        
        if (!playerState.hasItem(itemId)) {
            logger.warn("Cannot use item: not in inventory");
            publishError("No tienes ese item");
            return false;
        }
        
        actionExecutor.sendUseItemAction(itemId);
        logger.info("Use item action sent: itemId={}", itemId);
        publishActionSent("USE_ITEM");
        return true;
    }
    
    private boolean validateCommonConditions(String actionType) {
        if (!validateAction(actionType)) {
            return false;
        }
        
        if (playerState.isDead()) {
            logger.warn("Cannot {}: player is dead", actionType.toLowerCase());
            publishError("No puedes realizar acciones: estás muerto");
            return false;
        }
        
        return true;
    }
    
    private void publishActionSent(String actionType) {
        eventBus.publish(new ActionEvent(GameEventType.ACTION_SENT, actionType, true, null));
    }
    
    /**
     * Valida si se puede ejecutar una acción.
     */
    private boolean validateAction(String actionType) {
        if (gameState.getCurrentPhase() != ClientGameState.GamePhase.PLAYING) {
            logger.warn("Cannot perform action: not in game");
            publishError("No estás en una partida");
            return false;
        }
        
        if (!gameState.isMyTurn()) {
            logger.warn("Cannot perform action: not your turn");
            publishError("No es tu turno");
            return false;
        }
        
        return true;
    }
    
    /**
     * Publica un error de UI.
     */
    private void publishError(String message) {
        eventBus.publish(new ActionEvent(GameEventType.UI_ERROR, null, false, message));
    }
}
