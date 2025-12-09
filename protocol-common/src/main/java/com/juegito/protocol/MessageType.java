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
    GAME_HEARTBEAT,          // Heartbeat periódico del estado del juego
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
    
    // Mensajes de reconexión y resync
    REQUEST_RESYNC,          // Cliente pide resincronización
    FULL_RESYNC,             // Servidor envía resincronización completa
    RECONNECT_REQUEST,       // Cliente pide reconectarse a partida
    RECONNECT_ACCEPTED,      // Servidor acepta reconexión
    RECONNECT_REJECTED,      // Servidor rechaza reconexión
    
    // Mensajes de combate (FASE 4)
    ATTACK_REQUEST,          // Cliente solicita atacar
    COMBAT_RESULT,           // Servidor notifica resultado de combate
    PLAYER_DIED,             // Servidor notifica muerte de jugador
    ENEMY_UPDATE,            // Servidor notifica cambio en enemigo
    ENEMY_SPAWN,             // Servidor notifica aparición de enemigo
    ENEMY_DIED,              // Servidor notifica muerte de enemigo
    
    // Mensajes de habilidades (FASE 4)
    ABILITY_REQUEST,         // Cliente solicita usar habilidad
    ABILITY_RESULT,          // Servidor notifica resultado de habilidad
    ABILITY_COOLDOWN_UPDATE, // Servidor actualiza cooldowns
    
    // Mensajes de eventos aleatorios (FASE 4)
    RANDOM_EVENT_SPAWN,      // Servidor notifica aparición de evento
    EVENT_INTERACTION,       // Cliente elige opción de evento
    EVENT_RESULT,            // Servidor notifica resultado de evento
    
    // Mensajes de loot (FASE 4)
    LOOT_DISTRIBUTION,       // Servidor distribuye loot a jugadores
    INVENTORY_UPDATE,        // Servidor actualiza inventario de jugador
    
    // Mensajes de sistema
    ERROR,
    PING,
    PONG
}
