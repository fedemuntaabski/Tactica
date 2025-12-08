package com.juegito.game;

import com.juegito.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Genera mapas hexagonales procedurales con biomas y nodos especiales.
 * Responsable de crear la distribución inicial del terreno.
 */
public class MapGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MapGenerator.class);
    private final Random random;
    
    // Configuración por defecto
    private static final int DEFAULT_RADIUS = 5;
    private static final double FOREST_PROBABILITY = 0.35;
    private static final double MOUNTAIN_PROBABILITY = 0.25;
    private static final int RESOURCE_NODES_COUNT = 3;
    private static final int STRATEGIC_NODES_COUNT = 2;
    
    public MapGenerator() {
        this.random = new Random();
    }
    
    public MapGenerator(long seed) {
        this.random = new Random(seed);
    }
    
    /**
     * Genera un mapa hexagonal completo con la configuración por defecto.
     */
    public GameMap generateMap(int playerCount) {
        return generateMap(DEFAULT_RADIUS, playerCount);
    }
    
    /**
     * Genera un mapa hexagonal con radio especificado.
     */
    public GameMap generateMap(int radius, int playerCount) {
        logger.info("Generating map with radius {} for {} players", radius, playerCount);
        
        GameMap map = new GameMap(radius);
        List<HexCoordinate> allCoordinates = generateHexagonalCoordinates(radius);
        
        // Seleccionar puntos de spawn equidistantes
        List<HexCoordinate> spawnPoints = selectSpawnPoints(allCoordinates, playerCount);
        
        // Seleccionar nodos especiales
        List<HexCoordinate> resourceNodes = selectSpecialNodes(
            allCoordinates, spawnPoints, RESOURCE_NODES_COUNT);
        List<HexCoordinate> strategicNodes = selectSpecialNodes(
            allCoordinates, spawnPoints, STRATEGIC_NODES_COUNT);
        
        // Generar casillas con biomas
        for (HexCoordinate coord : allCoordinates) {
            TileType type = determineTileType(coord, spawnPoints, resourceNodes, strategicNodes);
            BiomeType biome = generateBiome(coord, spawnPoints);
            
            Tile tile = new Tile(coord, biome, type);
            map.addTile(tile);
        }
        
        logger.info("Map generated: {} tiles, {} spawns, {} resources, {} strategic",
            allCoordinates.size(), spawnPoints.size(), 
            resourceNodes.size(), strategicNodes.size());
        
        return map;
    }
    
    /**
     * Genera todas las coordenadas hexagonales dentro del radio especificado.
     */
    private List<HexCoordinate> generateHexagonalCoordinates(int radius) {
        List<HexCoordinate> coordinates = new ArrayList<>();
        
        for (int q = -radius; q <= radius; q++) {
            int r1 = Math.max(-radius, -q - radius);
            int r2 = Math.min(radius, -q + radius);
            
            for (int r = r1; r <= r2; r++) {
                coordinates.add(new HexCoordinate(q, r));
            }
        }
        
        return coordinates;
    }
    
    /**
     * Selecciona puntos de spawn distribuidos equitativamente en el mapa.
     */
    private List<HexCoordinate> selectSpawnPoints(
            List<HexCoordinate> allCoordinates, int count) {
        
        List<HexCoordinate> spawnPoints = new ArrayList<>();
        
        if (count == 2) {
            // Para 2 jugadores: opuestos
            spawnPoints.add(new HexCoordinate(-3, 0));
            spawnPoints.add(new HexCoordinate(3, 0));
        } else if (count == 3) {
            // Para 3 jugadores: triángulo equilátero
            spawnPoints.add(new HexCoordinate(0, -3));
            spawnPoints.add(new HexCoordinate(3, 0));
            spawnPoints.add(new HexCoordinate(-3, 3));
        } else if (count == 4) {
            // Para 4 jugadores: cuadrado
            spawnPoints.add(new HexCoordinate(-3, 0));
            spawnPoints.add(new HexCoordinate(0, -3));
            spawnPoints.add(new HexCoordinate(3, 0));
            spawnPoints.add(new HexCoordinate(0, 3));
        }
        
        // Filtrar solo coordenadas válidas
        spawnPoints.removeIf(coord -> !allCoordinates.contains(coord));
        
        return spawnPoints;
    }
    
    /**
     * Selecciona nodos especiales evitando spawns y otros nodos.
     */
    private List<HexCoordinate> selectSpecialNodes(
            List<HexCoordinate> allCoordinates,
            List<HexCoordinate> spawnPoints,
            int count) {
        
        List<HexCoordinate> candidates = new ArrayList<>(allCoordinates);
        candidates.removeAll(spawnPoints);
        
        List<HexCoordinate> selected = new ArrayList<>();
        
        for (int i = 0; i < count && !candidates.isEmpty(); i++) {
            int index = random.nextInt(candidates.size());
            HexCoordinate node = candidates.remove(index);
            selected.add(node);
            
            // Remover vecinos inmediatos para distribuir mejor
            candidates.removeIf(coord -> coord.distanceTo(node) <= 1);
        }
        
        return selected;
    }
    
    /**
     * Determina el tipo de casilla según su posición y nodos especiales.
     */
    private TileType determineTileType(
            HexCoordinate coord,
            List<HexCoordinate> spawnPoints,
            List<HexCoordinate> resourceNodes,
            List<HexCoordinate> strategicNodes) {
        
        if (spawnPoints.contains(coord)) {
            return TileType.SPAWN;
        }
        if (resourceNodes.contains(coord)) {
            return TileType.RESOURCE;
        }
        if (strategicNodes.contains(coord)) {
            return TileType.STRATEGIC;
        }
        
        return TileType.NORMAL;
    }
    
    /**
     * Genera el bioma para una coordenada usando ruido procedural simple.
     */
    private BiomeType generateBiome(HexCoordinate coord, List<HexCoordinate> spawnPoints) {
        // Asegurar que spawns estén en llanuras
        if (spawnPoints.contains(coord)) {
            return BiomeType.PLAINS;
        }
        
        // Generar bioma basado en probabilidades
        double value = random.nextDouble();
        
        if (value < MOUNTAIN_PROBABILITY) {
            return BiomeType.MOUNTAIN;
        } else if (value < MOUNTAIN_PROBABILITY + FOREST_PROBABILITY) {
            return BiomeType.FOREST;
        } else {
            return BiomeType.PLAINS;
        }
    }
    
    /**
     * Genera un mapa de prueba para testing.
     */
    public GameMap generateTestMap() {
        logger.info("Generating test map");
        
        GameMap map = new GameMap(3);
        
        // Mapa pequeño 3x3 para pruebas
        for (int q = -2; q <= 2; q++) {
            for (int r = -2; r <= 2; r++) {
                if (Math.abs(q + r) <= 2) {
                    HexCoordinate coord = new HexCoordinate(q, r);
                    BiomeType biome = BiomeType.PLAINS;
                    TileType type = TileType.NORMAL;
                    
                    // Puntos de spawn en extremos
                    if ((q == -2 && r == 0) || (q == 2 && r == 0)) {
                        type = TileType.SPAWN;
                    }
                    
                    // Un nodo de recurso en el centro
                    if (q == 0 && r == 0) {
                        type = TileType.RESOURCE;
                    }
                    
                    Tile tile = new Tile(coord, biome, type);
                    map.addTile(tile);
                }
            }
        }
        
        return map;
    }
}
