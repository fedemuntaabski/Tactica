package com.juegito.client.events;

/**
 * Evento de acción del jugador.
 */
public class ActionEvent extends GameEvent {
    private final String actionType;
    private final boolean success;
    private final String message;
    
    public ActionEvent(GameEventType type, String actionType, boolean success, String message) {
        super(type);
        this.actionType = actionType;
        this.success = success;
        this.message = message;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
}
