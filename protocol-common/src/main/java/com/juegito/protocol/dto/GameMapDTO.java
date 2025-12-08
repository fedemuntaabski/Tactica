package com.juegito.protocol.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO para transferir el mapa completo del juego.
 */
public class GameMapDTO {
    private int radius;
    private List<TileDTO> tiles;
    private Map<String, HexCoordinateDTO> playerPositions;
    private List<HexCoordinateDTO> spawnPoints;
    private List<HexCoordinateDTO> resourceNodes;
    private List<HexCoordinateDTO> strategicNodes;
    
    public GameMapDTO() {}
    
    public GameMapDTO(int radius, List<TileDTO> tiles, 
                      Map<String, HexCoordinateDTO> playerPositions,
                      List<HexCoordinateDTO> spawnPoints,
                      List<HexCoordinateDTO> resourceNodes,
                      List<HexCoordinateDTO> strategicNodes) {
        this.radius = radius;
        this.tiles = tiles;
        this.playerPositions = playerPositions;
        this.spawnPoints = spawnPoints;
        this.resourceNodes = resourceNodes;
        this.strategicNodes = strategicNodes;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
    }
    
    public List<TileDTO> getTiles() {
        return tiles;
    }
    
    public void setTiles(List<TileDTO> tiles) {
        this.tiles = tiles;
    }
    
    public Map<String, HexCoordinateDTO> getPlayerPositions() {
        return playerPositions;
    }
    
    public void setPlayerPositions(Map<String, HexCoordinateDTO> playerPositions) {
        this.playerPositions = playerPositions;
    }
    
    public List<HexCoordinateDTO> getSpawnPoints() {
        return spawnPoints;
    }
    
    public void setSpawnPoints(List<HexCoordinateDTO> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
    
    public List<HexCoordinateDTO> getResourceNodes() {
        return resourceNodes;
    }
    
    public void setResourceNodes(List<HexCoordinateDTO> resourceNodes) {
        this.resourceNodes = resourceNodes;
    }
    
    public List<HexCoordinateDTO> getStrategicNodes() {
        return strategicNodes;
    }
    
    public void setStrategicNodes(List<HexCoordinateDTO> strategicNodes) {
        this.strategicNodes = strategicNodes;
    }
}
