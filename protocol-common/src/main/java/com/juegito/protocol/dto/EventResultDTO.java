package com.juegito.protocol.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para resultado de evento.
 */
public class EventResultDTO {
    private String eventId;
    private boolean success;
    private String message;
    private List<ItemDTO> items;
    private int hpChange;
    private String effect;
    
    public EventResultDTO() {
        this.items = new ArrayList<>();
    }
    
    public EventResultDTO(String eventId, boolean success, String message,
                         List<ItemDTO> items, int hpChange, String effect) {
        this.eventId = eventId;
        this.success = success;
        this.message = message;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.hpChange = hpChange;
        this.effect = effect;
    }
    
    // Getters y Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<ItemDTO> getItems() { return new ArrayList<>(items); }
    public void setItems(List<ItemDTO> items) { this.items = new ArrayList<>(items); }
    
    public int getHpChange() { return hpChange; }
    public void setHpChange(int hpChange) { this.hpChange = hpChange; }
    
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }
}
