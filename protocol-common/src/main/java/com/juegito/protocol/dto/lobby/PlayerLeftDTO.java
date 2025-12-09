package com.juegito.protocol.dto.lobby;

/**
 * Notificación de que un jugador salió del lobby.
 */
public class PlayerLeftDTO {
    private String playerId;
    private String playerName;
    private String reason;
    
    public PlayerLeftDTO() {}
    
    public PlayerLeftDTO(String playerId, String playerName, String reason) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.reason = reason;
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
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
