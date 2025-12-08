package com.juegito.client.protocol;

/**
 * Tipos de mensajes que pueden ser intercambiados entre cliente y servidor.
 * Espejo del enum del servidor.
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
    
    // Mensajes de validación
    ACTION_VALID,
    ACTION_INVALID,
    
    // Mensajes de sistema
    ERROR,
    PING,
    PONG
}
