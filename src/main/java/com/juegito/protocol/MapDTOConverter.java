package com.juegito.protocol;

import com.juegito.model.*;
import com.juegito.protocol.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte entre objetos del modelo y DTOs para transferencia.
 * Centraliza la lógica de conversión para mantener DRY.
 */
public class MapDTOConverter {
    
    /**
     * Convierte HexCoordinate a DTO.
     */
    public static HexCoordinateDTO toDTO(HexCoordinate coord) {
        if (coord == null) return null;
        return new HexCoordinateDTO(coord.getQ(), coord.getR(), coord.getS());
    }
    
    /**
     * Convierte HexCoordinateDTO a modelo.
     */
    public static HexCoordinate fromDTO(HexCoordinateDTO dto) {
        if (dto == null) return null;
        return new HexCoordinate(dto.getQ(), dto.getR());
    }
    
    /**
     * Convierte Tile a DTO.
     */
    public static TileDTO toDTO(Tile tile) {
        if (tile == null) return null;
        
        return new TileDTO(
            toDTO(tile.getCoordinate()),
            tile.getBiome().name(),
            tile.getType().name(),
            tile.getOccupyingPlayerId(),
            tile.getMovementCost(),
            tile.getDefenseBonus()
        );
    }
    
    private static List<HexCoordinateDTO> convertCoordinateList(List<HexCoordinate> coords) {
        return coords.stream()
            .map(MapDTOConverter::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Convierte GameMap a DTO.
     */
    public static GameMapDTO toDTO(GameMap map) {
        if (map == null) return null;
        
        List<TileDTO> tileDTOs = map.getAllTiles().stream()
            .map(MapDTOConverter::toDTO)
            .collect(Collectors.toList());
        
        Map<String, HexCoordinateDTO> playerPositionDTOs = new HashMap<>();
        map.getPlayerPositions().forEach((playerId, coord) -> 
            playerPositionDTOs.put(playerId, toDTO(coord)));
        
        List<HexCoordinateDTO> spawnDTOs = convertCoordinateList(map.getSpawnPoints());
        List<HexCoordinateDTO> resourceDTOs = convertCoordinateList(map.getResourceNodes());
        List<HexCoordinateDTO> strategicDTOs = convertCoordinateList(map.getStrategicNodes());
        
        return new GameMapDTO(
            map.getRadius(),
            tileDTOs,
            playerPositionDTOs,
            spawnDTOs,
            resourceDTOs,
            strategicDTOs
        );
    }
    
    /**
     * Convierte MovementResult a DTO.
     */
    public static MovementDTO toDTO(String playerId, 
                                    com.juegito.game.MovementExecutor.MovementResult result) {
        if (result == null || !result.isSuccess()) return null;
        
        List<HexCoordinateDTO> pathDTOs = result.getPath() != null ?
            result.getPath().stream()
                .map(MapDTOConverter::toDTO)
                .collect(Collectors.toList()) : new ArrayList<>();
        
        return new MovementDTO(
            playerId,
            toDTO(result.getFrom()),
            toDTO(result.getTo()),
            pathDTOs,
            result.getCost(),
            result.getBiomeEffect()
        );
    }
    
    /**
     * Convierte lista de coordenadas a DTOs.
     */
    public static List<HexCoordinateDTO> toDTOList(List<HexCoordinate> coordinates) {
        if (coordinates == null) return new ArrayList<>();
        return convertCoordinateList(coordinates);
    }
}
