package com.juegito.game.inventory;

import com.juegito.game.character.PlayerClass;
import com.juegito.game.loot.Item;
import com.juegito.game.loot.LootSystem;

import java.util.*;

/**
 * Utilidades para integrar el sistema de loot con el inventario compartido.
 * Facilita la generación y distribución automática de loot al grupo.
 */
public class LootInventoryBridge {
    
    private final LootSystem lootSystem;
    private final InventoryManager inventoryManager;
    
    public LootInventoryBridge(LootSystem lootSystem, InventoryManager inventoryManager) {
        this.lootSystem = lootSystem;
        this.inventoryManager = inventoryManager;
    }
    
    /**
     * Genera loot y lo añade automáticamente al inventario compartido.
     * Usa afinidad de clases del grupo actual.
     */
    public List<Item> generateAndDistributeLoot(LootSystem.LootSource source, 
                                                int quantity,
                                                List<PlayerClass> partyClasses) {
        // Generar loot con afinidad por las clases del grupo
        List<Item> generatedLoot = lootSystem.generateLootForParty(source, quantity, partyClasses);
        
        // Añadir al inventario compartido
        inventoryManager.addLootToParty(generatedLoot);
        
        return generatedLoot;
    }
    
    /**
     * Distribuye oro encontrado al inventario del grupo.
     */
    public void distributeGold(int amount) {
        Item goldItem = ItemFactory.createGold("gold_drop_" + System.currentTimeMillis(), amount);
        
        // Procesar como recurso directamente
        if (goldItem.hasEffect("gold")) {
            int goldAmount = (int) goldItem.getEffect("gold");
            inventoryManager.getPartyInventory().addResource("gold", goldAmount);
        }
    }
    
    /**
     * Distribuye loot de evento especial (reliquias, llaves, etc.).
     */
    public void distributeEventLoot(String eventId, List<Item> specialItems) {
        inventoryManager.addLootToParty(specialItems);
    }
    
    /**
     * Recompensa al grupo por completar combate.
     * Genera loot basado en dificultad del enemigo.
     */
    public Map<String, Object> rewardCombatVictory(String enemyType, 
                                                    List<PlayerClass> partyClasses) {
        Map<String, Object> rewards = new HashMap<>();
        
        LootSystem.LootSource source;
        int itemQuantity;
        int goldAmount;
        
        switch (enemyType.toLowerCase()) {
            case "elite":
            case "boss":
                source = LootSystem.LootSource.ENEMY_ELITE;
                itemQuantity = 3;
                goldAmount = 50;
                break;
            case "common":
            default:
                source = LootSystem.LootSource.ENEMY_COMMON;
                itemQuantity = 1;
                goldAmount = 10;
                break;
        }
        
        // Generar items
        List<Item> loot = generateAndDistributeLoot(source, itemQuantity, partyClasses);
        
        // Distribuir oro
        distributeGold(goldAmount);
        
        rewards.put("items", loot);
        rewards.put("gold", goldAmount);
        
        return rewards;
    }
    
    /**
     * Recompensa por abrir cofre.
     */
    public Map<String, Object> rewardChestOpened(List<PlayerClass> partyClasses) {
        Map<String, Object> rewards = new HashMap<>();
        
        List<Item> loot = generateAndDistributeLoot(LootSystem.LootSource.CHEST, 2, partyClasses);
        int goldAmount = 25;
        distributeGold(goldAmount);
        
        rewards.put("items", loot);
        rewards.put("gold", goldAmount);
        
        return rewards;
    }
    
    /**
     * Resumen del inventario actual del grupo.
     */
    public InventoryManager.InventoryState getInventorySnapshot() {
        return inventoryManager.getInventoryState();
    }
}
