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
     * Valida antes de enviar al servidor.
     */
    public boolean executeMovement(int q, int r) {
        if (!validateAction("MOVE")) {
            return false;
        }
        
        if (playerState.isDead()) {
            logger.warn("Cannot move: player is dead");
            publishError("No puedes moverte: estás muerto");
            return false;
        }
        
        actionExecutor.sendMovementAction(q, r);
        eventBus.publish(new ActionEvent(GameEventType.ACTION_SENT, "MOVE", true, null));
        return true;
    }
    
    /**
     * Ejecuta una acción de ataque.
     */
    public boolean executeAttack(String targetId) {
        if (!validateAction("ATTACK")) {
            return false;
        }
        
        if (playerState.isDead()) {
            logger.warn("Cannot attack: player is dead");
            publishError("No puedes atacar: estás muerto");
            return false;
        }
        
        // TODO: Implementar envío de acción de ataque
        logger.info("Attack action: {}", targetId);
        eventBus.publish(new ActionEvent(GameEventType.ACTION_SENT, "ATTACK", true, null));
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
        
        // TODO: Implementar uso de item
        logger.info("Use item: {}", itemId);
        eventBus.publish(new ActionEvent(GameEventType.ACTION_SENT, "USE_ITEM", true, null));
        return true;
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
