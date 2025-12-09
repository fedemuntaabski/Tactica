package com.juegito.game.inventory;

/**
 * Categorías extendidas de items basadas en el sistema de For The King.
 */
public enum ItemCategory {
    // Equipables
    WEAPON,              // Espadas, hachas, arcos, bastones, etc.
    ARMOR,               // Cascos, pecho, botas, capas, escudos
    ACCESSORY,           // Anillos, amuletos, talismanes
    
    // Consumibles
    CONSUMABLE,          // Pociones, antídotos, bombas, flechas especiales
    
    // Especiales
    SKILL_ITEM,          // Items que desbloquean habilidades temporales
    BUFF_MATERIAL,       // Materiales que dan buffs temporales
    EVENT_LOOT,          // Loot único de eventos (claves, reliquias, malditos)
    PARTY_RESOURCE,      // Dinero, fichas, recursos compartidos
    SYNERGY_ITEM         // Items con efectos de sinergia entre clases
}
