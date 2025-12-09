package com.juegito.model.map;

/**
 * Representa un evento que puede ocurrir en una casilla.
 */
public class TileEvent {
    private final EventType eventType;
    private final String eventId;
    private final String description;
    private boolean resolved;
    private Object eventData;
    
    public TileEvent(EventType eventType, String eventId, String description) {
        this.eventType = eventType;
        this.eventId = eventId;
        this.description = description;
        this.resolved = false;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isResolved() {
        return resolved;
    }
    
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
    
    public Object getEventData() {
        return eventData;
    }
    
    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }
    
    /**
     * Tipos de eventos que pueden ocurrir en una casilla.
     */
    public enum EventType {
        COMBAT,       // Combate con enemigos
        TRAP,         // Trampa que requiere decisión
        LOOT,         // Cofre o tesoro
        NARRATIVE,    // Evento narrativo con decisiones
        ALTAR,        // Altar con bendición/maldición
        SHOP,         // Tienda para comprar items
        CAMP,         // Campamento para descansar
        AMBUSH,       // Emboscada sorpresa
        PUZZLE        // Acertijo o puzzle
    }
}
