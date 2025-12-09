package com.juegito.game.enemy;

import com.juegito.model.HexCoordinate;

/**
 * Tipos de enemigos con estadísticas predefinidas.
 * Implementa KISS: enum simple con configuración básica.
 */
public enum EnemyType {
    GOBLIN(30, 8, 2, 1),      // HP bajo, daño bajo, móvil, melee
    ORC(50, 12, 1, 1),        // HP medio, daño medio, lento, melee
    ARCHER(25, 10, 2, 3),     // HP bajo, daño medio, móvil, ranged
    TROLL(80, 15, 1, 1),      // HP alto, daño alto, lento, melee
    WIZARD(35, 18, 1, 3);     // HP bajo, daño alto, lento, ranged
    
    private final int maxHP;
    private final int baseDamage;
    private final int moveRange;
    private final int attackRange;
    
    EnemyType(int maxHP, int baseDamage, int moveRange, int attackRange) {
        this.maxHP = maxHP;
        this.baseDamage = baseDamage;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
    }
    
    public int getMaxHP() { return maxHP; }
    public int getBaseDamage() { return baseDamage; }
    public int getMoveRange() { return moveRange; }
    public int getAttackRange() { return attackRange; }
    
    /**
     * Crea un enemigo de este tipo en la posición especificada.
     */
    public Enemy createEnemy(String id, HexCoordinate position) {
        return new Enemy(id, this.name(), this, position, 
                        maxHP, baseDamage, moveRange, attackRange);
    }
}
