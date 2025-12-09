package com.juegito.game.event;

import com.juegito.model.HexCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un evento aleatorio en el juego.
 * Implementa KISS: estructura simple de evento con opciones.
 */
public class RandomEvent {
    private final String id;
    private final String title;
    private final String description;
    private final EventType type;
    private final List<EventOption> options;
    private final HexCoordinate location;
    
    public RandomEvent(String id, String title, String description, EventType type,
                      List<EventOption> options, HexCoordinate location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.options = new ArrayList<>(options);
        this.location = location;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public EventType getType() { return type; }
    public List<EventOption> getOptions() { return new ArrayList<>(options); }
    public HexCoordinate getLocation() { return location; }
    
    /**
     * Tipos de eventos aleatorios.
     */
    public enum EventType {
        CHEST,       // Cofre con loot
        TRAP,        // Trampa que causa daño
        ENCOUNTER,   // Encuentro narrativo
        SHRINE,      // Santuario con buff temporal
        MERCHANT     // Mercader para comprar/vender
    }
    
    /**
     * Opción de respuesta a un evento.
     */
    public static class EventOption {
        private final int index;
        private final String text;
        private final String description;
        private final double successChance;
        
        public EventOption(int index, String text, String description, double successChance) {
            this.index = index;
            this.text = text;
            this.description = description;
            this.successChance = successChance;
        }
        
        public int getIndex() { return index; }
        public String getText() { return text; }
        public String getDescription() { return description; }
        public double getSuccessChance() { return successChance; }
    }
}
