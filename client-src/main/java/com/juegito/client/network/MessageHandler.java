package com.juegito.client.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.juegito.client.protocol.Message;
import com.juegito.client.protocol.MessageType;
import com.juegito.client.protocol.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maneja la serialización y deserialización de mensajes.
 * Convierte entre JSON y objetos Java usando el protocolo definido.
 */
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    
    private final Gson gson;
    
    public MessageHandler() {
        this.gson = new Gson();
    }
    
    /**
     * Crea un mensaje para enviar al servidor.
     */
    public Message createMessage(MessageType type, String senderId, Object payload) {
        return new Message(type, senderId, payload);
    }
    
    /**
     * Deserializa un mensaje JSON recibido del servidor.
     */
    public Message parseMessage(String json) {
        try {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            
            Message message = new Message();
            message.setType(MessageType.valueOf(jsonObject.get("type").getAsString()));
            message.setTimestamp(jsonObject.get("timestamp").getAsLong());
            
            if (jsonObject.has("senderId") && !jsonObject.get("senderId").isJsonNull()) {
                message.setSenderId(jsonObject.get("senderId").getAsString());
            }
            
            if (jsonObject.has("payload") && !jsonObject.get("payload").isJsonNull()) {
                Object payload = parsePayload(message.getType(), jsonObject.get("payload"));
                message.setPayload(payload);
            }
            
            return message;
            
        } catch (Exception e) {
            logger.error("Error parsing message: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Parsea el payload según el tipo de mensaje.
     */
    private Object parsePayload(MessageType type, Object payloadJson) {
        String json = gson.toJson(payloadJson);
        
        switch (type) {
            case PLAYER_CONNECT:
                return gson.fromJson(json, PlayerConnectDTO.class);
                
            case LOBBY_STATE:
                return gson.fromJson(json, LobbyStateDTO.class);
                
            case GAME_STATE:
                return gson.fromJson(json, GameStateDTO.class);
                
            case PLAYER_ACTION:
            case ACTION_VALID:
            case ACTION_INVALID:
                return gson.fromJson(json, PlayerActionDTO.class);
                
            case TURN_START:
            case TURN_END:
            case ERROR:
                return gson.fromJson(json, JsonObject.class);
                
            default:
                return payloadJson;
        }
    }
    
    /**
     * Serializa un mensaje a JSON.
     */
    public String toJson(Message message) {
        return gson.toJson(message);
    }
}
