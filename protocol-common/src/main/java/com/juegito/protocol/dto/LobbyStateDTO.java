package com.juegito.protocol.dto;

import java.util.List;

/**
 * DTO para el estado del lobby (LEGACY - para compatibilidad con cliente antiguo).
 * 
 * @deprecated Usar LobbySnapshotDTO del paquete lobby para nuevas implementaciones.
 * Este DTO se mantiene solo para compatibilidad con clientes antiguos que aún no
 * se han actualizado al nuevo sistema de lobby.
 * 
 * @see com.juegito.protocol.dto.lobby.LobbySnapshotDTO
 */
@Deprecated
public class LobbyStateDTO {
    private List<PlayerInfoDTO> players;
    private int maxPlayers;
    private boolean gameStarted;
    
    public LobbyStateDTO() {}
    
    public LobbyStateDTO(List<PlayerInfoDTO> players, int maxPlayers, boolean gameStarted) {
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.gameStarted = gameStarted;
    }
    
    public List<PlayerInfoDTO> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<PlayerInfoDTO> players) {
        this.players = players;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
