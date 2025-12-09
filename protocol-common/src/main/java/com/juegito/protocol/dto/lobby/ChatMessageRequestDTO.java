package com.juegito.protocol.dto.lobby;

/**
 * DTO para solicitud de envío de mensaje de chat.
 */
public class ChatMessageRequestDTO {
    private String message;
    
    public ChatMessageRequestDTO() {
    }
    
    public ChatMessageRequestDTO(String message) {
        this.message = message;
    }
    
    // Getters y setters
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
