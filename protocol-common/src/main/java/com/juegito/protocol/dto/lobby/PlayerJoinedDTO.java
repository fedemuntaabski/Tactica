package com.juegito.protocol.dto.lobby;

/**
 * Notificación de que un jugador se unió al lobby.
 */
public class PlayerJoinedDTO {
    private PlayerLobbyDataDTO player;
    
    public PlayerJoinedDTO() {}
    
    public PlayerJoinedDTO(PlayerLobbyDataDTO player) {
        this.player = player;
    }
    
    public PlayerLobbyDataDTO getPlayer() {
        return player;
    }
    
    public void setPlayer(PlayerLobbyDataDTO player) {
        this.player = player;
    }
}
