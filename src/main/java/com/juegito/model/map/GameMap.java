package com.juegito.model.map;

import com.juegito.model.map.TileEvent.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el mapa completo del juego.
 */
public class GameMap {
    private final List<Tile> tiles;
    private final List<Connection> connections;
    private int currentTileId;
    private final int startTileId;
    private final int bossTileId;
    
    public GameMap() {
        this.tiles = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.startTileId = 0;
        this.currentTileId = 0;
        this.bossTileId = -1;
    }
    
    public GameMap(List<Tile> tiles, List<Connection> connections, int startTileId, int bossTileId) {
        this.tiles = tiles;
        this.connections = connections;
        this.startTileId = startTileId;
        this.currentTileId = startTileId;
        this.bossTileId = bossTileId;
    }
    
    public void addTile(Tile tile) {
        tiles.add(tile);
    }
    
    public void addConnection(int fromTileId, int toTileId) {
        connections.add(new Connection(fromTileId, toTileId));
    }
    
    public Tile getTile(int tileId) {
        return tiles.stream()
            .filter(t -> t.getId() == tileId)
            .findFirst()
            .orElse(null);
    }
    
    public Tile getCurrentTile() {
        return getTile(currentTileId);
    }
    
    public void setCurrentTileId(int tileId) {
        this.currentTileId = tileId;
        Tile tile = getTile(tileId);
        if (tile != null) {
            tile.setVisited(true);
            tile.setRevealed(true);
        }
    }
    
    public List<Tile> getAdjacentTiles(int tileId) {
        List<Tile> adjacent = new ArrayList<>();
        for (Connection conn : connections) {
            if (conn.fromTileId == tileId) {
                Tile tile = getTile(conn.toTileId);
                if (tile != null) {
                    adjacent.add(tile);
                }
            }
        }
        return adjacent;
    }
    
    public List<Tile> getAvailableMoves() {
        return getAdjacentTiles(currentTileId);
    }
    
    public boolean canMoveTo(int targetTileId) {
        return getAvailableMoves().stream()
            .anyMatch(t -> t.getId() == targetTileId);
    }
    
    public List<Tile> getTiles() {
        return new ArrayList<>(tiles);
    }
    
    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }
    
    public int getCurrentTileId() {
        return currentTileId;
    }
    
    public int getStartTileId() {
        return startTileId;
    }
    
    public int getBossTileId() {
        return bossTileId;
    }
    
    /**
     * Representa una conexión entre dos casillas.
     */
    public static class Connection {
        private final int fromTileId;
        private final int toTileId;
        
        public Connection(int fromTileId, int toTileId) {
            this.fromTileId = fromTileId;
            this.toTileId = toTileId;
        }
        
        public int getFromTileId() {
            return fromTileId;
        }
        
        public int getToTileId() {
            return toTileId;
        }
    }
}
