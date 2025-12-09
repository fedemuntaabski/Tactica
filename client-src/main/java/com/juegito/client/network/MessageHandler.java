package com.juegito.client.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.*;
import com.juegito.protocol.dto.lobby.*;
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
            // Mensajes de conexión
            case PLAYER_CONNECT:
                return gson.fromJson(json, PlayerConnectDTO.class);
            
            // Mensajes de lobby (servidor → cliente)
            case JOIN_RESPONSE:
                return gson.fromJson(json, JoinResponseDTO.class);
            
            case LOBBY_SNAPSHOT:
                return gson.fromJson(json, LobbySnapshotDTO.class);
            
            case PLAYER_JOINED:
                return gson.fromJson(json, PlayerJoinedDTO.class);
            
            case PLAYER_LEFT:
                return gson.fromJson(json, PlayerLeftDTO.class);
            
            case PLAYER_UPDATED:
                return gson.fromJson(json, PlayerUpdatedDTO.class);
            
            case INVALID_ACTION:
                return gson.fromJson(json, InvalidActionDTO.class);
            
            case START_MATCH:
                return gson.fromJson(json, StartMatchDTO.class);
            
            case KICKED_FROM_LOBBY:
                return gson.fromJson(json, KickedFromLobbyDTO.class);
            
            case CHAT_MESSAGE:
                return gson.fromJson(json, ChatMessageDTO.class);
                
            case CHANGE_PLAYER_NAME:
                return gson.fromJson(json, ChangePlayerNameDTO.class);
            
            // Mensajes de lobby legacy (compatibilidad)    
            case LOBBY_STATE:
                return gson.fromJson(json, LobbyStateDTO.class);
                
            case GAME_STATE:
                return gson.fromJson(json, GameStateDTO.class);
                
            case PLAYER_ACTION:
            case ACTION_VALID:
            case ACTION_INVALID:
                return gson.fromJson(json, PlayerActionDTO.class);
            
            // Heartbeat y reconexión
            case GAME_HEARTBEAT:
                return gson.fromJson(json, GameHeartbeatDTO.class);
            
            case FULL_RESYNC:
                return gson.fromJson(json, GameStateDTO.class);
            
            case RECONNECT_ACCEPTED:
            case RECONNECT_REJECTED:
                return gson.fromJson(json, ReconnectResponseDTO.class);
                
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
