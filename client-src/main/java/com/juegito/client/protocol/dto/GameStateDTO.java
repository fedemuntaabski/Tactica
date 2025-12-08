package com.juegito.client.protocol.dto;

import java.util.Map;

/**
 * DTO para el estado completo del juego.
 */
public class GameStateDTO {
    private String currentTurnPlayerId;
    private int turnNumber;
    private Map<String, Object> worldState;
    
    public GameStateDTO() {}
    
    public GameStateDTO(String currentTurnPlayerId, int turnNumber, Map<String, Object> worldState) {
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.turnNumber = turnNumber;
        this.worldState = worldState;
    }
    
    public String getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }
    
    public void setCurrentTurnPlayerId(String currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }
    
    public int getTurnNumber() {
        return turnNumber;
    }
    
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }
    
    public Map<String, Object> getWorldState() {
        return worldState;
    }
    
    public void setWorldState(Map<String, Object> worldState) {
        this.worldState = worldState;
    }
}
