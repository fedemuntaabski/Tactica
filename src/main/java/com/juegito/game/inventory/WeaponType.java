package com.juegito.game.inventory;

/**
 * Tipos de armas con características únicas.
 */
public enum WeaponType {
    SWORD("Espada", DamageType.SLASH, 1, 1.0),
    AXE("Hacha", DamageType.SLASH, 1, 0.9),
    SPEAR("Lanza", DamageType.PIERCE, 2, 1.0),
    BOW("Arco", DamageType.PIERCE, 4, 0.8),
    CROSSBOW("Ballesta", DamageType.PIERCE, 4, 0.7),
    STAFF("Bastón", DamageType.MAGIC, 2, 1.0),
    TOME("Tomo Mágico", DamageType.MAGIC, 3, 1.1),
    DAGGER("Daga", DamageType.PIERCE, 1, 1.2),
    MACE("Maza", DamageType.BLUNT, 1, 0.9),
    MYSTIC_GLOVES("Guantes Místicos", DamageType.MAGIC, 1, 1.0);
    
    private final String displayName;
    private final DamageType damageType;
    private final int range;
    private final double speedModifier; // 1.0 = normal, <1.0 = más lento, >1.0 = más rápido
    
    WeaponType(String displayName, DamageType damageType, int range, double speedModifier) {
        this.displayName = displayName;
        this.damageType = damageType;
        this.range = range;
        this.speedModifier = speedModifier;
    }
    
    public String getDisplayName() { return displayName; }
    public DamageType getDamageType() { return damageType; }
    public int getRange() { return range; }
    public double getSpeedModifier() { return speedModifier; }
}
