package com.juegito.server;

import com.juegito.game.ActionValidator;
import com.juegito.game.GameState;
import com.juegito.game.MovementExecutor;
import com.juegito.game.lobby.LobbyManager;
import com.juegito.game.lobby.PlayerLobbyData;
import com.juegito.model.HexCoordinate;
import com.juegito.model.Player;
import com.juegito.protocol.MapDTOConverter;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Servidor principal del juego.
 * Gestiona conexiones, lobby, estado del juego y comunicación con clientes.
 */
public class GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private static final long GAME_HEARTBEAT_INTERVAL_MS = 3000; // Heartbeat cada 3 segundos
    
    private final int port;
    private final LobbyManager lobbyManager;
    private final NetworkService networkService;
    private final GameState gameState;
    private final ActionValidator actionValidator;
    private final Map<String, ClientHandler> clientHandlers;
    private final Map<String, Player> players; // Networking layer
    private final ExecutorService threadPool;
    private final ScheduledExecutorService gameHeartbeatScheduler;
    private final Gson gson;
    
    private ServerSocket serverSocket;
    private volatile boolean running;
    private boolean gameStarted;
    private final int minPlayers;
    private final int maxPlayers;
    
    public GameServer(int port, int minPlayers, int maxPlayers, String hostName) {
        this.port = port;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        
        this.clientHandlers = new ConcurrentHashMap<>();
        this.players = new ConcurrentHashMap<>();
        this.networkService = new NetworkService(players, clientHandlers);
        
        // Inicializar el LobbyManager sin jugador host (el primer cliente será el host)
        this.lobbyManager = new LobbyManager(networkService::sendMessageToPlayer);
        
        this.gameState = new GameState();
        this.actionValidator = new ActionValidator(gameState);
        this.threadPool = Executors.newCachedThreadPool();
        this.gameHeartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        this.gson = new Gson();
        this.running = false;
        this.gameStarted = false;
    }
    
    /**
     * Inicia el servidor y comienza a aceptar conexiones.
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        gameStarted = false;
        
        // Iniciar el LobbyManager
        lobbyManager.start();
        
        logger.info("Game server started on port {}", port);
        logger.info("Waiting for players... (min: {}, max: {})", 
            minPlayers, maxPlayers);
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleNewConnection(clientSocket);
            } catch (IOException e) {
                if (running) {
                    logger.error("Error accepting connection: {}", e.getMessage());
                }
            }
        }
    }
    
    private void handleNewConnection(Socket clientSocket) throws IOException {
        if (gameStarted) {
            logger.warn("Rejecting connection - game already started");
            clientSocket.close();
            return;
        }
        
        if (lobbyManager.isFull()) {
            logger.warn("Rejecting connection - lobby full");
            clientSocket.close();
            return;
        }
        
        String playerId = UUID.randomUUID().toString();
        String playerName = "Player_" + (lobbyManager.getPlayerCount() + 1);
        
        Player player = new Player(playerId, playerName, clientSocket);
        players.put(playerId, player);
        
        ClientHandler handler = new ClientHandler(player, this);
        clientHandlers.put(playerId, handler);
        threadPool.execute(handler);
        
        // Agregar jugador al lobby automáticamente
        String ipAddress = clientSocket.getInetAddress().getHostAddress();
        lobbyManager.autoAddPlayer(playerId, playerName, ipAddress);
        
        notifyPlayerConnected(player);
        
        logger.info("New player connected: {} ({})", playerName, playerId);
    }
    
    private void notifyPlayerConnected(Player player) {
        PlayerConnectDTO dto = new PlayerConnectDTO(player.getPlayerName(), player.getPlayerId());
        Message message = new Message(MessageType.PLAYER_CONNECT, "server", dto);
        networkService.sendMessageToPlayer(player, message);
    }
    
    /**
     * Inicia el juego cuando se cumplen las condiciones.
     */
    private synchronized void startGame() {
        if (gameStarted) {
            return;
        }
        
        gameStarted = true;
        List<Player> playerList = new ArrayList<>(players.values());
        gameState.initializeGame(playerList);
        
        logger.info("Starting game with {} players", playerList.size());
        
        Message startMessage = new Message(MessageType.START_GAME, "server", null);
        networkService.broadcastMessage(startMessage);
        
        broadcastGameState();
        notifyTurnStart();
        
        // Iniciar heartbeat periódico del juego
        startGameHeartbeat();
    }
    
    /**
     * Inicia el heartbeat periódico del estado del juego.
     * Envía información resumida cada GAME_HEARTBEAT_INTERVAL_MS.
     */
    private void startGameHeartbeat() {
        gameHeartbeatScheduler.scheduleAtFixedRate(
            this::broadcastGameHeartbeat,
            GAME_HEARTBEAT_INTERVAL_MS,
            GAME_HEARTBEAT_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
        logger.info("Game heartbeat started - broadcasting every {}ms", GAME_HEARTBEAT_INTERVAL_MS);
    }
    
    /**
     * Envía heartbeat periódico con estado resumido del juego.
     * DRY: Método centralizado para enviar información periódica.
     */
    private synchronized void broadcastGameHeartbeat() {
        if (!gameStarted || !running) {
            return;
        }
        
        // Crear heartbeat con información resumida
        Map<String, Integer> playerHP = new HashMap<>();
        for (Player player : players.values()) {
            // TODO: Obtener HP real de cada jugador del gameState
            playerHP.put(player.getPlayerId(), 100); // Placeholder
        }
        
        GameHeartbeatDTO heartbeat = new GameHeartbeatDTO(
            gameState.getTurnNumber(),
            gameState.getCurrentTurnPlayerId(),
            playerHP
        );
        
        Message message = new Message(MessageType.GAME_HEARTBEAT, "server", heartbeat);
        networkService.broadcastMessage(message);
        
        logger.trace("Game heartbeat sent - Turn: {}", gameState.getTurnNumber());
    }
    
    /**
     * Transmite el estado del juego a todos los jugadores.
     */
    public synchronized void broadcastGameState() {
        GameStateDTO stateDTO = gameState.toDTO();
        Message message = new Message(MessageType.GAME_STATE, "server", stateDTO);
        networkService.broadcastMessage(message);
        
        // También enviar el estado del mapa
        broadcastMapState();
    }
    
    /**
     * Transmite el estado del mapa a todos los jugadores.
     */
    public synchronized void broadcastMapState() {
        if (gameState.getGameMap() != null) {
            GameMapDTO mapDTO = MapDTOConverter.toDTO(gameState.getGameMap());
            Message message = new Message(MessageType.MAP_STATE, "server", mapDTO);
            networkService.broadcastMessage(message);
        }
    }
    
    private void notifyTurnStart() {
        String currentPlayerId = gameState.getCurrentTurnPlayerId();
        if (currentPlayerId != null) {
            Player player = players.get(currentPlayerId);
            if (player != null) {
                Message turnStart = new Message(MessageType.TURN_START, "server", 
                    Map.of("turnNumber", gameState.getTurnNumber()));
                networkService.sendMessageToPlayer(player, turnStart);
                
                logger.debug("Turn started for player {}", currentPlayerId);
            }
        }
    }
    
    /**
     * Maneja una acción recibida de un jugador.
     */
    public void handlePlayerAction(String playerId, PlayerActionDTO action) {
        // Manejar movimiento si es de ese tipo
        if ("MOVE".equals(action.getActionType())) {
            handleMovementAction(playerId, action);
            return;
        }
        
        // Otras acciones
        ActionValidator.ValidationResult result = actionValidator.validate(playerId, action);
        
        if (result.isValid()) {
            processValidAction(playerId, action);
        } else {
            notifyInvalidAction(playerId, result.getReason());
        }
    }
    
    /**
     * Maneja una acción de movimiento.
     */
    private void handleMovementAction(String playerId, PlayerActionDTO action) {
        Object rawActionData = action.getActionData();
        if (!(rawActionData instanceof Map)) {
            notifyInvalidAction(playerId, "Datos de movimiento inválidos");
            return;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> actionData = (Map<String, Object>) rawActionData;
        if (actionData == null) {
            notifyInvalidAction(playerId, "Datos de movimiento faltantes");
            return;
        }
        
        // Extraer coordenadas de destino
        Object qObj = actionData.get("q");
        Object rObj = actionData.get("r");
        
        if (qObj == null || rObj == null) {
            notifyInvalidAction(playerId, "Coordenadas de destino inválidas");
            return;
        }
        
        int q = ((Number) qObj).intValue();
        int r = ((Number) rObj).intValue();
        HexCoordinate destination = new HexCoordinate(q, r);
        
        // Ejecutar movimiento
        MovementExecutor.MovementResult result = 
            gameState.executePlayerMovement(playerId, destination);
        
        if (result.isSuccess()) {
            // Notificar resultado al jugador
            MovementDTO movementDTO = MapDTOConverter.toDTO(playerId, result);
            Message message = new Message(MessageType.MOVEMENT_RESULT, "server", movementDTO);
            Player player = players.get(playerId);
            if (player != null) {
                networkService.sendMessageToPlayer(player, message);
            }
            
            // Actualizar mapa para todos
            broadcastMapState();
            
            // Avanzar turno
            gameState.advanceTurn();
            broadcastGameState();
            notifyTurnStart();
        } else {
            notifyInvalidAction(playerId, result.getMessage());
        }
    }
    
    private void processValidAction(String playerId, PlayerActionDTO action) {
        logger.info("Processing action {} from player {}", action.getActionType(), playerId);
        
        // Registrar que este jugador actuó (count check)
        boolean allActed = gameState.registerPlayerAction(playerId);
        
        // Notificar que la acción es válida
        Player player = players.get(playerId);
        if (player != null) {
            Message validMsg = new Message(MessageType.ACTION_VALID, "server", action);
            networkService.sendMessageToPlayer(player, validMsg);
        }
        
        // Aquí se procesaría la lógica específica de cada acción
        // Por ahora, simplemente broadcast de la acción
        
        Message turnEnd = new Message(MessageType.TURN_END, "server", 
            Map.of("playerId", playerId, "action", action));
        networkService.broadcastMessage(turnEnd);
        
        // Si todos actuaron, avanzar el turno automáticamente (count check)
        if (allActed) {
            logger.info("Count check completo - todos actuaron, avanzando turno");
            gameState.advanceTurn();
            broadcastGameState();
            notifyTurnStart();
        }
    }
    
    private void notifyInvalidAction(String playerId, String reason) {
        Player player = players.get(playerId);
        if (player != null) {
            Message invalidMsg = new Message(MessageType.ACTION_INVALID, "server", 
                Map.of("reason", reason));
            networkService.sendMessageToPlayer(player, invalidMsg);
            
            logger.debug("Invalid action from {}: {}", playerId, reason);
        }
    }
    
    /**
     * Maneja la desconexión de un jugador.
     */
    public synchronized void handlePlayerDisconnect(String playerId) {
        Player player = players.get(playerId);
        if (player == null) {
            return;
        }
        
        logger.info("Handling disconnect for player {}", playerId);
        
        ClientHandler handler = clientHandlers.remove(playerId);
        if (handler != null) {
            handler.stop();
        }
        
        player.disconnect();
        players.remove(playerId);
        
        // Notificar al lobby manager
        lobbyManager.handlePlayerDisconnected(playerId);
        
        if (gameStarted) {
            handleDisconnectDuringGame(playerId);
        }
    }
    
    private void handleDisconnectDuringGame(String playerId) {
        // Si el juego está en curso, notificar a todos
        Message disconnectMsg = new Message(MessageType.PLAYER_DISCONNECT, "server", 
            Map.of("playerId", playerId));
        networkService.broadcastMessage(disconnectMsg);
        
        // Si era el turno del jugador desconectado, avanzar
        if (playerId.equals(gameState.getCurrentTurnPlayerId())) {
            gameState.advanceTurn();
            broadcastGameState();
            notifyTurnStart();
        }
        
        // Si no quedan suficientes jugadores, terminar el juego
        if (lobbyManager.getPlayerCount() < minPlayers) {
            logger.warn("Not enough players, ending game");
            gameState.setGameActive(false);
            broadcastGameState();
        }
    }
    
    /**
     * Envía resincronización completa a un jugador.
     * KISS: Envía todo el estado necesario en un solo mensaje.
     */
    public void sendFullResync(String playerId) {
        Player player = players.get(playerId);
        if (player == null) {
            logger.warn("Cannot send resync to unknown player: {}", playerId);
            return;
        }
        
        logger.info("Sending full resync to player {}", playerId);
        
        // Enviar estado del juego
        GameStateDTO stateDTO = gameState.toDTO();
        Message gameStateMsg = new Message(MessageType.FULL_RESYNC, "server", stateDTO);
        networkService.sendMessageToPlayer(player, gameStateMsg);
        
        // Enviar estado del mapa
        if (gameState.getGameMap() != null) {
            GameMapDTO mapDTO = MapDTOConverter.toDTO(gameState.getGameMap());
            Message mapMsg = new Message(MessageType.MAP_STATE, "server", mapDTO);
            networkService.sendMessageToPlayer(player, mapMsg);
        }
        
        // Enviar turno actual
        Message turnMsg = new Message(MessageType.TURN_START, "server", 
            Map.of("turnNumber", gameState.getTurnNumber()));
        networkService.sendMessageToPlayer(player, turnMsg);
        
        logger.info("Full resync sent to player {}", playerId);
    }
    
    /**
     * Maneja solicitud de reconexión de un jugador.
     * Retorna true si se acepta la reconexión.
     */
    public boolean handleReconnect(String playerId) {
        // Validar que el jugador existe y el juego está activo
        if (!gameStarted || !running) {
            logger.warn("Reconnect rejected for {}: game not active", playerId);
            return false;
        }
        
        if (!players.containsKey(playerId)) {
            logger.warn("Reconnect rejected for {}: unknown player", playerId);
            return false;
        }
        
        logger.info("Reconnect accepted for player {}", playerId);
        return true;
    }
    
    /**
     * Obtiene el LobbyManager.
     */
    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
    
    /**
     * Detiene el servidor.
     */
    public void stop() {
        logger.info("Stopping server...");
        running = false;
        
        // Detener heartbeat del juego
        gameHeartbeatScheduler.shutdown();
        try {
            if (!gameHeartbeatScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                gameHeartbeatScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            gameHeartbeatScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Detener el lobby manager
        lobbyManager.stop();
        
        for (ClientHandler handler : clientHandlers.values()) {
            handler.stop();
        }
        
        for (Player player : players.values()) {
            player.disconnect();
        }
        
        threadPool.shutdown();
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error("Error closing server socket: {}", e.getMessage());
        }
        
        logger.info("Server stopped");
    }
    
    public static void main(String[] args) {
        int port = 8080;
        int minPlayers = 2;
        int maxPlayers = 4;
        String hostName = "Host";
        
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            minPlayers = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            maxPlayers = Integer.parseInt(args[2]);
        }
        if (args.length >= 4) {
            hostName = args[3];
        }
        
        GameServer server = new GameServer(port, minPlayers, maxPlayers, hostName);
        
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        try {
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start server: {}", e.getMessage());
            System.exit(1);
        }
    }
}
