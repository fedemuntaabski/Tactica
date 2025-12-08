package com.juegito.protocol;

/**
 * Tipos de mensajes que pueden ser intercambiados entre cliente y servidor.
 */
public enum MessageType {
    // Mensajes de conexión
    PLAYER_CONNECT,
    PLAYER_DISCONNECT,
    
    // Mensajes de lobby
    LOBBY_STATE,
    START_GAME,
    
    // Mensajes de juego
    GAME_STATE,
    PLAYER_ACTION,
    TURN_START,
    TURN_END,
    
    // Mensajes de mapa y movimiento
    MAP_STATE,
    MOVEMENT_REQUEST,
    MOVEMENT_RESULT,
    REACHABLE_TILES,
    
    // Mensajes de validación
    ACTION_VALID,
    ACTION_INVALID,
    
    // Mensajes de sistema
    ERROR,
    PING,
    PONG
}
