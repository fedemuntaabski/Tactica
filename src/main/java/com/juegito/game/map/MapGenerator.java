package com.juegito.game.map;

import com.juegito.model.BiomeType;
import com.juegito.model.map.*;
import com.juegito.model.map.TileEvent.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generador semiprocedural de mapas.
 * Estructura fija con contenido aleatorio.
 */
public class MapGenerator {
    private static final int MIN_TILES = 15;
    private static final int MAX_TILES = 20;
    private static final int NUM_BIOMES = 3;
    
    private final Random random;
    
    public MapGenerator(long seed) {
        this.random = new Random(seed);
    }
    
    public MapGenerator() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Genera un mapa completo.
     */
    public GameMap generateMap() {
        int totalTiles = MIN_TILES + random.nextInt(MAX_TILES - MIN_TILES + 1);
        
        List<Tile> tiles = new ArrayList<>();
        List<GameMap.Connection> connections = new ArrayList<>();
        
        int tilesPerBiome = totalTiles / NUM_BIOMES;
        int tileId = 0;
        
        // Bioma 1: Praderas (tiles 0 - n/3)
        BiomeType biome1 = BiomeType.PLAINS;
        int biome1End = tilesPerBiome;
        
        // Bioma 2: Bosque Oscuro (tiles n/3 - 2n/3)
        BiomeType biome2 = BiomeType.FOREST;
        int biome2End = biome1End + tilesPerBiome;
        
        // Bioma 3: Montañas (tiles 2n/3 - n)
        BiomeType biome3 = BiomeType.MOUNTAIN;
        int biome3End = totalTiles - 1;
        
        // Generar tiles lineales con bifurcaciones ocasionales
        for (int i = 0; i < totalTiles; i++) {
            BiomeType currentBiome;
            if (i < biome1End) {
                currentBiome = biome1;
            } else if (i < biome2End) {
                currentBiome = biome2;
            } else {
                currentBiome = biome3;
            }
            
            TileType tileType = determineTileType(i, totalTiles);
            Tile tile = new Tile(i, i, 0, currentBiome, tileType);
            
            // Asignar eventos según el tipo
            if (tileType == TileType.NORMAL || tileType == TileType.FORK) {
                tile.setEvent(generateRandomEvent(currentBiome));
            } else if (tileType == TileType.MINI_BOSS) {
                tile.setEvent(createMiniBossEvent());
            } else if (tileType == TileType.BOSS) {
                tile.setEvent(createBossEvent());
            } else if (tileType == TileType.SHOP) {
                tile.setEvent(createShopEvent());
            } else if (tileType == TileType.REST) {
                tile.setEvent(createRestEvent());
            } else if (tileType == TileType.TREASURE) {
                tile.setEvent(createTreasureEvent());
            }
            
            tiles.add(tile);
            
            // Conectar con el tile anterior (camino lineal)
            if (i > 0) {
                connections.add(new GameMap.Connection(i - 1, i));
            }
        }
        
        // Agregar algunas bifurcaciones (rutas alternativas)
        addBranches(tiles, connections, totalTiles);
        
        int startTileId = 0;
        int bossTileId = totalTiles - 1;
        
        // Revelar tile inicial
        tiles.get(startTileId).setRevealed(true);
        tiles.get(startTileId).setVisited(true);
        
        return new GameMap(tiles, connections, startTileId, bossTileId);
    }
    
    private TileType determineTileType(int index, int total) {
        if (index == 0) {
            return TileType.START;
        }
        if (index == total - 1) {
            return TileType.BOSS;
        }
        
        // Mini-boss alrededor del 70% del camino
        if (index == (int)(total * 0.7)) {
            return TileType.MINI_BOSS;
        }
        
        // Descanso cada ~5 tiles
        if (index % 5 == 0 && index > 0) {
            return TileType.REST;
        }
        
        // Tienda ocasional
        if (index % 7 == 0 && index > 2) {
            return TileType.SHOP;
        }
        
        // Tesoro ocasional
        if (random.nextDouble() < 0.15) {
            return TileType.TREASURE;
        }
        
        // Bifurcación ocasional
        if (random.nextDouble() < 0.2 && index > 2 && index < total - 3) {
            return TileType.FORK;
        }
        
        return TileType.NORMAL;
    }
    
    private TileEvent generateRandomEvent(BiomeType biome) {
        double roll = random.nextDouble();
        
        if (roll < 0.40) {
            return createCombatEvent(biome);
        } else if (roll < 0.55) {
            return createTrapEvent();
        } else if (roll < 0.70) {
            return createLootEvent();
        } else if (roll < 0.85) {
            return createNarrativeEvent(biome);
        } else {
            return createAltarEvent();
        }
    }
    
    private TileEvent createCombatEvent(BiomeType biome) {
        String description = "Un grupo de enemigos bloquea el camino.";
        TileEvent event = new TileEvent(EventType.COMBAT, "combat_" + random.nextInt(1000), description);
        return event;
    }
    
    private TileEvent createTrapEvent() {
        String[] trapDescriptions = {
            "Una trampa de púas se activa.",
            "El suelo se hunde revelando un pozo.",
            "Dardos venenosos salen de las paredes."
        };
        String desc = trapDescriptions[random.nextInt(trapDescriptions.length)];
        return new TileEvent(EventType.TRAP, "trap_" + random.nextInt(1000), desc);
    }
    
    private TileEvent createLootEvent() {
        return new TileEvent(EventType.LOOT, "loot_" + random.nextInt(1000), 
            "Encuentras un cofre abandonado.");
    }
    
    private TileEvent createNarrativeEvent(BiomeType biome) {
        String[] narratives = {
            "Un viajero misterioso ofrece intercambiar objetos.",
            "Encuentras ruinas antiguas con inscripciones.",
            "Un camino alternativo se revela en el bosque."
        };
        String desc = narratives[random.nextInt(narratives.length)];
        return new TileEvent(EventType.NARRATIVE, "narrative_" + random.nextInt(1000), desc);
    }
    
    private TileEvent createAltarEvent() {
        return new TileEvent(EventType.ALTAR, "altar_" + random.nextInt(1000),
            "Un altar antiguo emana poder mágico.");
    }
    
    private TileEvent createShopEvent() {
        return new TileEvent(EventType.SHOP, "shop", "Una tienda ambulante ofrece sus mercancías.");
    }
    
    private TileEvent createRestEvent() {
        return new TileEvent(EventType.CAMP, "camp", "Un campamento seguro para descansar.");
    }
    
    private TileEvent createTreasureEvent() {
        return new TileEvent(EventType.LOOT, "treasure", "Un cofre del tesoro bien escondido.");
    }
    
    private TileEvent createMiniBossEvent() {
        return new TileEvent(EventType.COMBAT, "miniboss", 
            "Un enemigo poderoso te desafía.");
    }
    
    private TileEvent createBossEvent() {
        return new TileEvent(EventType.COMBAT, "boss", 
            "El jefe final aguarda en su guarida.");
    }
    
    private void addBranches(List<Tile> tiles, List<GameMap.Connection> connections, int total) {
        // Agregar 1-2 rutas alternativas cortas
        int branches = 1 + random.nextInt(2);
        
        for (int b = 0; b < branches; b++) {
            // Punto de bifurcación (no demasiado cerca del inicio o final)
            int forkPoint = 3 + random.nextInt(total - 8);
            // Punto de reunión (2-4 tiles adelante)
            int mergePoint = forkPoint + 2 + random.nextInt(3);
            
            if (mergePoint >= total) continue;
            
            // Crear tile alternativo
            Tile altTile = new Tile(
                tiles.size(), 
                forkPoint, 
                1, // y=1 indica camino alternativo
                tiles.get(forkPoint).getBiome(),
                TileType.NORMAL
            );
            altTile.setEvent(generateRandomEvent(altTile.getBiome()));
            tiles.add(altTile);
            
            // Conectar: fork -> alt -> merge
            connections.add(new GameMap.Connection(forkPoint, altTile.getId()));
            connections.add(new GameMap.Connection(altTile.getId(), mergePoint));
        }
    }
}
