package com.juegito.game.enemy;

import com.juegito.model.HexCoordinate;

/**
 * Representa un enemigo en el juego.
 * Implementa KISS: propiedades simples sin lógica compleja.
 */
public class Enemy {
    private final String id;
    private String name;
    private EnemyType type;
    private HexCoordinate position;
    private int currentHP;
    private int maxHP;
    private int baseDamage;
    private int moveRange;
    private int attackRange;
    private boolean isAlive;
    
    public Enemy(String id, String name, EnemyType type, HexCoordinate position,
                 int maxHP, int baseDamage, int moveRange, int attackRange) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.position = position;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.baseDamage = baseDamage;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.isAlive = true;
    }
    
    /**
     * Aplica daño al enemigo.
     */
    public void takeDamage(int damage) {
        currentHP = Math.max(0, currentHP - damage);
        if (currentHP == 0) {
            isAlive = false;
        }
    }
    
    /**
     * Mueve el enemigo a una nueva posición.
     */
    public void moveTo(HexCoordinate newPosition) {
        this.position = newPosition;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public EnemyType getType() { return type; }
    public HexCoordinate getPosition() { return position; }
    public int getCurrentHP() { return currentHP; }
    public int getMaxHP() { return maxHP; }
    public int getBaseDamage() { return baseDamage; }
    public int getMoveRange() { return moveRange; }
    public int getAttackRange() { return attackRange; }
    public boolean isAlive() { return isAlive; }
    
    public float getHPPercentage() {
        return maxHP > 0 ? (float) currentHP / maxHP : 0f;
    }
    
    @Override
    public String toString() {
        return String.format("Enemy[%s, %s, HP:%d/%d, pos:%s]", 
            id, type, currentHP, maxHP, position);
    }
}
