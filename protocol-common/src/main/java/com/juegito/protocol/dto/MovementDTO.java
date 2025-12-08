package com.juegito.protocol.dto;

import java.util.List;

/**
 * DTO para transferir información de movimiento.
 */
public class MovementDTO {
    private String playerId;
    private HexCoordinateDTO from;
    private HexCoordinateDTO to;
    private List<HexCoordinateDTO> path;
    private int cost;
    private String biomeEffect;
    
    public MovementDTO() {}
    
    public MovementDTO(String playerId, HexCoordinateDTO from, HexCoordinateDTO to,
                       List<HexCoordinateDTO> path, int cost, String biomeEffect) {
        this.playerId = playerId;
        this.from = from;
        this.to = to;
        this.path = path;
        this.cost = cost;
        this.biomeEffect = biomeEffect;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public HexCoordinateDTO getFrom() {
        return from;
    }
    
    public void setFrom(HexCoordinateDTO from) {
        this.from = from;
    }
    
    public HexCoordinateDTO getTo() {
        return to;
    }
    
    public void setTo(HexCoordinateDTO to) {
        this.to = to;
    }
    
    public List<HexCoordinateDTO> getPath() {
        return path;
    }
    
    public void setPath(List<HexCoordinateDTO> path) {
        this.path = path;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public String getBiomeEffect() {
        return biomeEffect;
    }
    
    public void setBiomeEffect(String biomeEffect) {
        this.biomeEffect = biomeEffect;
    }
}
