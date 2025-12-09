package com.juegito.protocol.dto.map;

/**
 * DTO para transferir información de un evento de casilla.
 */
public class TileEventDTO {
    private String eventType;
    private String eventId;
    private String description;
    private boolean resolved;
    
    public TileEventDTO() {}
    
    public TileEventDTO(String eventType, String eventId, String description, boolean resolved) {
        this.eventType = eventType;
        this.eventId = eventId;
        this.description = description;
        this.resolved = resolved;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isResolved() {
        return resolved;
    }
    
    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
