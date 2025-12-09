package com.juegito.game;

import com.juegito.game.inventory.InventoryManager;
import com.juegito.game.inventory.LootInventoryBridge;
import com.juegito.game.loot.LootSystem;
import com.juegito.model.GameMap;
import com.juegito.model.HexCoordinate;
import com.juegito.model.Player;
import com.juegito.protocol.dto.GameStateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestiona el estado completo del mundo del juego.
 * Mantiene toda la información necesaria para la simulación del juego.
 */
public class GameState {
    private static final Logger logger = LoggerFactory.getLogger(GameState.class);
    
    private final Map<String, Object> worldState;
    private final List<String> playerOrder;
    private final Set<String> playersWhoActedThisTurn;
    private final Map<String, Integer> playerHealth;
    private final Map<String, String> playerClass;
    private int currentTurnIndex;
    private int turnNumber;
    private boolean gameActive;
    
    // Sistema de mapa
    private GameMap gameMap;
    private MapGenerator mapGenerator;
    private MovementExecutor movementExecutor;
    
    // Sistema de inventario
    private InventoryManager inventoryManager;
    private LootSystem lootSystem;
    private LootInventoryBridge lootBridge;
    
    public GameState() {
        this.worldState = new ConcurrentHashMap<>();
        this.playerOrder = new ArrayList<>();
        this.playersWhoActedThisTurn = ConcurrentHashMap.newKeySet();
        this.playerHealth = new ConcurrentHashMap<>();
        this.playerClass = new ConcurrentHashMap<>();
        this.currentTurnIndex = 0;
        this.turnNumber = 0;
        this.gameActive = false;
        this.mapGenerator = new MapGenerator();
        this.inventoryManager = new InventoryManager(50);
        this.lootSystem = new LootSystem();
        this.lootBridge = new LootInventoryBridge(lootSystem, inventoryManager);
    }
    
    public void initializeGame(List<Player> players) {
        playerOrder.clear();
        players.forEach(p -> playerOrder.add(p.getPlayerId()));
        Collections.shuffle(playerOrder);
        
        playerHealth.clear();
        players.forEach(p -> playerHealth.put(p.getPlayerId(), 100));
        
        players.forEach(p -> inventoryManager.registerPlayer(p.getPlayerId()));
        
        currentTurnIndex = 0;
        turnNumber = 1;
        gameActive = true;
        
        worldState.clear();
        worldState.put("initialized", true);
        worldState.put("startTime", System.currentTimeMillis());
        
        generateMap(players.size());
        positionPlayersAtSpawns();
        
        logger.info("Game initialized with {} players", players.size());
    }
    
    private void generateMap(int playerCount) {
        gameMap = mapGenerator.generateMap(playerCount);
        movementExecutor = new MovementExecutor(gameMap);
        logger.info("Map generated for {} players", playerCount);
    }
    
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
            playersWhoActedThisTurn.clear();
        }
        logger.debug("Turn advanced to player {} (turn {})", getCurrentTurnPlayerId(), turnNumber);
    }
    
    public boolean registerPlayerAction(String playerId) {
        playersWhoActedThisTurn.add(playerId);
        boolean allActed = playersWhoActedThisTurn.size() >= playerOrder.size();
        if (allActed) {
            logger.info("Todos los jugadores actuaron - turno {} completo", turnNumber);
        }
        return allActed;
    }
    
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
    
    public MovementExecutor.MovementResult executePlayerMovement(
            String playerId, HexCoordinate destination) {
        
        if (movementExecutor == null) {
            logger.warn("Cannot execute movement: game not initialized");
            return MovementExecutor.MovementResult.failure("Juego no inicializado");
        }
        
        return movementExecutor.executeMovement(playerId, destination);
    }
    
    public List<HexCoordinate> getReachablePositions(String playerId) {
        if (movementExecutor == null) {
            return List.of();
        }
        return movementExecutor.getReachablePositions(playerId);
    }
    
    public int getPlayerHP(String playerId) {
        return playerHealth.getOrDefault(playerId, 100);
    }
    
    public void setPlayerHP(String playerId, int hp) {
        playerHealth.put(playerId, Math.max(0, hp));
    }
    
    public boolean applyDamage(String playerId, int damage) {
        int currentHP = getPlayerHP(playerId);
        int newHP = Math.max(0, currentHP - damage);
        setPlayerHP(playerId, newHP);
        logger.info("Player {} took {} damage ({} -> {})", playerId, damage, currentHP, newHP);
        return newHP == 0;
    }
    
    public void setPlayerClass(String playerId, String className) {
        playerClass.put(playerId, className);
    }
    
    public String getPlayerClass(String playerId) {
        return playerClass.getOrDefault(playerId, "guerrero");
    }
    
    public Map<String, Integer> getAllPlayerHealth() {
        return new HashMap<>(playerHealth);
    }
    
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    
    public LootInventoryBridge getLootBridge() {
        return lootBridge;
    }
    
    public LootSystem getLootSystem() {
        return lootSystem;
    }

    public GameStateDTO toDTO() {
        return new GameStateDTO(
            getCurrentTurnPlayerId(),
            turnNumber,
            getWorldState()
        );
    }
}
