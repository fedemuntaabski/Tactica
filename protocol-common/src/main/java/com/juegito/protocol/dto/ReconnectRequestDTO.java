package com.juegito.protocol.dto;

/**
 * DTO para solicitud de reconexión del cliente.
 */
public class ReconnectRequestDTO {
    private String playerId;
    private String sessionToken;  // Token opcional para validar sesión
    
    public ReconnectRequestDTO() {}
    
    public ReconnectRequestDTO(String playerId, String sessionToken) {
        this.playerId = playerId;
        this.sessionToken = sessionToken;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
