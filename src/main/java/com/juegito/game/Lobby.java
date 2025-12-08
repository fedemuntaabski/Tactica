package com.juegito.game;

import com.juegito.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestiona el lobby del juego.
 * Maneja jugadores conectados antes del inicio de la partida.
 */
public class Lobby {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    
    private final Map<String, Player> players;
    private final int maxPlayers;
    private final int minPlayers;
    
    public Lobby(int minPlayers, int maxPlayers) {
        this.players = new ConcurrentHashMap<>();
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }
    
    public synchronized boolean addPlayer(Player player) {
        if (players.size() >= maxPlayers) {
            logger.warn("Lobby full, cannot add player {}", player.getPlayerId());
            return false;
        }
        
        players.put(player.getPlayerId(), player);
        logger.info("Player {} joined lobby ({}/{})", 
            player.getPlayerName(), players.size(), maxPlayers);
        return true;
    }
    
    public synchronized void removePlayer(String playerId) {
        Player removed = players.remove(playerId);
        if (removed != null) {
            logger.info("Player {} left lobby ({}/{})", 
                removed.getPlayerName(), players.size(), maxPlayers);
        }
    }
    
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }
    
    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
    
    public boolean canStartGame() {
        if (players.size() < minPlayers) {
            return false;
        }
        
        return players.values().stream().allMatch(Player::isReady);
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
}
