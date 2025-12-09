package com.juegito.protocol.dto.lobby;

/**
 * Estado de conexión de un jugador en el lobby.
 */
public enum ConnectionStatus {
    /** Jugador conectado pero no listo */
    CONNECTED,
    
    /** Jugador marcado como listo para iniciar */
    READY,
    
    /** Jugador desconectado */
    DISCONNECTED
}
