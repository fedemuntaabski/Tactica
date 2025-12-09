package com.juegito.protocol.dto.character;

/**
 * Stats base de una clase.
 */
public class BaseStatsDTO {
    private int hp;
    private int defense;
    private int attack;
    private int speed;
    
    public BaseStatsDTO() {}
    
    public BaseStatsDTO(int hp, int defense, int attack, int speed) {
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.speed = speed;
    }
    
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    
    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }
    
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
}
