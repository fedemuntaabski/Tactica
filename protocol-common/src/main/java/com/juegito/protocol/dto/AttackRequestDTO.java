package com.juegito.protocol.dto;

/**
 * DTO para solicitud de ataque.
 */
public class AttackRequestDTO {
    private String targetId;      // ID del objetivo (jugador o enemigo)
    private String attackType;    // "MELEE" o "RANGED"
    
    public AttackRequestDTO() {}
    
    public AttackRequestDTO(String targetId, String attackType) {
        this.targetId = targetId;
        this.attackType = attackType;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public String getAttackType() {
        return attackType;
    }
    
    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }
}
