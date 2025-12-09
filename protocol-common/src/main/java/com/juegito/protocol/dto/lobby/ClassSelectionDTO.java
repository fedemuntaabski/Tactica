package com.juegito.protocol.dto.lobby;

/**
 * Solicitud de selección de clase de un jugador.
 */
public class ClassSelectionDTO {
    private String classId;
    
    public ClassSelectionDTO() {}
    
    public ClassSelectionDTO(String classId) {
        this.classId = classId;
    }
    
    public String getClassId() {
        return classId;
    }
    
    public void setClassId(String classId) {
        this.classId = classId;
    }
}
