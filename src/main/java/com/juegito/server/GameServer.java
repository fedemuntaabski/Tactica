package com.juegito.server;

import com.juegito.game.ActionValidator;
import com.juegito.game.GameState;
import com.juegito.game.Lobby;
import com.juegito.game.MovementExecutor;
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

/**
 * Servidor principal del juego.
 * Gestiona conexiones, lobby, estado del juego y comunicación con clientes.
 */
public class GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    
    private final int port;
    private final Lobby lobby;
    private final GameState gameState;
    private final ActionValidator actionValidator;
    private final Map<String, ClientHandler> clientHandlers;
    private final ExecutorService threadPool;
    private final Gson gson;
    
    private ServerSocket serverSocket;
    private volatile boolean running;
    private boolean gameStarted;
    
    public GameServer(int port, int minPlayers, int maxPlayers) {
        this.port = port;
        this.lobby = new Lobby(minPlayers, maxPlayers);
        this.gameState = new GameState();
        this.actionValidator = new ActionValidator(gameState);
        this.clientHandlers = new ConcurrentHashMap<>();
        this.threadPool = Executors.newCachedThreadPool();
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
        
        logger.info("Game server started on port {}", port);
        logger.info("Waiting for players... (min: {}, max: {})", 
            lobby.getMinPlayers(), lobby.getMaxPlayers());
        
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
        
        if (lobby.isFull()) {
            logger.warn("Rejecting connection - lobby full");
            clientSocket.close();
            return;
        }
        
        String playerId = UUID.randomUUID().toString();
        String playerName = "Player_" + (lobby.getPlayerCount() + 1);
        
        Player player = new Player(playerId, playerName, clientSocket);
        
        if (lobby.addPlayer(player)) {
            ClientHandler handler = new ClientHandler(player, this);
            clientHandlers.put(playerId, handler);
            threadPool.execute(handler);
            
            notifyPlayerConnected(player);
            broadcastLobbyState();
            
            logger.info("New player connected: {} ({})", playerName, playerId);
        } else {
            player.disconnect();
        }
    }
    
    private void notifyPlayerConnected(Player player) {
        PlayerConnectDTO dto = new PlayerConnectDTO(player.getPlayerName(), player.getPlayerId());
        Message message = new Message(MessageType.PLAYER_CONNECT, "server", dto);
        player.sendMessage(gson.toJson(message));
    }
    
    /**
     * Transmite el estado del lobby a todos los jugadores conectados.
     */
    public synchronized void broadcastLobbyState() {
        List<PlayerInfoDTO> playerInfos = new ArrayList<>();
        
        for (Player p : lobby.getPlayers()) {
            playerInfos.add(new PlayerInfoDTO(
                p.getPlayerId(),
                p.getPlayerName(),
                p.isReady()
            ));
        }
        
        LobbyStateDTO lobbyState = new LobbyStateDTO(
            playerInfos,
            lobby.getMaxPlayers(),
            gameStarted
        );
        
        Message message = new Message(MessageType.LOBBY_STATE, "server", lobbyState);
        broadcastMessage(message);
        
        checkGameStart();
    }
    
    private void checkGameStart() {
        if (!gameStarted && lobby.canStartGame()) {
            startGame();
        }
    }
    
    /**
     * Inicia el juego cuando se cumplen las condiciones.
     */
    private synchronized void startGame() {
        if (gameStarted) {
            return;
        }
        
        gameStarted = true;
        List<Player> players = lobby.getPlayers();
        gameState.initializeGame(players);
        
        logger.info("Starting game with {} players", players.size());
        
        Message startMessage = new Message(MessageType.START_GAME, "server", null);
        broadcastMessage(startMessage);
        
        broadcastGameState();
        notifyTurnStart();
    }
    
    /**
     * Transmite el estado del juego a todos los jugadores.
     */
    public synchronized void broadcastGameState() {
        GameStateDTO stateDTO = gameState.toDTO();
        Message message = new Message(MessageType.GAME_STATE, "server", stateDTO);
        broadcastMessage(message);
        
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
            broadcastMessage(message);
        }
    }
    
    private void notifyTurnStart() {
        String currentPlayerId = gameState.getCurrentTurnPlayerId();
        if (currentPlayerId != null) {
            Player player = lobby.getPlayer(currentPlayerId);
            if (player != null) {
                Message turnStart = new Message(MessageType.TURN_START, "server", 
                    Map.of("turnNumber", gameState.getTurnNumber()));
                player.sendMessage(gson.toJson(turnStart));
                
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
            Player player = lobby.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(gson.toJson(message));
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
        
        // Notificar que la acción es válida
        Player player = lobby.getPlayer(playerId);
        if (player != null) {
            Message validMsg = new Message(MessageType.ACTION_VALID, "server", action);
            player.sendMessage(gson.toJson(validMsg));
        }
        
        // Aquí se procesaría la lógica específica de cada acción
        // Por ahora, simplemente avanzamos el turno
        
        Message turnEnd = new Message(MessageType.TURN_END, "server", 
            Map.of("playerId", playerId, "action", action));
        broadcastMessage(turnEnd);
        
        gameState.advanceTurn();
        broadcastGameState();
        notifyTurnStart();
    }
    
    private void notifyInvalidAction(String playerId, String reason) {
        Player player = lobby.getPlayer(playerId);
        if (player != null) {
            Message invalidMsg = new Message(MessageType.ACTION_INVALID, "server", 
                Map.of("reason", reason));
            player.sendMessage(gson.toJson(invalidMsg));
            
            logger.debug("Invalid action from {}: {}", playerId, reason);
        }
    }
    
    /**
     * Maneja la desconexión de un jugador.
     */
    public synchronized void handlePlayerDisconnect(String playerId) {
        Player player = lobby.getPlayer(playerId);
        if (player == null) {
            return;
        }
        
        logger.info("Handling disconnect for player {}", playerId);
        
        ClientHandler handler = clientHandlers.remove(playerId);
        if (handler != null) {
            handler.stop();
        }
        
        player.disconnect();
        lobby.removePlayer(playerId);
        
        if (gameStarted) {
            handleDisconnectDuringGame(playerId);
        } else {
            broadcastLobbyState();
        }
    }
    
    private void handleDisconnectDuringGame(String playerId) {
        // Si el juego está en curso, notificar a todos
        Message disconnectMsg = new Message(MessageType.PLAYER_DISCONNECT, "server", 
            Map.of("playerId", playerId));
        broadcastMessage(disconnectMsg);
        
        // Si era el turno del jugador desconectado, avanzar
        if (playerId.equals(gameState.getCurrentTurnPlayerId())) {
            gameState.advanceTurn();
            broadcastGameState();
            notifyTurnStart();
        }
        
        // Si no quedan suficientes jugadores, terminar el juego
        if (lobby.getPlayerCount() < lobby.getMinPlayers()) {
            logger.warn("Not enough players, ending game");
            gameState.setGameActive(false);
            broadcastGameState();
        }
    }
    
    private void broadcastMessage(Message message) {
        String messageJson = gson.toJson(message);
        for (Player player : lobby.getPlayers()) {
            if (player.isConnected()) {
                player.sendMessage(messageJson);
            }
        }
    }
    
    /**
     * Detiene el servidor.
     */
    public void stop() {
        logger.info("Stopping server...");
        running = false;
        
        for (ClientHandler handler : clientHandlers.values()) {
            handler.stop();
        }
        
        for (Player player : lobby.getPlayers()) {
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
        
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            minPlayers = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            maxPlayers = Integer.parseInt(args[2]);
        }
        
        GameServer server = new GameServer(port, minPlayers, maxPlayers);
        
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        
        try {
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start server: {}", e.getMessage());
            System.exit(1);
        }
    }
}
