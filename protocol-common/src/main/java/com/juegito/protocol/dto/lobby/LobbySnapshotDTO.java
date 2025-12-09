package com.juegito.protocol.dto.lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot completo del estado del lobby enviado periódicamente a los clientes.
 */
public class LobbySnapshotDTO {
    private String lobbyId;
    private String hostId;
    private int maxPlayers;
    private List<PlayerLobbyDataDTO> players;
    private LobbyConfigDTO lobbySettings;
    private LobbyStatus lobbyStatus;
    private long createdTimestamp;
    
    public LobbySnapshotDTO() {
        this.players = new ArrayList<>();
        this.lobbySettings = new LobbyConfigDTO();
        this.lobbyStatus = LobbyStatus.WAITING;
        this.maxPlayers = 6;
    }
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }
    
    public String getHostId() {
        return hostId;
    }
    
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public List<PlayerLobbyDataDTO> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<PlayerLobbyDataDTO> players) {
        this.players = players;
    }
    
    public LobbyConfigDTO getLobbySettings() {
        return lobbySettings;
    }
    
    public void setLobbySettings(LobbyConfigDTO lobbySettings) {
        this.lobbySettings = lobbySettings;
    }
    
    public LobbyStatus getLobbyStatus() {
        return lobbyStatus;
    }
    
    public void setLobbyStatus(LobbyStatus lobbyStatus) {
        this.lobbyStatus = lobbyStatus;
    }
    
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }
    
    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
