package com.juegito.protocol.dto.lobby;

import java.util.List;

/**
 * Notificación de inicio de partida con configuración final.
 */
public class StartMatchDTO {
    private long seed;
    private LobbyConfigDTO config;
    private List<PlayerLobbyDataDTO> players;
    
    public StartMatchDTO() {}
    
    public StartMatchDTO(long seed, LobbyConfigDTO config, List<PlayerLobbyDataDTO> players) {
        this.seed = seed;
        this.config = config;
        this.players = players;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public void setSeed(long seed) {
        this.seed = seed;
    }
    
    public LobbyConfigDTO getConfig() {
        return config;
    }
    
    public void setConfig(LobbyConfigDTO config) {
        this.config = config;
    }
    
    public List<PlayerLobbyDataDTO> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<PlayerLobbyDataDTO> players) {
        this.players = players;
    }
}
