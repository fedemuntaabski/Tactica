package com.juegito.model.map;

import com.juegito.model.BiomeType;

/**
 * Representa una casilla individual en el mapa hexagonal.
 */
public class Tile {
    private final int id;
    private final int x;
    private final int y;
    private final BiomeType biome;
    private final TileType type;
    private TileEvent event;
    private boolean visited;
    private boolean revealed;
    
    public Tile(int id, int x, int y, BiomeType biome, TileType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.biome = biome;
        this.type = type;
        this.visited = false;
        this.revealed = false;
    }
    
    public int getId() {
        return id;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public BiomeType getBiome() {
        return biome;
    }
    
    public TileType getType() {
        return type;
    }
    
    public TileEvent getEvent() {
        return event;
    }
    
    public void setEvent(TileEvent event) {
        this.event = event;
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
    
    @Override
    public String toString() {
        return String.format("Tile[id=%d, pos=(%d,%d), biome=%s, type=%s]", 
            id, x, y, biome, type);
    }
}
