package com.juegito.client;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.juegito.client.game.ActionExecutor;
import com.juegito.client.game.TurnManager;
import com.juegito.client.graphics.GameApplication;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.ServerUpdateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase principal del cliente del juego con LibGDX.
 * Coordina todos los componentes y gestiona el ciclo de vida de la aplicación.
 * 
 * FASE 3: Ahora usa LibGDX para interfaz gráfica en lugar de consola.
 */
public class GameClient {
    private static final Logger logger = LoggerFactory.getLogger(GameClient.class);
    
    private final ClientGameState gameState;
    private final ConnectionManager connectionManager;
    private final ActionExecutor actionExecutor;
    private final TurnManager turnManager;
    
    private GameApplication gameApplication;
    private volatile boolean running;
    
    public GameClient(String host, int port) {
        this.gameState = new ClientGameState();
        this.connectionManager = new ConnectionManager(host, port, gameState);
        this.actionExecutor = new ActionExecutor(connectionManager);
        this.turnManager = new TurnManager(gameState, actionExecutor);
        this.running = false;
        
        setupListeners();
    }
    
    private void setupListeners() {
        // Registrar listener de acciones
        ServerUpdateProcessor updateProcessor = connectionManager.getUpdateProcessor();
        updateProcessor.addStateChangeListener((type, data) -> {
            switch (type) {
                case ACTION_ACCEPTED:
                    turnManager.onActionResponse(true);
                    actionExecutor.onActionAccepted();
                    if (gameApplication != null) {
                        // Agregar al log del HUD cuando esté disponible
                        logger.info("Action accepted");
                    }
                    break;
                    
                case ACTION_REJECTED:
                    turnManager.onActionResponse(false);
                    String reason = data != null ? data.toString() : "Unknown";
                    actionExecutor.onActionRejected(reason);
                    if (gameApplication != null) {
                        logger.warn("Action rejected: {}", reason);
                    }
                    break;
                    
                default:
                    break;
            }
        });
        
        // Registrar listener de conexión
        connectionManager.addConnectionListener(state -> {
            logger.info("Connection state: {}", state);
            
            switch (state) {
                case CONNECTED:
                    logger.info("Connected to server");
                    break;
                    
                case DISCONNECTED:
                    logger.info("Disconnected from server");
                    break;
                    
                case CONNECTION_LOST:
                    logger.warn("Connection lost");
                    break;
                    
                case RECONNECTING:
                    logger.info("Attempting to reconnect...");
                    break;
                    
                case FAILED:
                    logger.error("Reconnection failed");
                    running = false;
                    break;
                    
                default:
                    break;
            }
        });
    }
    
    /**
     * Inicia el cliente y se conecta al servidor.
     */
    public void start() {
        logger.info("Starting game client with LibGDX...");
        running = true;
        
        // Conectar al servidor en background
        new Thread(() -> {
            if (!connectionManager.connect()) {
                logger.error("Failed to connect to server");
            }
        }).start();
        
        // Configurar LibGDX
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Juegito - Cliente");
        config.setWindowedMode(1280, 720);
        config.setResizable(true);
        config.useVsync(true);
        config.setForegroundFPS(60);
        
        // Crear aplicación LibGDX
        gameApplication = new GameApplication(gameState);
        
        // Inyectar dependencias después de un delay
        new Thread(() -> {
            try {
                Thread.sleep(500); // Esperar a que LibGDX inicialice
                gameApplication.setDependencies(actionExecutor, connectionManager);
                logger.info("Dependencies injected into GameApplication");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        new Lwjgl3Application(gameApplication, config);
    }
    
    /**
     * Detiene el cliente y cierra la conexión.
     */
    public void shutdown() {
        logger.info("Shutting down client...");
        running = false;
        connectionManager.disconnect();
    }
    
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto inválido, usando default: 8080");
            }
        }
        
        GameClient client = new GameClient(host, port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(client::shutdown));
        
        client.start();
    }
}
