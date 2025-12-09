package com.juegito.client.events;

/**
 * Evento base del sistema de eventos del cliente.
 * Implementa patrón Observer para desacoplar componentes.
 */
public abstract class GameEvent {
    private final GameEventType type;
    private final long timestamp;
    
    protected GameEvent(GameEventType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public GameEventType getType() {
        return type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
