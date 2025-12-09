package com.juegito.game.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona el sistema de movimiento cooperativo por votación.
 */
public class MovementVoteManager {
    private final Map<String, Vote> votes;
    private MovementSuggestion currentSuggestion;
    private String currentLeaderId;
    
    public MovementVoteManager() {
        this.votes = new HashMap<>();
    }
    
    /**
     * Inicia una nueva sugerencia de movimiento.
     */
    public void startSuggestion(String leaderId, int targetTileId) {
        this.currentLeaderId = leaderId;
        this.currentSuggestion = new MovementSuggestion(leaderId, targetTileId);
        this.votes.clear();
    }
    
    /**
     * Registra el voto de un jugador.
     */
    public void castVote(String playerId, boolean approve) {
        if (currentSuggestion == null) {
            return;
        }
        votes.put(playerId, new Vote(playerId, approve));
    }
    
    /**
     * Verifica si todos los jugadores han votado.
     */
    public boolean allPlayersVoted(int totalPlayers) {
        return votes.size() >= totalPlayers;
    }
    
    /**
     * Verifica si el movimiento fue aprobado (todos votan YES).
     */
    public boolean isApproved() {
        if (votes.isEmpty()) {
            return false;
        }
        return votes.values().stream().allMatch(v -> v.approve);
    }
    
    /**
     * Obtiene el resultado de la votación.
     */
    public VoteResult getResult() {
        if (currentSuggestion == null) {
            return null;
        }
        
        boolean approved = isApproved();
        int yesVotes = (int) votes.values().stream().filter(v -> v.approve).count();
        int noVotes = votes.size() - yesVotes;
        
        return new VoteResult(
            currentSuggestion.targetTileId,
            approved,
            yesVotes,
            noVotes
        );
    }
    
    /**
     * Limpia la votación actual.
     */
    public void clear() {
        currentSuggestion = null;
        votes.clear();
    }
    
    public MovementSuggestion getCurrentSuggestion() {
        return currentSuggestion;
    }
    
    public String getCurrentLeaderId() {
        return currentLeaderId;
    }
    
    public Map<String, Vote> getVotes() {
        return new HashMap<>(votes);
    }
    
    /**
     * Representa una sugerencia de movimiento.
     */
    public static class MovementSuggestion {
        private final String leaderId;
        private final int targetTileId;
        private final long timestamp;
        
        public MovementSuggestion(String leaderId, int targetTileId) {
            this.leaderId = leaderId;
            this.targetTileId = targetTileId;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getLeaderId() {
            return leaderId;
        }
        
        public int getTargetTileId() {
            return targetTileId;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * Representa un voto individual.
     */
    public static class Vote {
        private final String playerId;
        private final boolean approve;
        
        public Vote(String playerId, boolean approve) {
            this.playerId = playerId;
            this.approve = approve;
        }
        
        public String getPlayerId() {
            return playerId;
        }
        
        public boolean isApprove() {
            return approve;
        }
    }
    
    /**
     * Resultado de una votación.
     */
    public static class VoteResult {
        private final int targetTileId;
        private final boolean approved;
        private final int yesVotes;
        private final int noVotes;
        
        public VoteResult(int targetTileId, boolean approved, int yesVotes, int noVotes) {
            this.targetTileId = targetTileId;
            this.approved = approved;
            this.yesVotes = yesVotes;
            this.noVotes = noVotes;
        }
        
        public int getTargetTileId() {
            return targetTileId;
        }
        
        public boolean isApproved() {
            return approved;
        }
        
        public int getYesVotes() {
            return yesVotes;
        }
        
        public int getNoVotes() {
            return noVotes;
        }
    }
}
