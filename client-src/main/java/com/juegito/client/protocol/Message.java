package com.juegito.client.protocol;

/**
 * Clase base para todos los mensajes del protocolo.
 * Compatible con el protocolo del servidor.
 */
public class Message {
    private MessageType type;
    private long timestamp;
    private String senderId;
    private Object payload;
    
    public Message() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(MessageType type, String senderId, Object payload) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.senderId = senderId;
        this.payload = payload;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public Object getPayload() {
        return payload;
    }
    
    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
