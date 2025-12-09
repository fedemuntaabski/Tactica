package com.juegito.protocol.dto;

/**
 * DTO para información de item.
 */
public class ItemDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private String rarity;
    private int value;
    private boolean consumable;
    
    public ItemDTO() {}
    
    public ItemDTO(String id, String name, String description, String type,
                  String rarity, int value, boolean consumable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.value = value;
        this.consumable = consumable;
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
    
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    
    public boolean isConsumable() { return consumable; }
    public void setConsumable(boolean consumable) { this.consumable = consumable; }
}
