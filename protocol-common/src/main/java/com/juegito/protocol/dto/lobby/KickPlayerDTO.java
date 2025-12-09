package com.juegito.protocol.dto.lobby;

/**
 * Solicitud del host para expulsar a un jugador.
 */
public class KickPlayerDTO {
    private String playerId;
    private String reason;
    
    public KickPlayerDTO() {}
    
    public KickPlayerDTO(String playerId, String reason) {
        this.playerId = playerId;
        this.reason = reason;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
