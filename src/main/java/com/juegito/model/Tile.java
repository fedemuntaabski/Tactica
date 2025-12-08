package com.juegito.model;

/**
 * Representa una casilla individual en el mapa hexagonal.
 */
public class Tile {
    private final HexCoordinate coordinate;
    private final BiomeType biome;
    private final TileType type;
    private String occupyingPlayerId; // null si no hay jugador
    
    public Tile(HexCoordinate coordinate, BiomeType biome, TileType type) {
        this.coordinate = coordinate;
        this.biome = biome;
        this.type = type;
        this.occupyingPlayerId = null;
    }
    
    public HexCoordinate getCoordinate() {
        return coordinate;
    }
    
    public BiomeType getBiome() {
        return biome;
    }
    
    public TileType getType() {
        return type;
    }
    
    public String getOccupyingPlayerId() {
        return occupyingPlayerId;
    }
    
    public void setOccupyingPlayerId(String playerId) {
        this.occupyingPlayerId = playerId;
    }
    
    public boolean isOccupied() {
        return occupyingPlayerId != null;
    }
    
    public boolean isBlocked() {
        return type == TileType.BLOCKED;
    }
    
    public boolean isAccessible() {
        return !isBlocked();
    }
    
    /**
     * Retorna el costo de movimiento para entrar a esta casilla según el bioma.
     */
    public int getMovementCost() {
        switch (biome) {
            case MOUNTAIN:
                return 2; // Terreno difícil
            case FOREST:
                return 1; // Terreno normal
            case PLAINS:
                return 1; // Terreno fácil
            default:
                return 1;
        }
    }
    
    /**
     * Retorna el modificador de defensa que proporciona esta casilla.
     */
    public int getDefenseBonus() {
        switch (biome) {
            case FOREST:
                return 1; // Bonus de defensa en bosque
            case MOUNTAIN:
                return 2; // Mayor bonus en montaña
            case PLAINS:
                return 0; // Sin bonus
            default:
                return 0;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Tile[%s, %s, %s, occupied=%s]", 
            coordinate, biome, type, isOccupied());
    }
}
