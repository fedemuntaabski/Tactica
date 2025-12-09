package com.juegito.client.network;

import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.LobbyClientState;
import com.juegito.client.state.ServerUpdateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el estado de la conexión con el servidor.
 * Maneja conexión, desconexión, reconexión y heartbeat.
 */
public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    
    private static final int RECONNECT_ATTEMPTS = 3;
    private static final int RECONNECT_DELAY_MS = 2000;
    private static final int HEARTBEAT_INTERVAL_MS = 5000;
    
    private final NetworkClient networkClient;
    private final MessageHandler messageHandler;
    private final ServerUpdateProcessor updateProcessor;
    private final ClientGameState gameState;
    private final LobbyClientState lobbyState;
    
    private Thread receiveThread;
    private Thread heartbeatThread;
    private volatile boolean running;
    
    private final List<ConnectionListener> listeners;
    
    public ConnectionManager(String host, int port, ClientGameState gameState, LobbyClientState lobbyState) {
        this.networkClient = new NetworkClient(host, port);
        this.messageHandler = new MessageHandler();
        this.gameState = gameState;
        this.lobbyState = lobbyState;
        this.updateProcessor = new ServerUpdateProcessor(gameState, lobbyState);
        this.listeners = new ArrayList<>();
        this.running = false;
    }
    
    /**
     * Conecta al servidor.
     */
    public boolean connect() {
        try {
            gameState.setCurrentPhase(ClientGameState.GamePhase.CONNECTING);
            notifyListeners(ConnectionState.CONNECTING);
            
            networkClient.connect();
            startReceiveThread();
            startHeartbeatThread();
            
            gameState.setCurrentPhase(ClientGameState.GamePhase.LOBBY);
            notifyListeners(ConnectionState.CONNECTED);
            
            logger.info("Successfully connected to server");
            return true;
            
        } catch (IOException e) {
            logger.error("Failed to connect: {}", e.getMessage());
            gameState.setCurrentPhase(ClientGameState.GamePhase.DISCONNECTED);
            notifyListeners(ConnectionState.DISCONNECTED);
            return false;
        }
    }
    
    /**
     * Intenta reconectar al servidor.
     */
    public boolean reconnect() {
        logger.info("Attempting to reconnect...");
        notifyListeners(ConnectionState.RECONNECTING);
        
        for (int attempt = 1; attempt <= RECONNECT_ATTEMPTS; attempt++) {
            logger.info("Reconnection attempt {}/{}", attempt, RECONNECT_ATTEMPTS);
            
            try {
                Thread.sleep(RECONNECT_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            
            if (connect()) {
                logger.info("Reconnected successfully");
                return true;
            }
        }
        
        logger.error("Failed to reconnect after {} attempts", RECONNECT_ATTEMPTS);
        notifyListeners(ConnectionState.FAILED);
        return false;
    }
    
    /**
     * Desconecta del servidor.
     */
    public void disconnect() {
        logger.info("Disconnecting...");
        
        running = false;
        
        // Enviar mensaje de desconexión si aún conectado
        if (networkClient.isConnected()) {
            Message disconnectMsg = messageHandler.createMessage(
                MessageType.PLAYER_DISCONNECT,
                gameState.getPlayerId(),
                null
            );
            sendMessage(disconnectMsg);
        }
        
        networkClient.disconnect();
        
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
        }
        
        gameState.setCurrentPhase(ClientGameState.GamePhase.DISCONNECTED);
        notifyListeners(ConnectionState.DISCONNECTED);
    }
    
    /**
     * Envía un mensaje al servidor.
     */
    public void sendMessage(Message message) {
        if (!networkClient.isConnected()) {
            logger.warn("Cannot send message: not connected");
            return;
        }
        
        networkClient.sendMessage(message);
    }
    
    /**
     * Inicia el hilo que recibe mensajes del servidor.
     */
    private void startReceiveThread() {
        running = true;
        receiveThread = new Thread(() -> {
            logger.debug("Receive thread started");
            
            while (running && networkClient.isConnected()) {
                try {
                    String messageJson = networkClient.receiveMessage();
                    
                    if (messageJson == null) {
                        logger.warn("Connection closed by server");
                        handleConnectionLost();
                        break;
                    }
                    
                    Message message = messageHandler.parseMessage(messageJson);
                    if (message != null) {
                        updateProcessor.processMessage(message);
                    }
                    
                } catch (IOException e) {
                    if (running) {
                        logger.error("Error receiving message: {}", e.getMessage());
                        handleConnectionLost();
                    }
                    break;
                }
            }
            
            logger.debug("Receive thread stopped");
        }, "ReceiveThread");
        
        receiveThread.start();
    }
    
    /**
     * Inicia el hilo de heartbeat (ping/pong).
     */
    private void startHeartbeatThread() {
        heartbeatThread = new Thread(() -> {
            logger.debug("Heartbeat thread started");
            
            while (running && networkClient.isConnected()) {
                try {
                    Thread.sleep(HEARTBEAT_INTERVAL_MS);
                    
                    if (networkClient.isConnected()) {
                        Message ping = messageHandler.createMessage(
                            MessageType.PING,
                            gameState.getPlayerId(),
                            null
                        );
                        sendMessage(ping);
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            logger.debug("Heartbeat thread stopped");
        }, "HeartbeatThread");
        
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }
    
    /**
     * Maneja la pérdida de conexión.
     */
    private void handleConnectionLost() {
        logger.warn("Connection lost");
        running = false;
        notifyListeners(ConnectionState.CONNECTION_LOST);
        
        // Intentar reconectar automáticamente
        if (gameState.getCurrentPhase() != ClientGameState.GamePhase.DISCONNECTED) {
            reconnect();
        }
    }
    
    public ServerUpdateProcessor getUpdateProcessor() {
        return updateProcessor;
    }
    
    public boolean isConnected() {
        return networkClient.isConnected();
    }
    
    /**
     * Registra un listener de cambios de conexión.
     */
    public void addConnectionListener(ConnectionListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remueve un listener.
     */
    public void removeConnectionListener(ConnectionListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(ConnectionState state) {
        for (ConnectionListener listener : listeners) {
            try {
                listener.onConnectionStateChanged(state);
            } catch (Exception e) {
                logger.error("Error in connection listener: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Interfaz para listeners de estado de conexión.
     */
    public interface ConnectionListener {
        void onConnectionStateChanged(ConnectionState state);
    }
    
    /**
     * Estados de conexión.
     */
    public enum ConnectionState {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        RECONNECTING,
        CONNECTION_LOST,
        FAILED
    }
}
