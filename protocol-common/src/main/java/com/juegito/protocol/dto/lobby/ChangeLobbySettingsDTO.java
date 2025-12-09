package com.juegito.protocol.dto.lobby;

/**
 * Solicitud del host para cambiar la configuración del lobby.
 */
public class ChangeLobbySettingsDTO {
    private LobbyConfigDTO newSettings;
    
    public ChangeLobbySettingsDTO() {}
    
    public ChangeLobbySettingsDTO(LobbyConfigDTO newSettings) {
        this.newSettings = newSettings;
    }
    
    public LobbyConfigDTO getNewSettings() {
        return newSettings;
    }
    
    public void setNewSettings(LobbyConfigDTO newSettings) {
        this.newSettings = newSettings;
    }
}
