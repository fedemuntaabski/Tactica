package com.juegito.protocol.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para evento aleatorio.
 */
public class RandomEventDTO {
    private String id;
    private String title;
    private String description;
    private String type;
    private List<EventOptionDTO> options;
    private HexCoordinateDTO location;
    
    public RandomEventDTO() {
        this.options = new ArrayList<>();
    }
    
    public RandomEventDTO(String id, String title, String description, String type,
                         List<EventOptionDTO> options, HexCoordinateDTO location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.options = new ArrayList<>(options);
        this.location = location;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<EventOptionDTO> getOptions() { return new ArrayList<>(options); }
    public void setOptions(List<EventOptionDTO> options) { this.options = new ArrayList<>(options); }
    
    public HexCoordinateDTO getLocation() { return location; }
    public void setLocation(HexCoordinateDTO location) { this.location = location; }
    
    /**
     * DTO para opción de evento.
     */
    public static class EventOptionDTO {
        private int index;
        private String text;
        private String description;
        
        public EventOptionDTO() {}
        
        public EventOptionDTO(int index, String text, String description) {
            this.index = index;
            this.text = text;
            this.description = description;
        }
        
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
