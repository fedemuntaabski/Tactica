package com.juegito.game.lobby;

import com.juegito.protocol.dto.lobby.LobbyConfigDTO;

/**
 * Configuración del lobby (modelo del servidor).
 */
public class LobbyConfig {
    private String difficulty;
    private String mapSize;
    private int runLengthMinutes;
    private boolean randomSeed;
    private long customSeed;
    private boolean allowJoinInProgress;
    
    public LobbyConfig() {
        // Valores por defecto
        this.difficulty = "NORMAL";
        this.mapSize = "MEDIUM";
        this.runLengthMinutes = 30;
        this.randomSeed = true;
        this.allowJoinInProgress = false;
    }
    
    /**
     * Actualiza desde un DTO.
     */
    public void update(LobbyConfig other) {
        this.difficulty = other.difficulty;
        this.mapSize = other.mapSize;
        this.runLengthMinutes = other.runLengthMinutes;
        this.randomSeed = other.randomSeed;
        this.customSeed = other.customSeed;
        this.allowJoinInProgress = other.allowJoinInProgress;
    }
    
    /**
     * Convierte a DTO para enviar al cliente.
     */
    public LobbyConfigDTO toDTO() {
        LobbyConfigDTO dto = new LobbyConfigDTO();
        dto.setDifficulty(difficulty);
        dto.setMapSize(mapSize);
        dto.setRunLengthMinutes(runLengthMinutes);
        dto.setRandomSeed(randomSeed);
        dto.setCustomSeed(customSeed);
        dto.setAllowJoinInProgress(allowJoinInProgress);
        return dto;
    }
    
    /**
     * Crea desde un DTO.
     */
    public static LobbyConfig fromDTO(LobbyConfigDTO dto) {
        LobbyConfig config = new LobbyConfig();
        config.difficulty = dto.getDifficulty();
        config.mapSize = dto.getMapSize();
        config.runLengthMinutes = dto.getRunLengthMinutes();
        config.randomSeed = dto.isRandomSeed();
        config.customSeed = dto.getCustomSeed() != null ? dto.getCustomSeed() : 0;
        config.allowJoinInProgress = dto.isAllowJoinInProgress();
        return config;
    }
    
    // Getters
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public String getMapSize() {
        return mapSize;
    }
    
    public int getRunLengthMinutes() {
        return runLengthMinutes;
    }
    
    public boolean isRandomSeed() {
        return randomSeed;
    }
    
    public long getCustomSeed() {
        return customSeed;
    }
    
    public boolean isAllowJoinInProgress() {
        return allowJoinInProgress;
    }
}
