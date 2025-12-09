package com.juegito.game.lobby;

import com.juegito.protocol.dto.lobby.ConnectionStatus;
import com.juegito.protocol.dto.lobby.PlayerLobbyDataDTO;

/**
 * Datos de un jugador en el lobby (modelo del servidor).
 */
public class PlayerLobbyData {
    private final String playerId;
    private String playerName; // No final para permitir cambio de nombre
    private final boolean isHost;
    
    private ConnectionStatus connectionStatus;
    private String selectedClass;
    private String selectedColor;
    private String ipAddress;
    
    public PlayerLobbyData(String playerId, String playerName, boolean isHost) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isHost = isHost;
        this.connectionStatus = ConnectionStatus.CONNECTED;
    }
    
    /**
     * Convierte a DTO para enviar al cliente.
     */
    public PlayerLobbyDataDTO toDTO() {
        PlayerLobbyDataDTO dto = new PlayerLobbyDataDTO();
        dto.setPlayerId(playerId);
        dto.setPlayerName(playerName);
        dto.setHost(isHost);
        dto.setConnectionStatus(connectionStatus);
        dto.setSelectedClass(selectedClass);
        dto.setSelectedColor(selectedColor);
        return dto;
    }
    
    // Getters y setters
    
    public String getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public boolean isHost() {
        return isHost;
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
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
