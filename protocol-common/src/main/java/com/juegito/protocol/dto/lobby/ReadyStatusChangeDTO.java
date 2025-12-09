package com.juegito.protocol.dto.lobby;

/**
 * Solicitud de cambio de estado ready de un jugador.
 */
public class ReadyStatusChangeDTO {
    private boolean ready;
    
    public ReadyStatusChangeDTO() {}
    
    public ReadyStatusChangeDTO(boolean ready) {
        this.ready = ready;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
