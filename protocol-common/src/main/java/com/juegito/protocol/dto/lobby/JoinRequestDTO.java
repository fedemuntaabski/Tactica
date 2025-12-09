package com.juegito.protocol.dto.lobby;

/**
 * Solicitud de un jugador para unirse al lobby.
 */
public class JoinRequestDTO {
    private String playerName;
    
    public JoinRequestDTO() {}
    
    public JoinRequestDTO(String playerName) {
        this.playerName = playerName;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
