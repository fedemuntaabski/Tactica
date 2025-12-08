package com.juegito.client.state;

import com.juegito.client.protocol.dto.GameStateDTO;
import com.juegito.client.protocol.dto.LobbyStateDTO;
import com.juegito.client.protocol.dto.PlayerInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mantiene el estado local del juego en el cliente.
 * Sincronizado con las actualizaciones del servidor.
 */
public class ClientGameState {
    private static final Logger logger = LoggerFactory.getLogger(ClientGameState.class);
    
    // Estado del jugador local
    private String playerId;
    private String playerName;
    private boolean ready;
    
    // Estado del lobby
    private List<PlayerInfoDTO> lobbyPlayers;
    private int maxPlayers;
    private boolean gameStarted;
    
    // Estado del juego
    private String currentTurnPlayerId;
    private int turnNumber;
    private Map<String, Object> worldState;
    private GamePhase currentPhase;
    
    public ClientGameState() {
        this.lobbyPlayers = new ArrayList<>();
        this.worldState = new HashMap<>();
        this.currentPhase = GamePhase.DISCONNECTED;
        this.ready = false;
        this.gameStarted = false;
        this.turnNumber = 0;
    }
    
    /**
     * Actualiza el estado del lobby con información del servidor.
     */
    public void updateLobbyState(LobbyStateDTO lobbyState) {
        this.lobbyPlayers = new ArrayList<>(lobbyState.getPlayers());
        this.maxPlayers = lobbyState.getMaxPlayers();
        this.gameStarted = lobbyState.isGameStarted();
        
        // Actualizar estado de ready del jugador local
        lobbyPlayers.stream()
            .filter(p -> p.getPlayerId().equals(playerId))
            .findFirst()
            .ifPresent(p -> this.ready = p.isReady());
        
        logger.debug("Lobby state updated: {} players", lobbyPlayers.size());
    }
    
    /**
     * Actualiza el estado del juego con información del servidor.
     */
    public void updateGameState(GameStateDTO gameState) {
        this.currentTurnPlayerId = gameState.getCurrentTurnPlayerId();
        this.turnNumber = gameState.getTurnNumber();
        
        if (gameState.getWorldState() != null) {
            this.worldState = new HashMap<>(gameState.getWorldState());
        }
        
        logger.debug("Game state updated: turn {}, current player: {}", 
            turnNumber, currentTurnPlayerId);
    }
    
    /**
     * Verifica si es el turno del jugador local.
     */
    public boolean isMyTurn() {
        return playerId != null && playerId.equals(currentTurnPlayerId);
    }
    
    /**
     * Obtiene información del jugador local del lobby.
     */
    public PlayerInfoDTO getLocalPlayerInfo() {
        return lobbyPlayers.stream()
            .filter(p -> p.getPlayerId().equals(playerId))
            .findFirst()
            .orElse(null);
    }
    
    // Getters y setters
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public List<PlayerInfoDTO> getLobbyPlayers() {
        return new ArrayList<>(lobbyPlayers);
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
    
    public String getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }
    
    public int getTurnNumber() {
        return turnNumber;
    }
    
    public Map<String, Object> getWorldState() {
        return new HashMap<>(worldState);
    }
    
    public Object getWorldStateValue(String key) {
        return worldState.get(key);
    }
    
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    public void setCurrentPhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
        logger.info("Phase changed to: {}", currentPhase);
    }
    
    /**
     * Fases del juego desde la perspectiva del cliente.
     */
    public enum GamePhase {
        DISCONNECTED,
        CONNECTING,
        LOBBY,
        STARTING,
        PLAYING,
        GAME_OVER
    }
}
