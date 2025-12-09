package com.juegito.protocol.dto;

/**
 * DTO para solicitud de uso de habilidad.
 */
public class AbilityRequestDTO {
    private String abilityId;
    private HexCoordinateDTO targetPosition; // Opcional, según la habilidad
    private String targetId;                 // Opcional, para habilidades dirigidas
    
    public AbilityRequestDTO() {}
    
    public AbilityRequestDTO(String abilityId, HexCoordinateDTO targetPosition, String targetId) {
        this.abilityId = abilityId;
        this.targetPosition = targetPosition;
        this.targetId = targetId;
    }
    
    public String getAbilityId() {
        return abilityId;
    }
    
    public void setAbilityId(String abilityId) {
        this.abilityId = abilityId;
    }
    
    public HexCoordinateDTO getTargetPosition() {
        return targetPosition;
    }
    
    public void setTargetPosition(HexCoordinateDTO targetPosition) {
        this.targetPosition = targetPosition;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
