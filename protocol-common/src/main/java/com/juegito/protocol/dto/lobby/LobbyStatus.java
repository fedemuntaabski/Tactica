package com.juegito.protocol.dto.lobby;

/**
 * Estados del lobby durante su ciclo de vida.
 */
public enum LobbyStatus {
    /** Esperando jugadores, permitiendo cambios de configuración */
    WAITING,
    
    /** Iniciando partida, generando mundo */
    STARTING,
    
    /** Partida en curso */
    IN_GAME
}
