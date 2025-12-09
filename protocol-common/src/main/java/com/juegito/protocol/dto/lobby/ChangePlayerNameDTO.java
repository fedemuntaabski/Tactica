package com.juegito.protocol.dto.lobby;

/**
 * DTO para solicitud de cambio de nombre del jugador.
 */
public class ChangePlayerNameDTO {
    private String newName;
    
    public ChangePlayerNameDTO() {}
    
    public ChangePlayerNameDTO(String newName) {
        this.newName = newName;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public void setNewName(String newName) {
        this.newName = newName;
    }
}
