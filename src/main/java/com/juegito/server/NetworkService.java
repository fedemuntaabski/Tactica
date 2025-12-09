package com.juegito.server;

import com.juegito.model.Player;
import com.juegito.protocol.Message;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Servicio de red que maneja el broadcasting de mensajes.
 * Responsabilidad única: gestión de comunicación de red.
 */
public class NetworkService {
    private static final Logger logger = LoggerFactory.getLogger(NetworkService.class);
    
    private final Map<String, Player> players;
    private final Map<String, ClientHandler> clientHandlers;
    private final Gson gson;
    
    public NetworkService(Map<String, Player> players, Map<String, ClientHandler> clientHandlers) {
        this.players = players;
        this.clientHandlers = clientHandlers;
        this.gson = new Gson();
    }
    
    /**
     * Envía un mensaje a todos los jugadores conectados.
     */
    public void broadcastMessage(Message message) {
        String messageJson = gson.toJson(message);
        for (Player player : players.values()) {
            player.sendMessage(messageJson);
        }
        logger.debug("Broadcast message type: {}", message.getType());
    }
    
    /**
     * Envía un mensaje a un jugador específico.
     */
    public void sendMessageToPlayer(String playerId, Message message) {
        ClientHandler handler = clientHandlers.get(playerId);
        if (handler != null) {
            handler.sendMessage(message);
            logger.debug("Sent message type {} to player {}", message.getType(), playerId);
        } else {
            logger.warn("Cannot send message to player {}: handler not found", playerId);
        }
    }
    
    /**
     * Envía un mensaje a un jugador específico usando el objeto Player.
     */
    public void sendMessageToPlayer(Player player, Message message) {
        if (player != null) {
            player.sendMessage(gson.toJson(message));
            logger.debug("Sent message type {} to player {}", message.getType(), player.getPlayerId());
        }
    }
    
    /**
     * Envía un mensaje a una colección de jugadores.
     */
    public void broadcastToPlayers(Collection<Player> players, Message message) {
        String messageJson = gson.toJson(message);
        for (Player player : players) {
            player.sendMessage(messageJson);
        }
    }
}
