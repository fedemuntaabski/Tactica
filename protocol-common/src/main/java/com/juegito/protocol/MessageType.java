package com.juegito.protocol;

/**
 * Tipos de mensajes que pueden ser intercambiados entre cliente y servidor.
 */
public enum MessageType {
    // Mensajes de conexión
    PLAYER_CONNECT,
    PLAYER_DISCONNECT,
    
    // Mensajes de lobby (cliente → servidor)
    JOIN_REQUEST,
    LEAVE_LOBBY,
    READY_STATUS_CHANGE,
    CLASS_SELECTION,
    COLOR_SELECTION,
    KICK_PLAYER,
    START_MATCH_REQUEST,
    CHANGE_LOBBY_SETTINGS,
    CHAT_MESSAGE_REQUEST,
    CHANGE_PLAYER_NAME,
    
    // Mensajes de lobby (servidor → cliente)
    JOIN_RESPONSE,
    LOBBY_SNAPSHOT,
    PLAYER_JOINED,
    PLAYER_LEFT,
    PLAYER_UPDATED,
    INVALID_ACTION,
    START_MATCH,
    KICKED_FROM_LOBBY,
    CHAT_MESSAGE,
    
    // Mensajes de lobby legacy (mantener compatibilidad)
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
