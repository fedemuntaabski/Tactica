package com.juegito.client.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Bus de eventos centralizado para comunicación entre componentes.
 * Implementa patrón Publish-Subscribe para desacoplar el sistema.
 * Thread-safe para uso concurrente.
 */
public class EventBus {
    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);
    
    private final Map<GameEventType, List<GameEventListener>> listeners;
    
    public EventBus() {
        this.listeners = new EnumMap<>(GameEventType.class);
    }
    
    /**
     * Registra un listener para un tipo de evento específico.
     */
    public void subscribe(GameEventType type, GameEventListener listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
        logger.debug("Listener subscribed to {}", type);
    }
    
    /**
     * Remueve un listener de un tipo de evento.
     */
    public void unsubscribe(GameEventType type, GameEventListener listener) {
        List<GameEventListener> typeListeners = listeners.get(type);
        if (typeListeners != null) {
            typeListeners.remove(listener);
        }
    }
    
    /**
     * Publica un evento a todos los listeners suscritos.
     */
    public void publish(GameEvent event) {
        List<GameEventListener> typeListeners = listeners.get(event.getType());
        if (typeListeners == null || typeListeners.isEmpty()) {
            logger.trace("No listeners for event type {}", event.getType());
            return;
        }
        
        logger.debug("Publishing event: {}", event.getType());
        
        for (GameEventListener listener : typeListeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                logger.error("Error in event listener for {}: {}", event.getType(), e.getMessage(), e);
            }
        }
    }
    
    /**
     * Limpia todos los listeners.
     */
    public void clear() {
        listeners.clear();
        logger.debug("All event listeners cleared");
    }
}
