package com.juegito.game;

import com.juegito.protocol.dto.PlayerActionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valida las acciones de los jugadores.
 * Asegura que solo se procesen acciones válidas según el estado del juego.
 */
public class ActionValidator {
    private static final Logger logger = LoggerFactory.getLogger(ActionValidator.class);
    
    private final GameState gameState;
    
    public ActionValidator(GameState gameState) {
        this.gameState = gameState;
    }
    
    /**
     * Valida si una acción es válida en el contexto actual del juego.
     */
    public ValidationResult validate(String playerId, PlayerActionDTO action) {
        if (!gameState.isGameActive()) {
            return ValidationResult.invalid("Game is not active");
        }
        
        if (!isPlayerTurn(playerId)) {
            return ValidationResult.invalid("Not player's turn");
        }
        
        if (action == null || action.getActionType() == null) {
            return ValidationResult.invalid("Invalid action format");
        }
        
        return validateActionType(playerId, action);
    }
    
    private boolean isPlayerTurn(String playerId) {
        String currentPlayer = gameState.getCurrentTurnPlayerId();
        return currentPlayer != null && currentPlayer.equals(playerId);
    }
    
    private ValidationResult validateActionType(String playerId, PlayerActionDTO action) {
        // Aquí se pueden agregar validaciones específicas por tipo de acción
        // Por ahora, implementación básica
        String actionType = action.getActionType();
        
        switch (actionType) {
            case "MOVE":
            case "ATTACK":
            case "DEFEND":
            case "SKIP":
                return ValidationResult.valid();
            default:
                logger.warn("Unknown action type: {}", actionType);
                return ValidationResult.invalid("Unknown action type: " + actionType);
        }
    }
    
    /**
     * Clase interna para representar el resultado de una validación.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String reason;
        
        private ValidationResult(boolean valid, String reason) {
            this.valid = valid;
            this.reason = reason;
        }
        
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult invalid(String reason) {
            return new ValidationResult(false, reason);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getReason() {
            return reason;
        }
    }
}
