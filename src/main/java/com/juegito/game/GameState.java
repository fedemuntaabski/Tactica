package com.juegito.game;

import com.juegito.model.*;
import com.juegito.protocol.dto.GameStateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestiona el estado completo del mundo del juego.
 * Mantiene toda la información necesaria para la simulación del juego.
 */
public class GameState {
    private static final Logger logger = LoggerFactory.getLogger(GameState.class);
    
    private final Map<String, Object> worldState;
    private final List<String> playerOrder;
    private final Set<String> playersWhoActedThisTurn; // Count check: track qui\u00e9n actu\u00f3
    private int currentTurnIndex;
    private int turnNumber;
    private boolean gameActive;
    
    // Sistema de mapa
    private GameMap gameMap;
    private MapGenerator mapGenerator;
    private MovementExecutor movementExecutor;
    
    public GameState() {
        this.worldState = new ConcurrentHashMap<>();
        this.playerOrder = new ArrayList<>();
        this.playersWhoActedThisTurn = ConcurrentHashMap.newKeySet();
        this.currentTurnIndex = 0;
        this.turnNumber = 0;
        this.gameActive = false;
        this.mapGenerator = new MapGenerator();
    }
    
    public void initializeGame(List<Player> players) {
        playerOrder.clear();
        players.forEach(p -> playerOrder.add(p.getPlayerId()));
        Collections.shuffle(playerOrder);
        
        currentTurnIndex = 0;
        turnNumber = 1;
        gameActive = true;
        
        worldState.clear();
        worldState.put("initialized", true);
        worldState.put("startTime", System.currentTimeMillis());
        
        // Generar el mapa del juego
        generateMap(players.size());
        
        // Posicionar jugadores en spawns
        positionPlayersAtSpawns();
        
        logger.info("Game initialized with {} players", players.size());
    }
    
    /**
     * Genera el mapa del juego según el número de jugadores.
     */
    private void generateMap(int playerCount) {
        gameMap = mapGenerator.generateMap(playerCount);
        movementExecutor = new MovementExecutor(gameMap);
        logger.info("Map generated for {} players", playerCount);
    }
    
    /**
     * Posiciona a los jugadores en los puntos de spawn.
     */
    private void positionPlayersAtSpawns() {
        List<HexCoordinate> spawns = gameMap.getSpawnPoints();
        
        for (int i = 0; i < playerOrder.size() && i < spawns.size(); i++) {
            String playerId = playerOrder.get(i);
            HexCoordinate spawnPoint = spawns.get(i);
            gameMap.placePlayer(playerId, spawnPoint);
            
            logger.info("Player {} spawned at {}", playerId, spawnPoint);
        }
    }
    
    public String getCurrentTurnPlayerId() {
        if (playerOrder.isEmpty()) {
            return null;
        }
        return playerOrder.get(currentTurnIndex);
    }
    
    public void advanceTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % playerOrder.size();
        if (currentTurnIndex == 0) {
            turnNumber++;
            playersWhoActedThisTurn.clear(); // Nuevo turno, resetear contador
        }
        logger.debug("Turn advanced to player {} (turn {})", getCurrentTurnPlayerId(), turnNumber);
    }
    
    /**
     * Registra que un jugador actu\u00f3 en este turno.
     * @return true si TODOS los jugadores ya actuaron (count check completo)
     */
    public boolean registerPlayerAction(String playerId) {
        playersWhoActedThisTurn.add(playerId);
        boolean allActed = playersWhoActedThisTurn.size() >= playerOrder.size();
        if (allActed) {
            logger.info("Todos los jugadores actuaron - turno {} completo", turnNumber);
        }
        return allActed;
    }
    
    /**
     * Verifica si todos los jugadores actuaron en este turno.
     */
    public boolean haveAllPlayersActed() {
        return playersWhoActedThisTurn.size() >= playerOrder.size();
    }
    
    public int getTurnNumber() {
        return turnNumber;
    }
    
    public boolean isGameActive() {
        return gameActive;
    }
    
    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }
    
    public void updateWorldState(String key, Object value) {
        worldState.put(key, value);
    }
    
    public Object getWorldStateValue(String key) {
        return worldState.get(key);
    }
    
    public Map<String, Object> getWorldState() {
        return new HashMap<>(worldState);
    }
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public MovementExecutor getMovementExecutor() {
        return movementExecutor;
    }
    
    /**
     * Ejecuta un movimiento de jugador en el mapa.
     */
    public MovementExecutor.MovementResult executePlayerMovement(
            String playerId, HexCoordinate destination) {
        
        if (movementExecutor == null) {
            logger.warn("Cannot execute movement: game not initialized");
            return MovementExecutor.MovementResult.failure("Juego no inicializado");
        }
        
        return movementExecutor.executeMovement(playerId, destination);
    }
    
    /**
     * Obtiene las posiciones alcanzables por un jugador.
     */
    public List<HexCoordinate> getReachablePositions(String playerId) {
        if (movementExecutor == null) {
            return List.of();
        }
        return movementExecutor.getReachablePositions(playerId);
    }
    
    public GameStateDTO toDTO() {
        return new GameStateDTO(
            getCurrentTurnPlayerId(),
            turnNumber,
            getWorldState()
        );
    }
}
