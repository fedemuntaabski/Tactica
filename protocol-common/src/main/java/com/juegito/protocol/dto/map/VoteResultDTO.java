package com.juegito.protocol.dto.map;

import java.util.Map;

/**
 * DTO para notificar el resultado de una votación de movimiento.
 */
public class VoteResultDTO {
    private int targetTileId;
    private boolean approved;
    private int yesVotes;
    private int noVotes;
    private Map<String, Boolean> votes;
    
    public VoteResultDTO() {}
    
    public VoteResultDTO(int targetTileId, boolean approved, int yesVotes, int noVotes, Map<String, Boolean> votes) {
        this.targetTileId = targetTileId;
        this.approved = approved;
        this.yesVotes = yesVotes;
        this.noVotes = noVotes;
        this.votes = votes;
    }
    
    public int getTargetTileId() {
        return targetTileId;
    }
    
    public void setTargetTileId(int targetTileId) {
        this.targetTileId = targetTileId;
    }
    
    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    
    public int getYesVotes() {
        return yesVotes;
    }
    
    public void setYesVotes(int yesVotes) {
        this.yesVotes = yesVotes;
    }
    
    public int getNoVotes() {
        return noVotes;
    }
    
    public void setNoVotes(int noVotes) {
        this.noVotes = noVotes;
    }
    
    public Map<String, Boolean> getVotes() {
        return votes;
    }
    
    public void setVotes(Map<String, Boolean> votes) {
        this.votes = votes;
    }
}
