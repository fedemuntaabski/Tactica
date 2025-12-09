package com.juegito.game.loot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sistema de loot y reparto de objetos.
 * El servidor controla la generación y distribución de loot.
 * Implementa KISS: reglas simples de generación y reparto.
 */
public class LootSystem {
    private static final Logger logger = LoggerFactory.getLogger(LootSystem.class);
    
    private final Map<String, Item> itemDatabase;
    private final Random random;
    
    public LootSystem() {
        this.itemDatabase = new HashMap<>();
        this.random = new Random();
        initializeItems();
    }
    
    public LootSystem(long seed) {
        this.itemDatabase = new HashMap<>();
        this.random = new Random(seed);
        initializeItems();
    }
    
    /**
     * Inicializa el catálogo de items disponibles.
     * Implementa DRY: definición centralizada de items.
     */
    private void initializeItems() {
        // Pociones
        addItem(new Item("health_potion_small", "Poción de Salud Menor", 
            "Restaura 20 HP", Item.ItemType.POTION, Item.ItemRarity.COMMON, 20, true));
        
        addItem(new Item("health_potion_medium", "Poción de Salud", 
            "Restaura 50 HP", Item.ItemType.POTION, Item.ItemRarity.UNCOMMON, 50, true));
        
        addItem(new Item("health_potion_large", "Poción de Salud Mayor", 
            "Restaura 100 HP", Item.ItemType.POTION, Item.ItemRarity.RARE, 100, true));
        
        // Pergaminos
        addItem(new Item("scroll_fireball", "Pergamino: Bola de Fuego", 
            "Lanza una bola de fuego (25 de daño)", 
            Item.ItemType.SCROLL, Item.ItemRarity.UNCOMMON, 25, true));
        
        addItem(new Item("scroll_teleport", "Pergamino: Teletransporte", 
            "Teletransporta a una casilla visible", 
            Item.ItemType.SCROLL, Item.ItemRarity.RARE, 0, true));
        
        // Armas
        addItem(new Item("iron_sword", "Espada de Hierro", 
            "+5 de daño base", Item.ItemType.WEAPON, Item.ItemRarity.COMMON, 5, false));
        
        addItem(new Item("steel_bow", "Arco de Acero", 
            "+8 de daño a distancia", Item.ItemType.WEAPON, Item.ItemRarity.UNCOMMON, 8, false));
        
        // Armaduras
        addItem(new Item("leather_armor", "Armadura de Cuero", 
            "+2 de defensa", Item.ItemType.ARMOR, Item.ItemRarity.COMMON, 2, false));
        
        addItem(new Item("chainmail", "Cota de Malla", 
            "+4 de defensa", Item.ItemType.ARMOR, Item.ItemRarity.UNCOMMON, 4, false));
        
        // Recursos
        addItem(new Item("gold_coin", "Moneda de Oro", 
            "Moneda de oro (10 unidades)", Item.ItemType.RESOURCE, Item.ItemRarity.COMMON, 10, false));
        
        logger.info("Initialized {} items in database", itemDatabase.size());
    }
    
    private void addItem(Item item) {
        itemDatabase.put(item.getId(), item);
    }
    
    /**
     * Genera loot basado en una fuente (enemigo, cofre, evento).
     * Implementa KISS: probabilidades simples basadas en rareza.
     */
    public List<Item> generateLoot(LootSource source, int quantity) {
        List<Item> loot = new ArrayList<>();
        
        for (int i = 0; i < quantity; i++) {
            Item item = generateSingleItem(source);
            if (item != null) {
                loot.add(item);
            }
        }
        
        logger.info("Generated {} items from {}", loot.size(), source);
        return loot;
    }
    
    private Item generateSingleItem(LootSource source) {
        // Determinar rareza basada en la fuente
        Item.ItemRarity targetRarity = determineRarity(source);
        
        // Obtener items de esa rareza
        List<Item> candidates = new ArrayList<>();
        for (Item item : itemDatabase.values()) {
            if (item.getRarity() == targetRarity) {
                candidates.add(item);
            }
        }
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Seleccionar item aleatorio
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    private Item.ItemRarity determineRarity(LootSource source) {
        double roll = random.nextDouble();
        
        switch (source) {
            case ENEMY_COMMON:
                // 70% común, 25% poco común, 5% raro
                if (roll < 0.70) return Item.ItemRarity.COMMON;
                if (roll < 0.95) return Item.ItemRarity.UNCOMMON;
                return Item.ItemRarity.RARE;
                
            case ENEMY_ELITE:
                // 40% poco común, 40% raro, 15% épico, 5% legendario
                if (roll < 0.40) return Item.ItemRarity.UNCOMMON;
                if (roll < 0.80) return Item.ItemRarity.RARE;
                if (roll < 0.95) return Item.ItemRarity.EPIC;
                return Item.ItemRarity.LEGENDARY;
                
            case CHEST:
                // 50% común, 30% poco común, 15% raro, 5% épico
                if (roll < 0.50) return Item.ItemRarity.COMMON;
                if (roll < 0.80) return Item.ItemRarity.UNCOMMON;
                if (roll < 0.95) return Item.ItemRarity.RARE;
                return Item.ItemRarity.EPIC;
                
            case EVENT:
                // 60% común, 30% poco común, 10% raro
                if (roll < 0.60) return Item.ItemRarity.COMMON;
                if (roll < 0.90) return Item.ItemRarity.UNCOMMON;
                return Item.ItemRarity.RARE;
                
            default:
                return Item.ItemRarity.COMMON;
        }
    }
    
    /**
     * Distribuye loot entre jugadores según la regla especificada.
     * Implementa KISS: tres reglas simples de distribución.
     */
    public Map<String, List<Item>> distributeLoot(List<String> playerIds, List<Item> loot, 
                                                  DistributionRule rule) {
        Map<String, List<Item>> distribution = new HashMap<>();
        
        // Inicializar listas vacías para cada jugador
        for (String playerId : playerIds) {
            distribution.put(playerId, new ArrayList<>());
        }
        
        if (loot.isEmpty() || playerIds.isEmpty()) {
            return distribution;
        }
        
        switch (rule) {
            case ROUND_ROBIN:
                distributeRoundRobin(playerIds, loot, distribution);
                break;
                
            case RANDOM:
                distributeRandom(playerIds, loot, distribution);
                break;
                
            case ALL_GET_COPY:
                distributeAllGetCopy(playerIds, loot, distribution);
                break;
        }
        
        logger.info("Distributed {} items to {} players using {}", 
            loot.size(), playerIds.size(), rule);
        return distribution;
    }
    
    private void distributeRoundRobin(List<String> players, List<Item> loot, 
                                     Map<String, List<Item>> distribution) {
        for (int i = 0; i < loot.size(); i++) {
            String playerId = players.get(i % players.size());
            distribution.get(playerId).add(loot.get(i));
        }
    }
    
    private void distributeRandom(List<String> players, List<Item> loot,
                                 Map<String, List<Item>> distribution) {
        for (Item item : loot) {
            String randomPlayer = players.get(random.nextInt(players.size()));
            distribution.get(randomPlayer).add(item);
        }
    }
    
    private void distributeAllGetCopy(List<String> players, List<Item> loot,
                                     Map<String, List<Item>> distribution) {
        for (String playerId : players) {
            distribution.get(playerId).addAll(new ArrayList<>(loot));
        }
    }
    
    public Item getItem(String itemId) {
        return itemDatabase.get(itemId);
    }
    
    /**
     * Fuentes de loot disponibles.
     */
    public enum LootSource {
        ENEMY_COMMON,    // Enemigo común
        ENEMY_ELITE,     // Enemigo élite/jefe
        CHEST,           // Cofre
        EVENT            // Evento aleatorio
    }
    
    /**
     * Reglas de distribución de loot.
     */
    public enum DistributionRule {
        ROUND_ROBIN,     // Por turnos (1ro al jugador 1, 2do al jugador 2, etc)
        RANDOM,          // Aleatorio a cualquier jugador
        ALL_GET_COPY     // Todos reciben copia del mismo loot
    }
}
