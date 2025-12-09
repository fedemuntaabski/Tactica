package com.juegito.protocol.dto.character;

import java.util.List;

/**
 * Mensaje con todas las clases disponibles para el lobby.
 */
public class AvailableClassesDTO {
    private List<ClassInfoDTO> classes;
    
    public AvailableClassesDTO() {}
    
    public AvailableClassesDTO(List<ClassInfoDTO> classes) {
        this.classes = classes;
    }
    
    public List<ClassInfoDTO> getClasses() { return classes; }
    public void setClasses(List<ClassInfoDTO> classes) { this.classes = classes; }
}
