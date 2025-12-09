package com.juegito.game.loot;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un item en el juego.
 * Implementa KISS: propiedades simples sin lógica compleja.
 */
public class Item {
    private final String id;
    private final String name;
    private final String description;
    private final ItemType type;
    private final ItemRarity rarity;
    private final int value; // Para curación, daño, etc.
    private final boolean consumable;
    private final Map<String, Object> effects; // Efectos adicionales (stats, habilidades especiales)
    
    public Item(String id, String name, String description, ItemType type, 
                ItemRarity rarity, int value, boolean consumable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.value = value;
        this.consumable = consumable;
        this.effects = new HashMap<>();
    }
    
    public Item(String id, String name, String description, ItemType type,
                ItemRarity rarity, Map<String, Object> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.effects = new HashMap<>(effects);
        // Valor por defecto basado en efectos
        this.value = calculateValueFromEffects(effects);
        this.consumable = type == ItemType.POTION || type == ItemType.SCROLL;
    }
    
    private int calculateValueFromEffects(Map<String, Object> effects) {
        int total = 0;
        for (Object value : effects.values()) {
            if (value instanceof Integer) {
                total += (Integer) value;
            }
        }
        return total;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public ItemRarity getRarity() { return rarity; }
    public int getValue() { return value; }
    public boolean isConsumable() { return consumable; }
    public Map<String, Object> getEffects() { return new HashMap<>(effects); }
    
    public Object getEffect(String key) {
        return effects.get(key);
    }
    
    public boolean hasEffect(String key) {
        return effects.containsKey(key);
    }
    
    /**
     * Tipos de items disponibles.
     */
    public enum ItemType {
        POTION,      // Pociones de curación/maná
        SCROLL,      // Pergaminos de hechizos únicos
        WEAPON,      // Armas que aumentan daño
        ARMOR,       // Armaduras que aumentan defensa
        RESOURCE     // Recursos generales (oro, gemas)
    }
    
    /**
     * Rareza del item (afecta drop rate y poder).
     */
    public enum ItemRarity {
        COMMON,      // Común
        UNCOMMON,    // Poco común
        RARE,        // Raro
        EPIC,        // Épico
        LEGENDARY    // Legendario
    }
}
