package com.juegito.client.ui;

import com.juegito.client.controllers.ActionController;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.PlayerLocalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Consola de debug textual para testing sin gráficos.
 * Permite ver estado del juego y enviar comandos manuales.
 * Implementa KISS: Interfaz simple para debugging.
 */
public class DebugConsole {
    private static final Logger logger = LoggerFactory.getLogger(DebugConsole.class);
    
    private final ClientGameState gameState;
    private final PlayerLocalState playerState;
    private final ConnectionManager connectionManager;
    private final ActionController actionController;
    
    private volatile boolean running;
    
    public DebugConsole(ClientGameState gameState, PlayerLocalState playerState,
                       ConnectionManager connectionManager, ActionController actionController) {
        this.gameState = gameState;
        this.playerState = playerState;
        this.connectionManager = connectionManager;
        this.actionController = actionController;
        this.running = false;
    }
    
    /**
     * Inicia la consola de debug en modo interactivo.
     */
    public void start() {
        running = true;
        Thread consoleThread = new Thread(this::runConsole, "DebugConsole");
        consoleThread.setDaemon(true);
        consoleThread.start();
        logger.info("Debug console started");
    }
    
    /**
     * Loop principal de la consola.
     */
    private void runConsole() {
        Scanner scanner = new Scanner(System.in);
        
        printHelp();
        
        while (running) {
            System.out.print("> ");
            
            if (!scanner.hasNextLine()) {
                break;
            }
            
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            processCommand(input);
        }
        
        scanner.close();
    }
    
    /**
     * Procesa un comando ingresado.
     */
    private void processCommand(String input) {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();
        
        try {
            switch (command) {
                case "help":
                case "h":
                    printHelp();
                    break;
                    
                case "status":
                case "s":
                    printStatus();
                    break;
                    
                case "player":
                case "p":
                    printPlayerInfo();
                    break;
                    
                case "turn":
                case "t":
                    printTurnInfo();
                    break;
                    
                case "move":
                case "m":
                    if (parts.length >= 3) {
                        int q = Integer.parseInt(parts[1]);
                        int r = Integer.parseInt(parts[2]);
                        actionController.executeMovement(q, r);
                    } else {
                        System.out.println("Uso: move <q> <r>");
                    }
                    break;
                    
                case "attack":
                case "a":
                    if (parts.length >= 2) {
                        actionController.executeAttack(parts[1]);
                    } else {
                        System.out.println("Uso: attack <targetId>");
                    }
                    break;
                    
                case "item":
                case "i":
                    if (parts.length >= 2) {
                        actionController.executeUseItem(parts[1]);
                    } else {
                        printInventory();
                    }
                    break;
                    
                case "map":
                    printMapInfo();
                    break;
                    
                case "quit":
                case "exit":
                case "q":
                    System.out.println("Saliendo...");
                    running = false;
                    break;
                    
                default:
                    System.out.println("Comando desconocido: " + command);
                    System.out.println("Usa 'help' para ver comandos disponibles");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            logger.error("Error processing command: {}", input, e);
        }
    }
    
    /**
     * Imprime ayuda de comandos.
     */
    private void printHelp() {
        System.out.println("\n=== CONSOLA DE DEBUG ===");
        System.out.println("Comandos disponibles:");
        System.out.println("  help, h         - Muestra esta ayuda");
        System.out.println("  status, s       - Estado general del juego");
        System.out.println("  player, p       - Información del jugador");
        System.out.println("  turn, t         - Información del turno actual");
        System.out.println("  move <q> <r>    - Mover a coordenadas hexagonales");
        System.out.println("  attack <id>     - Atacar objetivo");
        System.out.println("  item [id]       - Usar item o listar inventario");
        System.out.println("  map             - Información del mapa");
        System.out.println("  quit, exit, q   - Salir");
        System.out.println("========================\n");
    }
    
    /**
     * Imprime estado general del juego.
     */
    private void printStatus() {
        System.out.println("\n=== ESTADO DEL JUEGO ===");
        System.out.println("Fase: " + gameState.getCurrentPhase());
        System.out.println("Conectado: " + connectionManager.isConnected());
        System.out.println("ID Jugador: " + gameState.getPlayerId());
        System.out.println("Nombre: " + gameState.getPlayerName());
        System.out.println("Partida iniciada: " + gameState.isGameStarted());
        System.out.println("========================\n");
    }
    
    /**
     * Imprime información del jugador.
     */
    private void printPlayerInfo() {
        System.out.println("\n=== JUGADOR ===");
        System.out.println("ID: " + playerState.getPlayerId());
        System.out.println("Nombre: " + playerState.getPlayerName());
        System.out.println("HP: " + playerState.getCurrentHP() + "/" + playerState.getMaxHP());
        System.out.println("Estado: " + (playerState.isDead() ? "MUERTO" : "VIVO"));
        System.out.println("Posición: (" + playerState.getPosQ() + ", " + playerState.getPosR() + ")");
        System.out.println("Inventario: " + playerState.getInventorySize() + "/" + playerState.getMaxInventorySize());
        
        if (!playerState.getStatusEffects().isEmpty()) {
            System.out.println("Efectos activos:");
            playerState.getStatusEffects().forEach((effect, duration) -> 
                System.out.println("  - " + effect + " (" + duration + " turnos)")
            );
        }
        
        System.out.println("===============\n");
    }
    
    /**
     * Imprime información del turno.
     */
    private void printTurnInfo() {
        System.out.println("\n=== TURNO ===");
        System.out.println("Número de turno: " + gameState.getTurnNumber());
        System.out.println("Jugador actual: " + gameState.getCurrentTurnPlayerId());
        System.out.println("Es mi turno: " + (gameState.isMyTurn() ? "SÍ" : "NO"));
        System.out.println("=============\n");
    }
    
    /**
     * Imprime inventario del jugador.
     */
    private void printInventory() {
        System.out.println("\n=== INVENTARIO ===");
        System.out.println("Capacidad: " + playerState.getInventorySize() + "/" + playerState.getMaxInventorySize());
        
        if (playerState.getInventory().isEmpty()) {
            System.out.println("  (vacío)");
        } else {
            for (int i = 0; i < playerState.getInventory().size(); i++) {
                System.out.println("  " + (i + 1) + ". " + playerState.getInventory().get(i));
            }
        }
        
        System.out.println("==================\n");
    }
    
    /**
     * Imprime información del mapa.
     */
    private void printMapInfo() {
        System.out.println("\n=== MAPA ===");
        
        if (gameState.getGameMap() == null) {
            System.out.println("No hay información del mapa disponible");
        } else {
            var map = gameState.getGameMap();
            System.out.println("Tiles: " + (map.getTiles() != null ? map.getTiles().size() : 0));
            System.out.println("Jugadores en mapa: " + 
                (map.getPlayerPositions() != null ? map.getPlayerPositions().size() : 0));
        }
        
        System.out.println("============\n");
    }
    
    /**
     * Detiene la consola.
     */
    public void stop() {
        running = false;
        logger.info("Debug console stopped");
    }
}
