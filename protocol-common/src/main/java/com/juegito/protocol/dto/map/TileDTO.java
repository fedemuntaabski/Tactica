package com.juegito.protocol.dto.map;

/**
 * DTO para transferir información de una casilla.
 */
public class TileDTO {
    private int id;
    private int x;
    private int y;
    private String biome;
    private String type;
    private boolean visited;
    private boolean revealed;
    private TileEventDTO event;
    
    public TileDTO() {}
    
    public TileDTO(int id, int x, int y, String biome, String type, 
                   boolean visited, boolean revealed, TileEventDTO event) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.biome = biome;
        this.type = type;
        this.visited = visited;
        this.revealed = revealed;
        this.event = event;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public String getBiome() {
        return biome;
    }
    
    public void setBiome(String biome) {
        this.biome = biome;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isVisited() {
        return visited;
    }
    
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public boolean isRevealed() {
        return revealed;
    }
    
    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
    
    public TileEventDTO getEvent() {
        return event;
    }
    
    public void setEvent(TileEventDTO event) {
        this.event = event;
    }
}
