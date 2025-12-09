package com.juegito.game.inventory;

import com.juegito.game.loot.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Gestor central del sistema de inventario.
 * Coordina el inventario compartido (PartyInventory) y los equipamientos individuales (CharacterEquipment).
 * Implementa KISS: servidor tiene autoridad, validaciones simples.
 */
public class InventoryManager {
    private static final Logger logger = LoggerFactory.getLogger(InventoryManager.class);
    
    private final PartyInventory partyInventory;
    private final Map<String, CharacterEquipment> playerEquipments;
    
    public InventoryManager(int partyInventoryCapacity) {
        this.partyInventory = new PartyInventory(partyInventoryCapacity);
        this.playerEquipments = new HashMap<>();
    }
    
    /**
     * Registra un jugador en el sistema de inventario.
     */
    public void registerPlayer(String playerId) {
        if (!playerEquipments.containsKey(playerId)) {
            playerEquipments.put(playerId, new CharacterEquipment(playerId));
            logger.info("Registered player {} in inventory system", playerId);
        }
    }
    
    /**
     * Remueve un jugador del sistema.
     */
    public void unregisterPlayer(String playerId) {
        CharacterEquipment equipment = playerEquipments.remove(playerId);
        if (equipment != null) {
            List<Item> items = equipment.unequipAll();
            partyInventory.addItems(items);
            logger.info("Unregistered player {} and returned items to party inventory", playerId);
        }
    }
    
    /**
     * Añade loot al inventario compartido.
     */
    public void addLootToParty(List<Item> loot) {
        partyInventory.addItems(loot);
    }
    
    /**
     * Jugador equipa un item del inventario compartido.
     */
    public boolean equipItemFromParty(String playerId, String itemId, EquipmentSlot slot) {
        CharacterEquipment equipment = playerEquipments.get(playerId);
        if (equipment == null) {
            logger.warn("Player {} not registered in inventory system", playerId);
            return false;
        }
        
        Optional<Item> itemOpt = partyInventory.getItem(itemId);
        if (itemOpt.isEmpty()) {
            logger.warn("Item {} not found in party inventory", itemId);
            return false;
        }
        
        Item item = itemOpt.get();
        
        // Remover del inventario compartido
        if (!partyInventory.removeItem(itemId)) {
            return false;
        }
        
        // Equipar item
        Optional<Item> previousItem = equipment.equipItem(slot, item);
        
        // Si había item previo, devolverlo al inventario compartido
        previousItem.ifPresent(partyInventory::addItem);
        
        logger.info("Player {} equipped {} from party inventory", playerId, item.getName());
        return true;
    }
    
    /**
     * Jugador desequipa un item y lo devuelve al inventario compartido.
     */
    public boolean unequipItemToParty(String playerId, EquipmentSlot slot) {
        CharacterEquipment equipment = playerEquipments.get(playerId);
        if (equipment == null) {
            return false;
        }
        
        Optional<Item> itemOpt = equipment.unequipItem(slot);
        if (itemOpt.isEmpty()) {
            return false;
        }
        
        Item item = itemOpt.get();
        boolean added = partyInventory.addItem(item);
        
        if (!added) {
            // Si no cabe en inventario, re-equipar
            equipment.equipItem(slot, item);
            logger.warn("Party inventory full, cannot unequip item");
            return false;
        }
        
        logger.info("Player {} unequipped {} to party inventory", playerId, item.getName());
        return true;
    }
    
    /**
     * Obtiene el inventario compartido.
     */
    public PartyInventory getPartyInventory() {
        return partyInventory;
    }
    
    /**
     * Obtiene el equipamiento de un jugador.
     */
    public Optional<CharacterEquipment> getPlayerEquipment(String playerId) {
        return Optional.ofNullable(playerEquipments.get(playerId));
    }
    
    /**
     * Usa un consumible del inventario compartido.
     */
    public boolean useConsumable(String itemId, String targetPlayerId) {
        Optional<Item> itemOpt = partyInventory.getItem(itemId);
        if (itemOpt.isEmpty()) {
            return false;
        }
        
        Item item = itemOpt.get();
        if (!item.isConsumable()) {
            logger.warn("Item {} is not consumable", item.getName());
            return false;
        }
        
        // Remover del inventario
        partyInventory.removeItem(itemId);
        logger.info("Used consumable {} on player {}", item.getName(), targetPlayerId);
        return true;
    }
    
    /**
     * Compra un item con recursos del grupo.
     */
    public boolean purchaseItem(Item item, int cost, String resourceType) {
        if (!partyInventory.consumeResource(resourceType, cost)) {
            logger.warn("Not enough {} to purchase {}", resourceType, item.getName());
            return false;
        }
        
        boolean added = partyInventory.addItem(item);
        if (!added) {
            // Devolver recursos si no se pudo añadir
            partyInventory.addResource(resourceType, cost);
            return false;
        }
        
        logger.info("Purchased {} for {} {}", item.getName(), cost, resourceType);
        return true;
    }
    
    /**
     * Obtiene resumen del estado del inventario.
     */
    public InventoryState getInventoryState() {
        InventoryState state = new InventoryState();
        state.partyItems = partyInventory.getAllItems();
        state.partyResources = partyInventory.getAllResources();
        state.capacity = partyInventory.getCurrentCapacity();
        state.maxCapacity = partyInventory.getMaxCapacity();
        
        playerEquipments.forEach((playerId, equipment) -> {
            state.playerEquipments.put(playerId, equipment.getAllEquippedItems());
        });
        
        return state;
    }
    
    /**
     * Clase para transportar estado del inventario.
     */
    public static class InventoryState {
        public List<Item> partyItems = new ArrayList<>();
        public Map<String, Integer> partyResources = new HashMap<>();
        public int capacity;
        public int maxCapacity;
        public Map<String, Map<EquipmentSlot, Item>> playerEquipments = new HashMap<>();
    }
}
