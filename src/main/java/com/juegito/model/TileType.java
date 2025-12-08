package com.juegito.model;

/**
 * Tipos de casillas especiales en el mapa.
 */
public enum TileType {
    /**
     * Casilla normal sin efectos especiales.
     */
    NORMAL,
    
    /**
     * Punto de spawn donde aparecen los jugadores.
     */
    SPAWN,
    
    /**
     * Nodo de recursos que proporciona ventajas.
     */
    RESOURCE,
    
    /**
     * Nodo estratégico que otorga control del área.
     */
    STRATEGIC,
    
    /**
     * Terreno bloqueado e inaccesible.
     */
    BLOCKED
}
