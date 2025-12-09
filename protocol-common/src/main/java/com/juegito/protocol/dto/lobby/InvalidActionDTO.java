package com.juegito.protocol.dto.lobby;

/**
 * Notificación de que una acción fue inválida.
 */
public class InvalidActionDTO {
    private String action;
    private String reason;
    
    public InvalidActionDTO() {}
    
    public InvalidActionDTO(String action, String reason) {
        this.action = action;
        this.reason = reason;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
