package com.juegito.client;

import com.juegito.client.game.ActionExecutor;
import com.juegito.client.game.TurnManager;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.ServerUpdateProcessor;
import com.juegito.client.ui.UIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Clase principal del cliente del juego.
 * Coordina todos los componentes y gestiona el ciclo de vida de la aplicación.
 */
public class GameClient {
    private static final Logger logger = LoggerFactory.getLogger(GameClient.class);
    
    private final ClientGameState gameState;
    private final ConnectionManager connectionManager;
    private final ActionExecutor actionExecutor;
    private final TurnManager turnManager;
    private final UIController uiController;
    
    private volatile boolean running;
    
    public GameClient(String host, int port) {
        this.gameState = new ClientGameState();
        this.connectionManager = new ConnectionManager(host, port, gameState);
        this.actionExecutor = new ActionExecutor(connectionManager);
        this.turnManager = new TurnManager(gameState, actionExecutor);
        this.uiController = new UIController(gameState, connectionManager, turnManager);
        this.running = false;
        
        setupListeners();
    }
    
    private void setupListeners() {
        // Registrar UI controller para recibir actualizaciones
        ServerUpdateProcessor updateProcessor = connectionManager.getUpdateProcessor();
        updateProcessor.addStateChangeListener(uiController);
        updateProcessor.addStateChangeListener(uiController.getLobbyScreen());
        
        // Registrar listener de acciones
        updateProcessor.addStateChangeListener((type, data) -> {
            switch (type) {
                case ACTION_ACCEPTED:
                    turnManager.onActionResponse(true);
                    actionExecutor.onActionAccepted();
                    break;
                    
                case ACTION_REJECTED:
                    turnManager.onActionResponse(false);
                    String reason = data != null ? data.toString() : "Unknown";
                    actionExecutor.onActionRejected(reason);
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
                    System.out.println("\n✓ Conectado al servidor");
                    break;
                    
                case DISCONNECTED:
                    System.out.println("\n✗ Desconectado del servidor");
                    break;
                    
                case CONNECTION_LOST:
                    System.out.println("\n⚠ Conexión perdida");
                    break;
                    
                case RECONNECTING:
                    System.out.println("\n↻ Intentando reconectar...");
                    break;
                    
                case FAILED:
                    System.out.println("\n✗ Falló la reconexión");
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
        logger.info("Starting game client...");
        running = true;
        
        displayWelcome();
        
        if (!connectionManager.connect()) {
            logger.error("Failed to connect to server");
            System.out.println("\nNo se pudo conectar al servidor. Verifica que esté ejecutándose.");
            return;
        }
        
        runInputLoop();
    }
    
    private void displayWelcome() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         JUEGITO - CLIENTE              ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    /**
     * Loop principal de entrada del usuario.
     */
    private void runInputLoop() {
        Scanner scanner = new Scanner(System.in);
        
        while (running && connectionManager.isConnected()) {
            try {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    processInput(input);
                }
                
                Thread.sleep(100);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in input loop: {}", e.getMessage());
            }
        }
        
        scanner.close();
        shutdown();
    }
    
    /**
     * Procesa la entrada del usuario.
     */
    private void processInput(String input) {
        switch (gameState.getCurrentPhase()) {
            case LOBBY:
                processLobbyInput(input);
                break;
                
            case PLAYING:
                processGameInput(input);
                break;
                
            default:
                break;
        }
    }
    
    private void processLobbyInput(String input) {
        switch (input) {
            case "r":
            case "ready":
                uiController.getLobbyScreen().toggleReady();
                break;
                
            case "u":
            case "unready":
                uiController.getLobbyScreen().toggleReady();
                break;
                
            case "q":
            case "quit":
                running = false;
                break;
                
            default:
                System.out.println("Comando no reconocido. Usa R (ready), U (unready) o Q (quit)");
                break;
        }
    }
    
    private void processGameInput(String input) {
        if (!turnManager.canPerformAction()) {
            if (!gameState.isMyTurn()) {
                System.out.println("No es tu turno.");
            } else {
                System.out.println("Esperando respuesta del servidor...");
            }
            return;
        }
        
        switch (input) {
            case "m":
            case "move":
                turnManager.executeAction("MOVE", null);
                System.out.println("Acción: Mover");
                break;
                
            case "a":
            case "attack":
                turnManager.executeAction("ATTACK", null);
                System.out.println("Acción: Atacar");
                break;
                
            case "d":
            case "defend":
                turnManager.executeAction("DEFEND", null);
                System.out.println("Acción: Defender");
                break;
                
            case "s":
            case "skip":
                turnManager.executeAction("SKIP", null);
                System.out.println("Acción: Saltar turno");
                break;
                
            case "q":
            case "quit":
                running = false;
                break;
                
            default:
                System.out.println("Comando no reconocido. Usa M (mover), A (atacar), D (defender), S (skip) o Q (quit)");
                break;
        }
    }
    
    /**
     * Detiene el cliente y cierra la conexión.
     */
    public void shutdown() {
        logger.info("Shutting down client...");
        running = false;
        connectionManager.disconnect();
        System.out.println("\n¡Hasta luego!");
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
