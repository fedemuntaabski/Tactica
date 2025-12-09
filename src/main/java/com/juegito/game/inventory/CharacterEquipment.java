package com.juegito.game.inventory;

import com.juegito.game.loot.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Equipamiento individual de un personaje.
 * Cada jugador tiene su propio panel de slots.
 * Items equipados salen del inventario global.
 * Implementa KISS: mapa simple de slot → item.
 */
public class CharacterEquipment {
    private static final Logger logger = LoggerFactory.getLogger(CharacterEquipment.class);
    
    private final String playerId;
    private final Map<EquipmentSlot, Item> equippedItems;
    private final List<String> activeBuffs;
    
    public CharacterEquipment(String playerId) {
        this.playerId = playerId;
        this.equippedItems = new EnumMap<>(EquipmentSlot.class);
        this.activeBuffs = new ArrayList<>();
    }
    
    /**
     * Equipa un item en un slot específico.
     * Retorna el item previamente equipado (si hay).
     */
    public Optional<Item> equipItem(EquipmentSlot slot, Item item) {
        if (!canEquipInSlot(item, slot)) {
            logger.warn("Item {} cannot be equipped in slot {}", item.getName(), slot);
            return Optional.empty();
        }
        
        Item previous = equippedItems.put(slot, item);
        logger.info("Player {} equipped {} in slot {}", playerId, item.getName(), slot);
        
        applyItemEffects(item);
        
        return Optional.ofNullable(previous);
    }
    
    /**
     * Desequipa un item de un slot.
     */
    public Optional<Item> unequipItem(EquipmentSlot slot) {
        Item item = equippedItems.remove(slot);
        if (item != null) {
            logger.info("Player {} unequipped {} from slot {}", playerId, item.getName(), slot);
            removeItemEffects(item);
        }
        return Optional.ofNullable(item);
    }
    
    /**
     * Obtiene el item equipado en un slot.
     */
    public Optional<Item> getEquippedItem(EquipmentSlot slot) {
        return Optional.ofNullable(equippedItems.get(slot));
    }
    
    /**
     * Obtiene todos los items equipados.
     */
    public Map<EquipmentSlot, Item> getAllEquippedItems() {
        return new EnumMap<>(equippedItems);
    }
    
    /**
     * Verifica si un item puede equiparse en un slot.
     */
    private boolean canEquipInSlot(Item item, EquipmentSlot slot) {
        if (!item.hasEffect("equipSlot")) {
            return false;
        }
        
        Object slotData = item.getEffect("equipSlot");
        if (slotData instanceof EquipmentSlot) {
            return slotData == slot;
        } else if (slotData instanceof List) {
            return ((List<?>) slotData).contains(slot);
        }
        
        return false;
    }
    
    /**
     * Aplica efectos pasivos del item equipado.
     */
    private void applyItemEffects(Item item) {
        if (item.hasEffect("passiveAbility")) {
            String ability = (String) item.getEffect("passiveAbility");
            activeBuffs.add(ability);
            logger.info("Applied passive ability: {}", ability);
        }
    }
    
    /**
     * Remueve efectos pasivos al desequipar.
     */
    private void removeItemEffects(Item item) {
        if (item.hasEffect("passiveAbility")) {
            String ability = (String) item.getEffect("passiveAbility");
            activeBuffs.remove(ability);
            logger.info("Removed passive ability: {}", ability);
        }
    }
    
    /**
     * Calcula stats totales del equipamiento.
     */
    public Map<String, Integer> calculateTotalStats() {
        Map<String, Integer> totalStats = new HashMap<>();
        
        for (Item item : equippedItems.values()) {
            Map<String, Object> effects = item.getEffects();
            effects.forEach((key, value) -> {
                if (value instanceof Number && !key.equals("equipSlot") && !key.equals("category")) {
                    totalStats.merge(key, ((Number) value).intValue(), Integer::sum);
                }
            });
        }
        
        return totalStats;
    }
    
    /**
     * Obtiene el arma equipada.
     */
    public Optional<Item> getWeapon() {
        return getEquippedItem(EquipmentSlot.WEAPON);
    }
    
    /**
     * Obtiene el escudo equipado.
     */
    public Optional<Item> getShield() {
        return getEquippedItem(EquipmentSlot.SHIELD);
    }
    
    /**
     * Verifica si tiene un tipo específico de item equipado.
     */
    public boolean hasItemEquipped(EquipmentSlot slot) {
        return equippedItems.containsKey(slot);
    }
    
    /**
     * Obtiene buffs activos del equipamiento.
     */
    public List<String> getActiveBuffs() {
        return new ArrayList<>(activeBuffs);
    }
    
    /**
     * Limpia todo el equipamiento (para testing o muerte).
     */
    public List<Item> unequipAll() {
        List<Item> allItems = new ArrayList<>(equippedItems.values());
        equippedItems.clear();
        activeBuffs.clear();
        logger.info("Player {} unequipped all items", playerId);
        return allItems;
    }
}
