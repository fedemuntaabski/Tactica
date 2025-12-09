package com.juegito.protocol.dto.lobby;

/**
 * DTO para un mensaje de chat en el lobby.
 */
public class ChatMessageDTO {
    private String playerId;
    private String playerName;
    private String message;
    private long timestamp;
    
    public ChatMessageDTO() {
    }
    
    public ChatMessageDTO(String playerId, String playerName, String message) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters y setters
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
