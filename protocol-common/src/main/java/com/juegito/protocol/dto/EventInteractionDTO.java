package com.juegito.protocol.dto;

/**
 * DTO para solicitud de interacción con evento.
 */
public class EventInteractionDTO {
    private String eventId;
    private int optionIndex;
    
    public EventInteractionDTO() {}
    
    public EventInteractionDTO(String eventId, int optionIndex) {
        this.eventId = eventId;
        this.optionIndex = optionIndex;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public int getOptionIndex() {
        return optionIndex;
    }
    
    public void setOptionIndex(int optionIndex) {
        this.optionIndex = optionIndex;
    }
}
