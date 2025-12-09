package com.juegito.game.loot;

import com.juegito.game.character.PlayerClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sistema de generación de loot con afinidad por clase.
 * Inspirado en For The King: loot aleatorio pero con peso hacia el tipo de equipo de cada clase.
 * El loot es compartido, pero cada clase tiene más probabilidad de encontrar su equipo.
 */
public class ClassBasedLootGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ClassBasedLootGenerator.class);
    
    private final Random random;
    private static final double CLASS_AFFINITY_BONUS = 0.4; // 40% más probabilidad de su tipo
    
    public ClassBasedLootGenerator() {
        this.random = new Random();
    }
    
    public ClassBasedLootGenerator(long seed) {
        this.random = new Random(seed);
    }
    
    /**
     * Genera loot considerando las clases presentes en el grupo.
     * Aumenta probabilidad de items relevantes para las clases actuales.
     */
    public List<Item> generateLootForParty(List<PlayerClass> partyClasses, 
                                           LootSystem.LootSource source, 
                                           int quantity) {
        List<Item> loot = new ArrayList<>();
        
        for (int i = 0; i < quantity; i++) {
            // Seleccionar una clase aleatoria del grupo para sesgar el loot
            PlayerClass targetClass = partyClasses.get(random.nextInt(partyClasses.size()));
            Item item = generateItemForClass(targetClass, source);
            if (item != null) {
                loot.add(item);
            }
        }
        
        logger.info("Generated {} items for party with affinity", loot.size());
        return loot;
    }
    
    /**
     * Genera un item con afinidad hacia una clase específica.
     */
    private Item generateItemForClass(PlayerClass playerClass, LootSystem.LootSource source) {
        double roll = random.nextDouble();
        
        // 60% de probabilidad de generar item afín a la clase
        // 40% de probabilidad de item genérico
        if (roll < 0.6) {
            return generateClassSpecificItem(playerClass, source);
        } else {
            return generateGenericItem(source);
        }
    }
    
    /**
     * Genera un item específico para una clase.
     */
    private Item generateClassSpecificItem(PlayerClass playerClass, LootSystem.LootSource source) {
        Item.ItemRarity rarity = determineRarity(source);
        
        switch (playerClass) {
            case GUARDIAN:
                return generateGuardianItem(rarity);
            case RANGER:
                return generateRangerItem(rarity);
            case MAGE:
                return generateMageItem(rarity);
            case CLERIC:
                return generateClericItem(rarity);
            case ROGUE:
                return generateRogueItem(rarity);
            default:
                return generateGenericItem(source);
        }
    }
    
    private Item generateGuardianItem(Item.ItemRarity rarity) {
        String[] itemIds = {"heavy_armor", "tower_shield", "mace", "mitigation_ring"};
        String[] names = {"Armadura Pesada", "Escudo Torre", "Maza de Guerra", "Anillo de Mitigación"};
        String[] descs = {"+8 Defensa, +10 HP", "+10 Defensa, Bloqueo +15%", 
                         "+12 Ataque, Aturdir 10%", "+5 Defensa, -10% daño recibido"};
        
        int idx = random.nextInt(itemIds.length);
        Map<String, Object> effects = new HashMap<>();
        
        switch (idx) {
            case 0: effects.put("defense", 8); effects.put("hp", 10); break;
            case 1: effects.put("defense", 10); effects.put("block", 15); break;
            case 2: effects.put("attack", 12); effects.put("stun_chance", 10); break;
            case 3: effects.put("defense", 5); effects.put("damage_reduction", 10); break;
        }
        
        return new Item(itemIds[idx] + "_" + rarity, names[idx], descs[idx],
                       Item.ItemType.ARMOR, rarity, effects);
    }
    
    private Item generateRangerItem(Item.ItemRarity rarity) {
        String[] itemIds = {"longbow", "crossbow", "stealth_cloak", "crit_amulet"};
        String[] names = {"Arco Largo", "Ballesta", "Capa de Sigilo", "Amuleto Crítico"};
        String[] descs = {"+15 Ataque, Rango 5", "+18 Ataque, Rango 4, Perforación", 
                         "+3 Defensa, +10% Evasión", "+5 Ataque, +15% Crítico"};
        
        int idx = random.nextInt(itemIds.length);
        Map<String, Object> effects = new HashMap<>();
        
        switch (idx) {
            case 0: effects.put("attack", 15); effects.put("range", 5); break;
            case 1: effects.put("attack", 18); effects.put("range", 4); effects.put("piercing", true); break;
            case 2: effects.put("defense", 3); effects.put("evasion", 10); break;
            case 3: effects.put("attack", 5); effects.put("crit", 15); break;
        }
        
        return new Item(itemIds[idx] + "_" + rarity, names[idx], descs[idx],
                       Item.ItemType.WEAPON, rarity, effects);
    }
    
    private Item generateMageItem(Item.ItemRarity rarity) {
        String[] itemIds = {"arcane_staff", "elemental_tome", "mana_crystal", "wizard_hat"};
        String[] names = {"Bastón Arcano", "Tomo Elemental", "Cristal de Maná", "Sombrero de Mago"};
        String[] descs = {"+18 Ataque mágico, +10 Maná", "Añade elemento: Fuego/Hielo/Rayo", 
                         "+20 Maná, Regeneración +2/turno", "+5 Defensa, +15 Maná, +1 Hechizo"};
        
        int idx = random.nextInt(itemIds.length);
        Map<String, Object> effects = new HashMap<>();
        
        switch (idx) {
            case 0: effects.put("attack", 18); effects.put("mana", 10); break;
            case 1: effects.put("element", random.nextInt(3)); break; // 0=fuego, 1=hielo, 2=rayo
            case 2: effects.put("mana", 20); effects.put("mana_regen", 2); break;
            case 3: effects.put("defense", 5); effects.put("mana", 15); effects.put("spell_slots", 1); break;
        }
        
        return new Item(itemIds[idx] + "_" + rarity, names[idx], descs[idx],
                       Item.ItemType.WEAPON, rarity, effects);
    }
    
    private Item generateClericItem(Item.ItemRarity rarity) {
        String[] itemIds = {"holy_staff", "sacred_relic", "healing_vestment", "blessing_tome"};
        String[] names = {"Cetro Sagrado", "Reliquia Sagrada", "Vestimenta Sanadora", "Tomo de Bendiciones"};
        String[] descs = {"+10 Ataque, +12 Curación", "+15 Curación, Revivir caído", 
                         "+6 Defensa, +10 HP, Regeneración +3", "+8 Curación, Bendición dura +1 turno"};
        
        int idx = random.nextInt(itemIds.length);
        Map<String, Object> effects = new HashMap<>();
        
        switch (idx) {
            case 0: effects.put("attack", 10); effects.put("healing", 12); break;
            case 1: effects.put("healing", 15); effects.put("revive", true); break;
            case 2: effects.put("defense", 6); effects.put("hp", 10); effects.put("regen", 3); break;
            case 3: effects.put("healing", 8); effects.put("buff_duration", 1); break;
        }
        
        return new Item(itemIds[idx] + "_" + rarity, names[idx], descs[idx],
                       Item.ItemType.ARMOR, rarity, effects);
    }
    
    private Item generateRogueItem(Item.ItemRarity rarity) {
        String[] itemIds = {"poison_dagger", "shadow_cloak", "trap_kit_advanced", "thief_ring"};
        String[] names = {"Daga Venenosa", "Capa de Sombras", "Kit de Trampas Avanzado", "Anillo Ladrón"};
        String[] descs = {"+15 Ataque, +20% Crítico, Veneno 5 daño/turno", "+4 Defensa, +15% Evasión, Sigilo", 
                         "5 trampas mejoradas", "+8 Ataque, +1 Velocidad, +10% robo"};
        
        int idx = random.nextInt(itemIds.length);
        Map<String, Object> effects = new HashMap<>();
        
        switch (idx) {
            case 0: effects.put("attack", 15); effects.put("crit", 20); effects.put("poison", 5); break;
            case 1: effects.put("defense", 4); effects.put("evasion", 15); effects.put("stealth", true); break;
            case 2: effects.put("traps", 5); effects.put("improved", true); break;
            case 3: effects.put("attack", 8); effects.put("speed", 1); effects.put("steal", 10); break;
        }
        
        return new Item(itemIds[idx] + "_" + rarity, names[idx], descs[idx],
                       Item.ItemType.WEAPON, rarity, effects);
    }
    
    /**
     * Genera item genérico (pociones, pergaminos, recursos).
     */
    private Item generateGenericItem(LootSystem.LootSource source) {
        Item.ItemRarity rarity = determineRarity(source);
        
        String[] itemTypes = {"potion", "scroll", "resource"};
        String type = itemTypes[random.nextInt(itemTypes.length)];
        
        switch (type) {
            case "potion":
                return generatePotion(rarity);
            case "scroll":
                return generateScroll(rarity);
            case "resource":
                return generateResource(rarity);
            default:
                return generatePotion(rarity);
        }
    }
    
    private Item generatePotion(Item.ItemRarity rarity) {
        int healing = rarity == Item.ItemRarity.COMMON ? 20 : 
                     rarity == Item.ItemRarity.UNCOMMON ? 40 : 60;
        return new Item("health_potion_" + rarity, "Poción de Salud", 
                       "Restaura " + healing + " HP",
                       Item.ItemType.POTION, rarity, 
                       Map.of("healing", healing));
    }
    
    private Item generateScroll(Item.ItemRarity rarity) {
        String[] scrolls = {"fireball", "ice_blast", "lightning", "teleport"};
        String scroll = scrolls[random.nextInt(scrolls.length)];
        return new Item("scroll_" + scroll + "_" + rarity, "Pergamino: " + scroll,
                       "Uso único",
                       Item.ItemType.SCROLL, rarity,
                       Map.of("spell", scroll));
    }
    
    private Item generateResource(Item.ItemRarity rarity) {
        int amount = rarity == Item.ItemRarity.COMMON ? 10 : 
                    rarity == Item.ItemRarity.UNCOMMON ? 25 : 50;
        return new Item("gold_" + rarity, "Oro",
                       amount + " monedas",
                       Item.ItemType.RESOURCE, rarity,
                       Map.of("gold", amount));
    }
    
    private Item.ItemRarity determineRarity(LootSystem.LootSource source) {
        double roll = random.nextDouble();
        
        switch (source) {
            case ENEMY_COMMON:
                if (roll < 0.70) return Item.ItemRarity.COMMON;
                if (roll < 0.95) return Item.ItemRarity.UNCOMMON;
                return Item.ItemRarity.RARE;
                
            case ENEMY_ELITE:
                if (roll < 0.40) return Item.ItemRarity.UNCOMMON;
                if (roll < 0.80) return Item.ItemRarity.RARE;
                if (roll < 0.95) return Item.ItemRarity.EPIC;
                return Item.ItemRarity.LEGENDARY;
                
            case CHEST:
                if (roll < 0.50) return Item.ItemRarity.COMMON;
                if (roll < 0.80) return Item.ItemRarity.UNCOMMON;
                if (roll < 0.95) return Item.ItemRarity.RARE;
                return Item.ItemRarity.EPIC;
                
            case EVENT:
                if (roll < 0.60) return Item.ItemRarity.COMMON;
                if (roll < 0.90) return Item.ItemRarity.UNCOMMON;
                return Item.ItemRarity.RARE;
                
            default:
                return Item.ItemRarity.COMMON;
        }
    }
}
