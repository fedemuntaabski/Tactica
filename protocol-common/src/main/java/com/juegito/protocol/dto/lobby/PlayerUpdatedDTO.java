package com.juegito.protocol.dto.lobby;

/**
 * Notificación de que un jugador actualizó su estado en el lobby.
 */
public class PlayerUpdatedDTO {
    private PlayerLobbyDataDTO player;
    
    public PlayerUpdatedDTO() {}
    
    public PlayerUpdatedDTO(PlayerLobbyDataDTO player) {
        this.player = player;
    }
    
    public PlayerLobbyDataDTO getPlayer() {
        return player;
    }
    
    public void setPlayer(PlayerLobbyDataDTO player) {
        this.player = player;
    }
}
