package com.juegito.protocol.dto;

/**
 * DTO para respuesta de reconexión.
 */
public class ReconnectResponseDTO {
    private boolean accepted;
    private String reason;
    
    public ReconnectResponseDTO() {}
    
    public ReconnectResponseDTO(boolean accepted, String reason) {
        this.accepted = accepted;
        this.reason = reason;
    }
    
    public static ReconnectResponseDTO accepted() {
        return new ReconnectResponseDTO(true, "Reconnection successful");
    }
    
    public static ReconnectResponseDTO rejected(String reason) {
        return new ReconnectResponseDTO(false, reason);
    }
    
    public boolean isAccepted() {
        return accepted;
    }
    
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
