package com.juegito.protocol.dto.map;

import java.util.List;

/**
 * DTO para transferir el estado completo del mapa.
 */
public class MapStateDTO {
    private List<TileDTO> tiles;
    private List<ConnectionDTO> connections;
    private int currentTileId;
    private int startTileId;
    private int bossTileId;
    
    public MapStateDTO() {}
    
    public MapStateDTO(List<TileDTO> tiles, List<ConnectionDTO> connections, 
                       int currentTileId, int startTileId, int bossTileId) {
        this.tiles = tiles;
        this.connections = connections;
        this.currentTileId = currentTileId;
        this.startTileId = startTileId;
        this.bossTileId = bossTileId;
    }
    
    public List<TileDTO> getTiles() {
        return tiles;
    }
    
    public void setTiles(List<TileDTO> tiles) {
        this.tiles = tiles;
    }
    
    public List<ConnectionDTO> getConnections() {
        return connections;
    }
    
    public void setConnections(List<ConnectionDTO> connections) {
        this.connections = connections;
    }
    
    public int getCurrentTileId() {
        return currentTileId;
    }
    
    public void setCurrentTileId(int currentTileId) {
        this.currentTileId = currentTileId;
    }
    
    public int getStartTileId() {
        return startTileId;
    }
    
    public void setStartTileId(int startTileId) {
        this.startTileId = startTileId;
    }
    
    public int getBossTileId() {
        return bossTileId;
    }
    
    public void setBossTileId(int bossTileId) {
        this.bossTileId = bossTileId;
    }
    
    /**
     * DTO para conexiones entre tiles.
     */
    public static class ConnectionDTO {
        private int fromTileId;
        private int toTileId;
        
        public ConnectionDTO() {}
        
        public ConnectionDTO(int fromTileId, int toTileId) {
            this.fromTileId = fromTileId;
            this.toTileId = toTileId;
        }
        
        public int getFromTileId() {
            return fromTileId;
        }
        
        public void setFromTileId(int fromTileId) {
            this.fromTileId = fromTileId;
        }
        
        public int getToTileId() {
            return toTileId;
        }
        
        public void setToTileId(int toTileId) {
            this.toTileId = toTileId;
        }
    }
}
