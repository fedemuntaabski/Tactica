package com.juegito.protocol.dto;

/**
 * DTO para información de habilidad.
 */
public class AbilityDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private int cooldownTurns;
    private int range;
    private int basePower;
    private int currentCooldown; // Turnos restantes de cooldown para el jugador
    
    public AbilityDTO() {}
    
    public AbilityDTO(String id, String name, String description, String type,
                     int cooldownTurns, int range, int basePower, int currentCooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.cooldownTurns = cooldownTurns;
        this.range = range;
        this.basePower = basePower;
        this.currentCooldown = currentCooldown;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getCooldownTurns() { return cooldownTurns; }
    public void setCooldownTurns(int cooldownTurns) { this.cooldownTurns = cooldownTurns; }
    
    public int getRange() { return range; }
    public void setRange(int range) { this.range = range; }
    
    public int getBasePower() { return basePower; }
    public void setBasePower(int basePower) { this.basePower = basePower; }
    
    public int getCurrentCooldown() { return currentCooldown; }
    public void setCurrentCooldown(int currentCooldown) { this.currentCooldown = currentCooldown; }
    
    public boolean isAvailable() {
        return currentCooldown == 0;
    }
}
