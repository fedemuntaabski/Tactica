package com.juegito.protocol.dto;

/**
 * DTO para información básica de un jugador.
 */
public class PlayerInfoDTO {
    private String playerId;
    private String playerName;
    private boolean ready;
    
    public PlayerInfoDTO() {}
    
    public PlayerInfoDTO(String playerId, String playerName, boolean ready) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.ready = ready;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
