package com.juegito.protocol.dto.map;

/**
 * DTO para sugerir un movimiento.
 */
public class MovementSuggestionDTO {
    private String leaderId;
    private int targetTileId;
    
    public MovementSuggestionDTO() {}
    
    public MovementSuggestionDTO(String leaderId, int targetTileId) {
        this.leaderId = leaderId;
        this.targetTileId = targetTileId;
    }
    
    public String getLeaderId() {
        return leaderId;
    }
    
    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }
    
    public int getTargetTileId() {
        return targetTileId;
    }
    
    public void setTargetTileId(int targetTileId) {
        this.targetTileId = targetTileId;
    }
}
