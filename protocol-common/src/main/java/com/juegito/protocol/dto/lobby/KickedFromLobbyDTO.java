package com.juegito.protocol.dto.lobby;

/**
 * Notificación al jugador de que fue expulsado del lobby.
 */
public class KickedFromLobbyDTO {
    private String reason;
    
    public KickedFromLobbyDTO() {}
    
    public KickedFromLobbyDTO(String reason) {
        this.reason = reason;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
