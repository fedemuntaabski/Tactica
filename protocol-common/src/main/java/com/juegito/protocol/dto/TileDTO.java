package com.juegito.protocol.dto;

/**
 * DTO para transferir información de casillas del mapa.
 */
public class TileDTO {
    private HexCoordinateDTO coordinate;
    private String biome;  // FOREST, MOUNTAIN, PLAINS
    private String type;   // NORMAL, SPAWN, RESOURCE, STRATEGIC, BLOCKED
    private String occupyingPlayerId;
    private int movementCost;
    private int defenseBonus;
    
    public TileDTO() {}
    
    public TileDTO(HexCoordinateDTO coordinate, String biome, String type,
                   String occupyingPlayerId, int movementCost, int defenseBonus) {
        this.coordinate = coordinate;
        this.biome = biome;
        this.type = type;
        this.occupyingPlayerId = occupyingPlayerId;
        this.movementCost = movementCost;
        this.defenseBonus = defenseBonus;
    }
    
    public HexCoordinateDTO getCoordinate() {
        return coordinate;
    }
    
    public void setCoordinate(HexCoordinateDTO coordinate) {
        this.coordinate = coordinate;
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
    
    public String getOccupyingPlayerId() {
        return occupyingPlayerId;
    }
    
    public void setOccupyingPlayerId(String occupyingPlayerId) {
        this.occupyingPlayerId = occupyingPlayerId;
    }
    
    public int getMovementCost() {
        return movementCost;
    }
    
    public void setMovementCost(int movementCost) {
        this.movementCost = movementCost;
    }
    
    public int getDefenseBonus() {
        return defenseBonus;
    }
    
    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }
}
