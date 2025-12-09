package com.juegito.game.character;

import com.juegito.game.loot.Item;

import java.util.*;

/**
 * Equipamiento inicial por clase.
 * Cada clase comienza con un set predeterminado que refleja su rol.
 */
public class InitialEquipment {
    
    /**
     * Obtiene el equipamiento inicial completo de una clase.
     */
    public static List<Item> getInitialEquipment(PlayerClass playerClass) {
        switch (playerClass) {
            case GUARDIAN:
                return getGuardianEquipment();
            case RANGER:
                return getRangerEquipment();
            case MAGE:
                return getMageEquipment();
            case CLERIC:
                return getClericEquipment();
            case ROGUE:
                return getRogueEquipment();
            default:
                return Collections.emptyList();
        }
    }
    
    private static List<Item> getGuardianEquipment() {
        return Arrays.asList(
            new Item("escudo_simple", "Escudo simple", 
                "Escudo de madera reforzada. +3 Defensa",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON, 
                Map.of("defense", 3)),
            new Item("armadura_ligera", "Armadura ligera",
                "Cota de malla básica. +5 Defensa",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON,
                Map.of("defense", 5)),
            new Item("maza_basica", "Maza básica",
                "Maza de hierro pesada. +8 Ataque",
                Item.ItemType.WEAPON, Item.ItemRarity.COMMON,
                Map.of("attack", 8))
        );
    }
    
    private static List<Item> getRangerEquipment() {
        return Arrays.asList(
            new Item("arco_corto", "Arco corto",
                "Arco de caza simple. +10 Ataque, Rango 4",
                Item.ItemType.WEAPON, Item.ItemRarity.COMMON,
                Map.of("attack", 10, "range", 4)),
            new Item("capucha_ligera", "Capucha ligera",
                "Capucha de cuero. +2 Defensa, +5% Evasión",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON,
                Map.of("defense", 2, "evasion", 5)),
            new Item("flechas_comunes", "Flechas comunes",
                "Carcaj con 20 flechas. Munición básica",
                Item.ItemType.RESOURCE, Item.ItemRarity.COMMON,
                Map.of("ammo", 20))
        );
    }
    
    private static List<Item> getMageEquipment() {
        return Arrays.asList(
            new Item("baston_rustico", "Bastón rústico",
                "Bastón de madera tallada. +12 Ataque mágico",
                Item.ItemType.WEAPON, Item.ItemRarity.COMMON,
                Map.of("attack", 12, "magic", 5)),
            new Item("tunica_simple", "Túnica simple",
                "Túnica de aprendiz. +3 Defensa, +10 Maná",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON,
                Map.of("defense", 3, "mana", 10)),
            new Item("grimorio_basico", "Grimorio básico",
                "Libro de conjuros iniciales. +1 ranura de hechizo",
                Item.ItemType.RESOURCE, Item.ItemRarity.COMMON,
                Map.of("spell_slots", 1))
        );
    }
    
    private static List<Item> getClericEquipment() {
        return Arrays.asList(
            new Item("cetro_pequeno", "Cetro pequeño",
                "Cetro sagrado. +8 Ataque, +8 Curación",
                Item.ItemType.WEAPON, Item.ItemRarity.COMMON,
                Map.of("attack", 8, "healing", 8)),
            new Item("escudo_madera", "Escudo de madera",
                "Escudo simple con símbolo sagrado. +3 Defensa",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON,
                Map.of("defense", 3)),
            new Item("vestimenta_bendecida", "Vestimenta bendecida",
                "Túnica con bendición menor. +4 Defensa, +5 HP",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON,
                Map.of("defense", 4, "hp", 5))
        );
    }
    
    private static List<Item> getRogueEquipment() {
        return Arrays.asList(
            new Item("dagas_simples", "Dagas simples",
                "Par de dagas afiladas. +12 Ataque, +10% Crítico",
                Item.ItemType.WEAPON, Item.ItemRarity.COMMON,
                Map.of("attack", 12, "crit", 10)),
            new Item("guantes_agiles", "Guantes ágiles",
                "Guantes de cuero fino. +1 Defensa, +1 Velocidad",
                Item.ItemType.ARMOR, Item.ItemRarity.COMMON,
                Map.of("defense", 1, "speed", 1)),
            new Item("kit_trampas_basico", "Kit de trampas básico",
                "Contiene 3 trampas básicas. Consumible",
                Item.ItemType.RESOURCE, Item.ItemRarity.COMMON,
                Map.of("traps", 3))
        );
    }
    
    /**
     * Calcula stats totales considerando equipamiento.
     */
    public static Map<String, Integer> calculateTotalStats(PlayerClass playerClass, List<Item> equipment) {
        Map<String, Integer> stats = new HashMap<>();
        
        // Stats base de la clase
        PlayerClass.BaseStats baseStats = playerClass.getBaseStats();
        stats.put("hp", baseStats.getHp());
        stats.put("defense", baseStats.getDefense());
        stats.put("attack", baseStats.getAttack());
        stats.put("speed", baseStats.getSpeed());
        
        // Sumar bonuses del equipamiento
        for (Item item : equipment) {
            Map<String, Object> effects = item.getEffects();
            if (effects != null) {
                effects.forEach((key, value) -> {
                    if (value instanceof Number) {
                        stats.merge(key, ((Number) value).intValue(), Integer::sum);
                    }
                });
            }
        }
        
        return stats;
    }
}
