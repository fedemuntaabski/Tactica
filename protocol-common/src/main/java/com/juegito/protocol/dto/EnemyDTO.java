package com.juegito.protocol.dto;

/**
 * DTO para información de enemigo.
 */
public class EnemyDTO {
    private String id;
    private String name;
    private String type;
    private HexCoordinateDTO position;
    private int currentHP;
    private int maxHP;
    private int baseDamage;
    private int moveRange;
    private int attackRange;
    private boolean alive;
    
    public EnemyDTO() {}
    
    public EnemyDTO(String id, String name, String type, HexCoordinateDTO position,
                   int currentHP, int maxHP, int baseDamage, int moveRange, 
                   int attackRange, boolean alive) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.position = position;
        this.currentHP = currentHP;
        this.maxHP = maxHP;
        this.baseDamage = baseDamage;
        this.moveRange = moveRange;
        this.attackRange = attackRange;
        this.alive = alive;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public HexCoordinateDTO getPosition() { return position; }
    public void setPosition(HexCoordinateDTO position) { this.position = position; }
    
    public int getCurrentHP() { return currentHP; }
    public void setCurrentHP(int currentHP) { this.currentHP = currentHP; }
    
    public int getMaxHP() { return maxHP; }
    public void setMaxHP(int maxHP) { this.maxHP = maxHP; }
    
    public int getBaseDamage() { return baseDamage; }
    public void setBaseDamage(int baseDamage) { this.baseDamage = baseDamage; }
    
    public int getMoveRange() { return moveRange; }
    public void setMoveRange(int moveRange) { this.moveRange = moveRange; }
    
    public int getAttackRange() { return attackRange; }
    public void setAttackRange(int attackRange) { this.attackRange = attackRange; }
    
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
}
