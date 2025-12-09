package com.juegito.protocol.dto;

import java.util.Map;

/**
 * DTO para heartbeat periódico del estado del juego.
 * Envía información resumida cada pocos segundos para mantener sincronización.
 */
public class GameHeartbeatDTO {
    private int turnNumber;
    private String currentTurnPlayerId;
    private Map<String, Integer> playerHP;  // playerId -> HP
    private long timestamp;
    
    public GameHeartbeatDTO() {}
    
    public GameHeartbeatDTO(int turnNumber, String currentTurnPlayerId, 
                           Map<String, Integer> playerHP) {
        this.turnNumber = turnNumber;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.playerHP = playerHP;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters y Setters
    
    public int getTurnNumber() {
        return turnNumber;
    }
    
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }
    
    public String getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }
    
    public void setCurrentTurnPlayerId(String currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }
    
    public Map<String, Integer> getPlayerHP() {
        return playerHP;
    }
    
    public void setPlayerHP(Map<String, Integer> playerHP) {
        this.playerHP = playerHP;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
