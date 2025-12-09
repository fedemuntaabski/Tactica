package com.juegito.model.map;

/**
 * Tipo de casilla en el mapa.
 */
public enum TileType {
    START,           // Casilla inicial
    NORMAL,          // Casilla normal con posible evento
    MINI_BOSS,       // Casilla con mini-boss
    BOSS,            // Casilla del jefe final
    REST,            // Casilla de descanso/campamento
    SHOP,            // Casilla de tienda
    TREASURE,        // Casilla con cofre garantizado
    FORK             // Casilla donde el camino se bifurca
}
