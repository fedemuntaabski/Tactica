package com.juegito.protocol.dto.lobby;

/**
 * Respuesta del servidor a una solicitud de join.
 */
public class JoinResponseDTO {
    private boolean success;
    private String playerId;
    private String reason;
    
    public JoinResponseDTO() {}
    
    public JoinResponseDTO(boolean success, String playerId, String reason) {
        this.success = success;
        this.playerId = playerId;
        this.reason = reason;
    }
    
    public static JoinResponseDTO success(String playerId) {
        return new JoinResponseDTO(true, playerId, null);
    }
    
    public static JoinResponseDTO failure(String reason) {
        return new JoinResponseDTO(false, null, reason);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
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
