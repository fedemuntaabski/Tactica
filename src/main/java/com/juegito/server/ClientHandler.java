package com.juegito.server;

import com.juegito.model.Player;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.PlayerActionDTO;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Maneja la comunicación con un cliente individual.
 * Ejecuta en su propio hilo para cada jugador conectado.
 */
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    
    private final Player player;
    private final GameServer server;
    private final Gson gson;
    private volatile boolean running;
    
    public ClientHandler(Player player, GameServer server) {
        this.player = player;
        this.server = server;
        this.gson = new Gson();
        this.running = true;
    }
    
    @Override
    public void run() {
        logger.info("Client handler started for player {}", player.getPlayerId());
        
        try {
            while (running && player.isConnected()) {
                String messageJson = player.receiveMessage();
                
                if (messageJson == null) {
                    // Cliente desconectado
                    handleDisconnect();
                    break;
                }
                
                processMessage(messageJson);
            }
        } catch (IOException e) {
            logger.error("Error reading from client {}: {}", player.getPlayerId(), e.getMessage());
            handleDisconnect();
        }
    }
    
    private void processMessage(String messageJson) {
        try {
            Message message = gson.fromJson(messageJson, Message.class);
            
            if (message == null || message.getType() == null) {
                logger.warn("Received invalid message from {}", player.getPlayerId());
                return;
            }
            
            logger.debug("Received {} from {}", message.getType(), player.getPlayerId());
            
            switch (message.getType()) {
                case PLAYER_ACTION:
                    handlePlayerAction(message);
                    break;
                    
                case PING:
                    handlePing();
                    break;
                    
                case PLAYER_DISCONNECT:
                    handleDisconnect();
                    break;
                    
                default:
                    logger.warn("Unhandled message type: {}", message.getType());
            }
            
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
        }
    }
    
    private void handlePlayerAction(Message message) {
        PlayerActionDTO action = gson.fromJson(
            gson.toJson(message.getPayload()), 
            PlayerActionDTO.class
        );
        server.handlePlayerAction(player.getPlayerId(), action);
    }
    
    private void handlePing() {
        Message pong = new Message(MessageType.PONG, "server", null);
        player.sendMessage(gson.toJson(pong));
    }
    
    private void handleDisconnect() {
        logger.info("Player {} disconnected", player.getPlayerId());
        running = false;
        server.handlePlayerDisconnect(player.getPlayerId());
    }
    
    public void stop() {
        running = false;
    }
}
