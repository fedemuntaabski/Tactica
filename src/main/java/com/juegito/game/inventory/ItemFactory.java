package com.juegito.game.inventory;

import com.juegito.game.loot.Item;

import java.util.*;

/**
 * Factory para crear los 9 tipos de items del sistema.
 * Implementa DRY: creación centralizada de items con efectos predefinidos.
 */
public class ItemFactory {
    
    // ==================== 1. ARMAS (WEAPONS) ====================
    
    public static Item createSword(String id, String name, int baseDamage, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.WEAPON);
        effects.put("equipSlot", EquipmentSlot.WEAPON);
        effects.put("weaponType", WeaponType.SWORD);
        effects.put("attack", baseDamage);
        effects.put("damageType", DamageType.SLASH);
        effects.put("range", 1);
        
        return new Item(id, name, "Espada de combate cuerpo a cuerpo", 
                Item.ItemType.WEAPON, rarity, effects);
    }
    
    public static Item createBow(String id, String name, int baseDamage, int range, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.WEAPON);
        effects.put("equipSlot", EquipmentSlot.WEAPON);
        effects.put("weaponType", WeaponType.BOW);
        effects.put("attack", baseDamage);
        effects.put("damageType", DamageType.PIERCE);
        effects.put("range", range);
        effects.put("speedModifier", 0.8);
        
        return new Item(id, name, "Arco para ataques a distancia", 
                Item.ItemType.WEAPON, rarity, effects);
    }
    
    public static Item createStaff(String id, String name, int magicPower, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.WEAPON);
        effects.put("equipSlot", EquipmentSlot.WEAPON);
        effects.put("weaponType", WeaponType.STAFF);
        effects.put("attack", magicPower);
        effects.put("damageType", DamageType.MAGIC);
        effects.put("range", 2);
        effects.put("magic", 5);
        
        return new Item(id, name, "Bastón de poder mágico", 
                Item.ItemType.WEAPON, rarity, effects);
    }
    
    // ==================== 2. ARMADURAS (ARMOR) ====================
    
    public static Item createHelmet(String id, String name, int defense, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.ARMOR);
        effects.put("equipSlot", EquipmentSlot.HELMET);
        effects.put("defense", defense);
        
        return new Item(id, name, "Casco protector", 
                Item.ItemType.ARMOR, rarity, effects);
    }
    
    public static Item createChestArmor(String id, String name, int defense, int hp, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.ARMOR);
        effects.put("equipSlot", EquipmentSlot.CHEST);
        effects.put("defense", defense);
        effects.put("hp", hp);
        
        return new Item(id, name, "Armadura de pecho", 
                Item.ItemType.ARMOR, rarity, effects);
    }
    
    public static Item createShield(String id, String name, int defense, int blockChance, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.ARMOR);
        effects.put("equipSlot", EquipmentSlot.SHIELD);
        effects.put("defense", defense);
        effects.put("blockChance", blockChance);
        
        return new Item(id, name, "Escudo para bloquear ataques", 
                Item.ItemType.ARMOR, rarity, effects);
    }
    
    // ==================== 3. ACCESORIOS (ACCESSORIES) ====================
    
    public static Item createRing(String id, String name, Map<String, Object> bonuses, Item.ItemRarity rarity) {
        bonuses.put("category", ItemCategory.ACCESSORY);
        bonuses.put("equipSlot", Arrays.asList(EquipmentSlot.RING_1, EquipmentSlot.RING_2));
        
        return new Item(id, name, "Anillo con efectos pasivos", 
                Item.ItemType.ARMOR, rarity, bonuses);
    }
    
    public static Item createAmulet(String id, String name, Map<String, Object> bonuses, Item.ItemRarity rarity) {
        bonuses.put("category", ItemCategory.ACCESSORY);
        bonuses.put("equipSlot", EquipmentSlot.AMULET);
        
        return new Item(id, name, "Amuleto con poder especial", 
                Item.ItemType.ARMOR, rarity, bonuses);
    }
    
    // ==================== 4. CONSUMIBLES (CONSUMABLES) ====================
    
    public static Item createHealthPotion(String id, int healAmount, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.CONSUMABLE);
        effects.put("healing", healAmount);
        
        return new Item(id, "Poción de Salud", "Restaura " + healAmount + " HP", 
                Item.ItemType.POTION, rarity, effects);
    }
    
    public static Item createAntidote(String id, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.CONSUMABLE);
        effects.put("curePoison", true);
        
        return new Item(id, "Antídoto", "Cura envenenamiento", 
                Item.ItemType.POTION, rarity, effects);
    }
    
    public static Item createBomb(String id, int damage, int aoe, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.CONSUMABLE);
        effects.put("damage", damage);
        effects.put("aoeRadius", aoe);
        
        return new Item(id, "Bomba", "Daño en área: " + damage, 
                Item.ItemType.POTION, rarity, effects);
    }
    
    // ==================== 5. ITEMS DE HABILIDAD (SKILL_ITEMS) ====================
    
    public static Item createSkillBook(String id, String skillName, String abilityId, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.SKILL_ITEM);
        effects.put("grantsAbility", abilityId);
        effects.put("duration", "run"); // Durante la run completa
        
        return new Item(id, skillName, "Desbloquea habilidad temporal: " + abilityId, 
                Item.ItemType.SCROLL, rarity, effects);
    }
    
    // ==================== 6. MATERIALES DE BUFF (BUFF_MATERIALS) ====================
    
    public static Item createWhetstone(String id, int attackBonus, int duration, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.BUFF_MATERIAL);
        effects.put("attackBuff", attackBonus);
        effects.put("duration", duration);
        
        return new Item(id, "Piedra de Afilar", "+" + attackBonus + " ATK por " + duration + " turnos", 
                Item.ItemType.POTION, rarity, effects);
    }
    
    public static Item createVigorTea(String id, int speedBonus, int duration, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.BUFF_MATERIAL);
        effects.put("speedBuff", speedBonus);
        effects.put("duration", duration);
        
        return new Item(id, "Té Vigorizante", "+" + speedBonus + "% velocidad por " + duration + " turnos", 
                Item.ItemType.POTION, rarity, effects);
    }
    
    // ==================== 7. LOOT DE EVENTO (EVENT_LOOT) ====================
    
    public static Item createKey(String id, String doorId, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.EVENT_LOOT);
        effects.put("unlocks", doorId);
        
        return new Item(id, "Llave Especial", "Abre: " + doorId, 
                Item.ItemType.RESOURCE, rarity, effects);
    }
    
    public static Item createRelic(String id, String name, Map<String, Object> bonuses, 
                                   Map<String, Object> penalties, Item.ItemRarity rarity) {
        bonuses.put("category", ItemCategory.EVENT_LOOT);
        if (penalties != null) {
            bonuses.put("cursedPenalties", penalties);
        }
        
        return new Item(id, name, "Reliquia con poder especial", 
                Item.ItemType.ARMOR, rarity, bonuses);
    }
    
    // ==================== 8. RECURSOS DEL GRUPO (PARTY_RESOURCES) ====================
    
    public static Item createGold(String id, int amount) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.PARTY_RESOURCE);
        effects.put("gold", amount);
        
        return new Item(id, "Oro", amount + " monedas de oro", 
                Item.ItemType.RESOURCE, Item.ItemRarity.COMMON, effects);
    }
    
    // ==================== 9. ITEMS DE SINERGIA (SYNERGY_ITEMS) ====================
    
    public static Item createGuardAmulet(String id, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.SYNERGY_ITEM);
        effects.put("equipSlot", EquipmentSlot.AMULET);
        effects.put("synergyType", "nearTank");
        effects.put("damageReduction", 15);
        effects.put("description", "Si estás cerca del tanque, recibís -15% daño");
        
        return new Item(id, "Amuleto de Guardia", "Sinergia con tanque", 
                Item.ItemType.ARMOR, rarity, effects);
    }
    
    public static Item createHunterGloves(String id, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.SYNERGY_ITEM);
        effects.put("equipSlot", EquipmentSlot.AMULET);
        effects.put("synergyType", "markedEnemies");
        effects.put("damageBonus", 30);
        effects.put("description", "+30% daño a enemigos marcados");
        
        return new Item(id, "Guantes del Cazador", "Sinergia con marcado", 
                Item.ItemType.ARMOR, rarity, effects);
    }
    
    public static Item createArcanistTome(String id, Item.ItemRarity rarity) {
        Map<String, Object> effects = new HashMap<>();
        effects.put("category", ItemCategory.SYNERGY_ITEM);
        effects.put("equipSlot", EquipmentSlot.AMULET);
        effects.put("synergyType", "magicEffects");
        effects.put("durationBonus", 1);
        effects.put("description", "+1 turno de duración a estados mágicos");
        
        return new Item(id, "Tomo Arcanista", "Sinergia con magia", 
                Item.ItemType.ARMOR, rarity, effects);
    }
}
