package com.juegito.client.events;

/**
 * Tipos de eventos que pueden ocurrir en el cliente.
 */
public enum GameEventType {
    // Eventos de conexión
    CONNECTION_ESTABLISHED,
    CONNECTION_LOST,
    RECONNECTION_SUCCESS,
    RECONNECTION_FAILED,
    
    // Eventos de lobby
    LOBBY_JOINED,
    LOBBY_LEFT,
    PLAYER_READY_CHANGED,
    GAME_STARTING,
    
    // Eventos de juego
    TURN_STARTED,
    TURN_ENDED,
    ACTION_SENT,
    ACTION_ACCEPTED,
    ACTION_REJECTED,
    
    // Eventos de estado
    MAP_UPDATED,
    PLAYER_MOVED,
    PLAYER_STATS_CHANGED,
    
    // Eventos de UI
    UI_ERROR,
    UI_MESSAGE
}
