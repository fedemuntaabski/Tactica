package com.juegito.game;

import com.juegito.model.Player;
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
    private int currentTurnIndex;
    private int turnNumber;
    private boolean gameActive;
    
    public GameState() {
        this.worldState = new ConcurrentHashMap<>();
        this.playerOrder = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.turnNumber = 0;
        this.gameActive = false;
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
        
        logger.info("Game initialized with {} players", players.size());
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
        }
        logger.debug("Turn advanced to player {} (turn {})", getCurrentTurnPlayerId(), turnNumber);
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
    
    public GameStateDTO toDTO() {
        return new GameStateDTO(
            getCurrentTurnPlayerId(),
            turnNumber,
            getWorldState()
        );
    }
}
