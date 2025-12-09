package com.juegito.protocol.dto.map;

/**
 * DTO para votar sobre un movimiento sugerido.
 */
public class MovementVoteDTO {
    private String playerId;
    private boolean approve;
    
    public MovementVoteDTO() {}
    
    public MovementVoteDTO(String playerId, boolean approve) {
        this.playerId = playerId;
        this.approve = approve;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public boolean isApprove() {
        return approve;
    }
    
    public void setApprove(boolean approve) {
        this.approve = approve;
    }
}
