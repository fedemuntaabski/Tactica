package com.juegito.protocol.dto.lobby;

/**
 * Solicitud de selección de color de un jugador.
 */
public class ColorSelectionDTO {
    private String color;
    
    public ColorSelectionDTO() {}
    
    public ColorSelectionDTO(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
}
