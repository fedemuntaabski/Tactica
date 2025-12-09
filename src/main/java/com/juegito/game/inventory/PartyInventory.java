package com.juegito.game.inventory;

import com.juegito.game.loot.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Inventario global compartido por todo el grupo.
 * Todos los jugadores pueden ver y tomar items.
 * Los items no están "poseídos" hasta que se equipan.
 * Implementa KISS: una lista simple con operaciones básicas.
 */
public class PartyInventory {
    private static final Logger logger = LoggerFactory.getLogger(PartyInventory.class);
    
    private final List<Item> items;
    private final Map<String, Integer> resources; // Oro, fichas, etc.
    private final int maxCapacity;
    
    public PartyInventory(int maxCapacity) {
        this.items = Collections.synchronizedList(new ArrayList<>());
        this.resources = new ConcurrentHashMap<>();
        this.maxCapacity = maxCapacity;
        this.resources.put("gold", 0);
    }
    
    /**
     * Añade un item al inventario compartido.
     */
    public boolean addItem(Item item) {
        if (items.size() >= maxCapacity) {
            logger.warn("Party inventory full, cannot add item: {}", item.getName());
            return false;
        }
        
        items.add(item);
        logger.info("Added item to party inventory: {}", item.getName());
        return true;
    }
    
    /**
     * Añade múltiples items (loot drop).
     */
    public void addItems(List<Item> newItems) {
        for (Item item : newItems) {
            if (!addItem(item)) {
                logger.warn("Some items could not be added due to capacity");
                break;
            }
        }
    }
    
    /**
     * Remueve un item del inventario (cuando se equipa).
     */
    public boolean removeItem(String itemId) {
        boolean removed = items.removeIf(item -> item.getId().equals(itemId));
        if (removed) {
            logger.info("Removed item from party inventory: {}", itemId);
        }
        return removed;
    }
    
    /**
     * Obtiene un item específico por ID.
     */
    public Optional<Item> getItem(String itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }
    
    /**
     * Lista todos los items del inventario compartido.
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }
    
    /**
     * Filtra items por categoría.
     */
    public List<Item> getItemsByCategory(ItemCategory category) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.hasEffect("category") && item.getEffect("category").equals(category)) {
                result.add(item);
            }
        }
        return result;
    }
    
    /**
     * Añade recursos (oro, fichas).
     */
    public void addResource(String resourceType, int amount) {
        resources.merge(resourceType, amount, Integer::sum);
        logger.info("Added {} {} to party resources", amount, resourceType);
    }
    
    /**
     * Consume recursos si hay suficientes.
     */
    public boolean consumeResource(String resourceType, int amount) {
        int current = resources.getOrDefault(resourceType, 0);
        if (current >= amount) {
            resources.put(resourceType, current - amount);
            logger.info("Consumed {} {} from party resources", amount, resourceType);
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene cantidad de un recurso.
     */
    public int getResource(String resourceType) {
        return resources.getOrDefault(resourceType, 0);
    }
    
    /**
     * Obtiene todos los recursos.
     */
    public Map<String, Integer> getAllResources() {
        return new HashMap<>(resources);
    }
    
    /**
     * Capacidad actual vs máxima.
     */
    public int getCurrentCapacity() {
        return items.size();
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public boolean isFull() {
        return items.size() >= maxCapacity;
    }
    
    /**
     * Limpia el inventario (para testing o reset).
     */
    public void clear() {
        items.clear();
        resources.clear();
        resources.put("gold", 0);
        logger.info("Party inventory cleared");
    }
}
