package com.juegito.client.events;

/**
 * Evento de conexión.
 */
public class ConnectionEvent extends GameEvent {
    private final String message;
    
    public ConnectionEvent(GameEventType type, String message) {
        super(type);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
