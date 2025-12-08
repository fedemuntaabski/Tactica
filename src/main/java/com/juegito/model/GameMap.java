package com.juegito.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Representa el mapa completo del juego con todas sus casillas.
 * Gestiona el estado del terreno y las posiciones de los jugadores.
 */
public class GameMap {
    private final int radius; // Radio del mapa hexagonal
    private final Map<HexCoordinate, Tile> tiles;
    private final Map<String, HexCoordinate> playerPositions;
    private final List<HexCoordinate> spawnPoints;
    private final List<HexCoordinate> resourceNodes;
    private final List<HexCoordinate> strategicNodes;
    
    public GameMap(int radius) {
        this.radius = radius;
        this.tiles = new ConcurrentHashMap<>();
        this.playerPositions = new ConcurrentHashMap<>();
        this.spawnPoints = new ArrayList<>();
        this.resourceNodes = new ArrayList<>();
        this.strategicNodes = new ArrayList<>();
    }
    
    /**
     * Añade una casilla al mapa.
     */
    public void addTile(Tile tile) {
        tiles.put(tile.getCoordinate(), tile);
        
        // Registrar nodos especiales
        if (tile.getType() == TileType.SPAWN) {
            spawnPoints.add(tile.getCoordinate());
        } else if (tile.getType() == TileType.RESOURCE) {
            resourceNodes.add(tile.getCoordinate());
        } else if (tile.getType() == TileType.STRATEGIC) {
            strategicNodes.add(tile.getCoordinate());
        }
    }
    
    /**
     * Obtiene una casilla por sus coordenadas.
     */
    public Tile getTile(HexCoordinate coordinate) {
        return tiles.get(coordinate);
    }
    
    /**
     * Verifica si una coordenada existe en el mapa.
     */
    public boolean containsTile(HexCoordinate coordinate) {
        return tiles.containsKey(coordinate);
    }
    
    /**
     * Obtiene todas las casillas del mapa.
     */
    public Collection<Tile> getAllTiles() {
        return tiles.values();
    }
    
    /**
     * Obtiene los vecinos accesibles de una coordenada.
     */
    public List<Tile> getAccessibleNeighbors(HexCoordinate coordinate) {
        List<Tile> neighbors = new ArrayList<>();
        
        for (HexCoordinate neighbor : coordinate.getNeighbors()) {
            Tile tile = getTile(neighbor);
            if (tile != null && tile.isAccessible()) {
                neighbors.add(tile);
            }
        }
        
        return neighbors;
    }
    
    /**
     * Coloca un jugador en una posición del mapa.
     */
    public boolean placePlayer(String playerId, HexCoordinate coordinate) {
        Tile tile = getTile(coordinate);
        
        if (tile == null || !tile.isAccessible()) {
            return false;
        }
        
        // Remover jugador de posición anterior si existe
        HexCoordinate oldPosition = playerPositions.get(playerId);
        if (oldPosition != null) {
            Tile oldTile = getTile(oldPosition);
            if (oldTile != null) {
                oldTile.setOccupyingPlayerId(null);
            }
        }
        
        // Colocar en nueva posición
        tile.setOccupyingPlayerId(playerId);
        playerPositions.put(playerId, coordinate);
        
        return true;
    }
    
    /**
     * Obtiene la posición actual de un jugador.
     */
    public HexCoordinate getPlayerPosition(String playerId) {
        return playerPositions.get(playerId);
    }
    
    /**
     * Remueve un jugador del mapa.
     */
    public void removePlayer(String playerId) {
        HexCoordinate position = playerPositions.remove(playerId);
        if (position != null) {
            Tile tile = getTile(position);
            if (tile != null) {
                tile.setOccupyingPlayerId(null);
            }
        }
    }
    
    public int getRadius() {
        return radius;
    }
    
    public List<HexCoordinate> getSpawnPoints() {
        return new ArrayList<>(spawnPoints);
    }
    
    public List<HexCoordinate> getResourceNodes() {
        return new ArrayList<>(resourceNodes);
    }
    
    public List<HexCoordinate> getStrategicNodes() {
        return new ArrayList<>(strategicNodes);
    }
    
    public Map<String, HexCoordinate> getPlayerPositions() {
        return new HashMap<>(playerPositions);
    }
    
    /**
     * Calcula la distancia entre dos coordenadas.
     */
    public int getDistance(HexCoordinate from, HexCoordinate to) {
        return from.distanceTo(to);
    }
}
