package com.juegito.protocol.dto.lobby;

/**
 * Información completa de un jugador en el lobby.
 */
public class PlayerLobbyDataDTO {
    private String playerId;
    private String playerName;
    private ConnectionStatus connectionStatus;
    private String selectedClass;
    private String selectedColor;
    private boolean isHost;
    
    public PlayerLobbyDataDTO() {
        this.connectionStatus = ConnectionStatus.CONNECTED;
    }
    
    public PlayerLobbyDataDTO(String playerId, String playerName, boolean isHost) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.connectionStatus = ConnectionStatus.CONNECTED;
        this.isHost = isHost;
    }
    
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
    
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
    
    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public String getSelectedClass() {
        return selectedClass;
    }
    
    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }
    
    public String getSelectedColor() {
        return selectedColor;
    }
    
    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }
    
    public boolean isHost() {
        return isHost;
    }
    
    public void setHost(boolean host) {
        isHost = host;
    }
}
