package com.juegito.game.character;

import java.util.Arrays;
import java.util.List;

/**
 * Define las 5 clases jugables del juego.
 * Cada clase tiene stats base, equipamiento inicial y habilidades.
 * Inspirado en For The King: roles definidos, loot compartido, sinergias.
 */
public enum PlayerClass {
    GUARDIAN(
        "Guarda del Alba",
        "Tanque / Control",
        "Recibe daño, protege al grupo, controla posiciones",
        new BaseStats(100, 15, 10, 3), // HP, DEF, ATK, SPD
        Arrays.asList("Escudo simple", "Armadura ligera", "Maza básica"),
        "Muralla Viviente",
        Arrays.asList("Armaduras pesadas", "Escudos", "Armas de una mano", "Objetos de mitigación")
    ),
    
    RANGER(
        "Cazador Errante",
        "Rango / DPS crítico",
        "Daño a distancia, eliminación de amenazas, ventaja táctica",
        new BaseStats(60, 8, 25, 8), // HP, DEF, ATK, SPD
        Arrays.asList("Arco corto", "Capucha ligera", "Flechas comunes"),
        "Disparo Preciso",
        Arrays.asList("Arcos", "Ballestas", "Capas", "Objetos de crítico/evasión")
    ),
    
    MAGE(
        "Erudito Arcano",
        "Mago / Control / AOE",
        "Control del campo, daño en área, utilidades",
        new BaseStats(70, 8, 22, 5), // HP, DEF, ATK, SPD
        Arrays.asList("Bastón rústico", "Túnica simple", "Grimorio básico"),
        "Chispa Arcana",
        Arrays.asList("Bastones", "Amuletos", "Objetos de maná", "Afinidades elementales")
    ),
    
    CLERIC(
        "Clérigo del Sendero",
        "Soporte / Sanación / Bendiciones",
        "Curar, mitigar, buffear",
        new BaseStats(80, 12, 12, 5), // HP, DEF, ATK, SPD
        Arrays.asList("Cetro pequeño", "Escudo de madera", "Vestimenta bendecida"),
        "Rezo Curativo",
        Arrays.asList("Cetros", "Reliquias", "Armaduras sagradas", "Objetos de regeneración")
    ),
    
    ROGUE(
        "Pícaro Sombrío",
        "Sigilo / DPS híbrido / Trampas",
        "Daño burst, trampas, desestabilización",
        new BaseStats(65, 7, 28, 9), // HP, DEF, ATK, SPD
        Arrays.asList("Dagas simples", "Guantes ágiles", "Kit de trampas básico"),
        "Golpe Sombra",
        Arrays.asList("Dagas", "Capas", "Objetos de veneno", "Kits mejorados", "Ítems de evasión")
    );
    
    private final String displayName;
    private final String role;
    private final String description;
    private final BaseStats baseStats;
    private final List<String> initialEquipment;
    private final String initialAbility;
    private final List<String> preferredLoot;
    
    PlayerClass(String displayName, String role, String description, 
                BaseStats baseStats, List<String> initialEquipment,
                String initialAbility, List<String> preferredLoot) {
        this.displayName = displayName;
        this.role = role;
        this.description = description;
        this.baseStats = baseStats;
        this.initialEquipment = initialEquipment;
        this.initialAbility = initialAbility;
        this.preferredLoot = preferredLoot;
    }
    
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public String getDescription() { return description; }
    public BaseStats getBaseStats() { return baseStats; }
    public List<String> getInitialEquipment() { return initialEquipment; }
    public String getInitialAbility() { return initialAbility; }
    public List<String> getPreferredLoot() { return preferredLoot; }
    
    /**
     * Obtiene el daño base para combate.
     */
    public int getBaseDamage() {
        return baseStats.getAttack();
    }
    
    /**
     * Obtiene la defensa base.
     */
    public int getBaseDefense() {
        return baseStats.getDefense();
    }
    
    /**
     * Obtiene el HP máximo inicial.
     */
    public int getMaxHP() {
        return baseStats.getHp();
    }
    
    /**
     * Obtiene la velocidad (iniciativa).
     */
    public int getSpeed() {
        return baseStats.getSpeed();
    }
    
    /**
     * Stats base de una clase.
     */
    public static class BaseStats {
        private final int hp;
        private final int defense;
        private final int attack;
        private final int speed;
        
        public BaseStats(int hp, int defense, int attack, int speed) {
            this.hp = hp;
            this.defense = defense;
            this.attack = attack;
            this.speed = speed;
        }
        
        public int getHp() { return hp; }
        public int getDefense() { return defense; }
        public int getAttack() { return attack; }
        public int getSpeed() { return speed; }
    }
    
    /**
     * Obtiene una clase por su nombre (case-insensitive).
     */
    public static PlayerClass fromString(String className) {
        if (className == null) {
            return null;
        }
        
        String normalized = className.toLowerCase().trim();
        
        // Nombres en español
        switch (normalized) {
            case "guardian":
            case "guarda":
            case "tanque":
                return GUARDIAN;
            case "ranger":
            case "cazador":
            case "arquero":
                return RANGER;
            case "mage":
            case "mago":
            case "erudito":
                return MAGE;
            case "cleric":
            case "clerigo":
            case "clérigo":
            case "sanador":
                return CLERIC;
            case "rogue":
            case "picaro":
            case "pícaro":
            case "ladron":
            case "ladrón":
                return ROGUE;
            default:
                // Intentar match directo con enum
                try {
                    return PlayerClass.valueOf(normalized.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
        }
    }
    
    /**
     * Verifica si un nombre de clase es válido.
     */
    public static boolean isValid(String className) {
        return fromString(className) != null;
    }
}
