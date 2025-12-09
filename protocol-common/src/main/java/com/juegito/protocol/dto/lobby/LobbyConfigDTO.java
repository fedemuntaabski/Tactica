package com.juegito.protocol.dto.lobby;

/**
 * Configuración del lobby y partida.
 */
public class LobbyConfigDTO {
    private String difficulty;
    private String mapSize;
    private int runLengthMinutes;
    private boolean randomSeed;
    private Long customSeed;
    private boolean allowJoinInProgress;
    
    public LobbyConfigDTO() {
        // Valores por defecto
        this.difficulty = "NORMAL";
        this.mapSize = "MEDIUM";
        this.runLengthMinutes = 30;
        this.randomSeed = true;
        this.allowJoinInProgress = false;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getMapSize() {
        return mapSize;
    }
    
    public void setMapSize(String mapSize) {
        this.mapSize = mapSize;
    }
    
    public int getRunLengthMinutes() {
        return runLengthMinutes;
    }
    
    public void setRunLengthMinutes(int runLengthMinutes) {
        this.runLengthMinutes = runLengthMinutes;
    }
    
    public boolean isRandomSeed() {
        return randomSeed;
    }
    
    public void setRandomSeed(boolean randomSeed) {
        this.randomSeed = randomSeed;
    }
    
    public Long getCustomSeed() {
        return customSeed;
    }
    
    public void setCustomSeed(Long customSeed) {
        this.customSeed = customSeed;
    }
    
    public boolean isAllowJoinInProgress() {
        return allowJoinInProgress;
    }
    
    public void setAllowJoinInProgress(boolean allowJoinInProgress) {
        this.allowJoinInProgress = allowJoinInProgress;
    }
}
