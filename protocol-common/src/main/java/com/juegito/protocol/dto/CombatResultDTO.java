package com.juegito.protocol.dto;

/**
 * DTO para resultado de combate.
 */
public class CombatResultDTO {
    private String attackerId;
    private String targetId;
    private String attackType;
    private boolean hit;
    private int damage;
    private boolean critical;
    private int defenseBonus;
    private int targetRemainingHP;
    private boolean targetDead;
    
    public CombatResultDTO() {}
    
    public CombatResultDTO(String attackerId, String targetId, String attackType,
                          boolean hit, int damage, boolean critical, int defenseBonus,
                          int targetRemainingHP, boolean targetDead) {
        this.attackerId = attackerId;
        this.targetId = targetId;
        this.attackType = attackType;
        this.hit = hit;
        this.damage = damage;
        this.critical = critical;
        this.defenseBonus = defenseBonus;
        this.targetRemainingHP = targetRemainingHP;
        this.targetDead = targetDead;
    }
    
    // Getters y Setters
    public String getAttackerId() { return attackerId; }
    public void setAttackerId(String attackerId) { this.attackerId = attackerId; }
    
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    
    public String getAttackType() { return attackType; }
    public void setAttackType(String attackType) { this.attackType = attackType; }
    
    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }
    
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    
    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }
    
    public int getDefenseBonus() { return defenseBonus; }
    public void setDefenseBonus(int defenseBonus) { this.defenseBonus = defenseBonus; }
    
    public int getTargetRemainingHP() { return targetRemainingHP; }
    public void setTargetRemainingHP(int targetRemainingHP) { this.targetRemainingHP = targetRemainingHP; }
    
    public boolean isTargetDead() { return targetDead; }
    public void setTargetDead(boolean targetDead) { this.targetDead = targetDead; }
}
