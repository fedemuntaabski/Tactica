package com.juegito.client.game;

import com.juegito.client.network.ConnectionManager;
import com.juegito.client.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejecuta acciones del jugador y las envía al servidor.
 * Mantiene un registro de acciones pendientes.
 */
public class ActionExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ActionExecutor.class);
    
    private final ConnectionManager connectionManager;
    private final List<ActionListener> listeners;
    
    public ActionExecutor(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Envía una acción al servidor.
     */
    public void sendAction(Message actionMessage) {
        if (!connectionManager.isConnected()) {
            logger.error("Cannot send action: not connected");
            notifyListeners(ActionResult.FAILED, "Not connected to server");
            return;
        }
        
        try {
            connectionManager.sendMessage(actionMessage);
            notifyListeners(ActionResult.SENT, null);
            logger.debug("Action sent to server");
            
        } catch (Exception e) {
            logger.error("Error sending action: {}", e.getMessage());
            notifyListeners(ActionResult.FAILED, e.getMessage());
        }
    }
    
    /**
     * Notifica resultado de acción aceptada por servidor.
     */
    public void onActionAccepted() {
        notifyListeners(ActionResult.ACCEPTED, null);
    }
    
    /**
     * Notifica resultado de acción rechazada por servidor.
     */
    public void onActionRejected(String reason) {
        notifyListeners(ActionResult.REJECTED, reason);
    }
    
    /**
     * Registra un listener de resultados de acciones.
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remueve un listener.
     */
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(ActionResult result, String message) {
        for (ActionListener listener : listeners) {
            try {
                listener.onActionResult(result, message);
            } catch (Exception e) {
                logger.error("Error in action listener: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Interfaz para listeners de resultados de acciones.
     */
    public interface ActionListener {
        void onActionResult(ActionResult result, String message);
    }
    
    /**
     * Resultados posibles de una acción.
     */
    public enum ActionResult {
        SENT,       // Acción enviada al servidor
        ACCEPTED,   // Acción aceptada por el servidor
        REJECTED,   // Acción rechazada por el servidor
        FAILED      // Error al enviar
    }
}
