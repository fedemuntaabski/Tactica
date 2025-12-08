package com.juegito.client.protocol.dto;

/**
 * DTO para información de conexión de jugador.
 */
public class PlayerConnectDTO {
    private String playerName;
    private String playerId;
    
    public PlayerConnectDTO() {}
    
    public PlayerConnectDTO(String playerName, String playerId) {
        this.playerName = playerName;
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
