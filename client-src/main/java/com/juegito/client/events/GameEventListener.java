package com.juegito.client.events;

/**
 * Listener para eventos del juego.
 * Los componentes implementan esta interfaz para recibir notificaciones.
 */
@FunctionalInterface
public interface GameEventListener {
    /**
     * Llamado cuando ocurre un evento.
     * 
     * @param event El evento que ocurrió
     */
    void onEvent(GameEvent event);
}
