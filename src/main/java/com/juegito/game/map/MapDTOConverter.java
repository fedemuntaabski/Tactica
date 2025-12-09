package com.juegito.game.map;

import com.juegito.model.map.*;
import com.juegito.protocol.dto.map.*;
import com.juegito.protocol.dto.map.MapStateDTO.ConnectionDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Conversor entre modelos de mapa y DTOs.
 */
public class MapDTOConverter {
    
    /**
     * Convierte un GameMap completo a DTO.
     */
    public static MapStateDTO toMapStateDTO(GameMap map) {
        List<TileDTO> tileDTOs = map.getTiles().stream()
            .map(MapDTOConverter::toTileDTO)
            .collect(Collectors.toList());
        
        List<ConnectionDTO> connectionDTOs = map.getConnections().stream()
            .map(c -> new ConnectionDTO(c.getFromTileId(), c.getToTileId()))
            .collect(Collectors.toList());
        
        return new MapStateDTO(
            tileDTOs,
            connectionDTOs,
            map.getCurrentTileId(),
            map.getStartTileId(),
            map.getBossTileId()
        );
    }
    
    /**
     * Convierte un Tile a TileDTO.
     */
    public static TileDTO toTileDTO(Tile tile) {
        TileEventDTO eventDTO = null;
        if (tile.getEvent() != null) {
            eventDTO = toTileEventDTO(tile.getEvent());
        }
        
        return new TileDTO(
            tile.getId(),
            tile.getX(),
            tile.getY(),
            tile.getBiome().name(),
            tile.getType().name(),
            tile.isVisited(),
            tile.isRevealed(),
            eventDTO
        );
    }
    
    /**
     * Convierte un TileEvent a TileEventDTO.
     */
    public static TileEventDTO toTileEventDTO(TileEvent event) {
        return new TileEventDTO(
            event.getEventType().name(),
            event.getEventId(),
            event.getDescription(),
            event.isResolved()
        );
    }
    
    /**
     * Convierte un MovementVoteManager.VoteResult a VoteResultDTO.
     */
    public static VoteResultDTO toVoteResultDTO(MovementVoteManager.VoteResult result, 
                                                MovementVoteManager voteManager) {
        java.util.Map<String, Boolean> votes = voteManager.getVotes().entrySet().stream()
            .collect(Collectors.toMap(
                java.util.Map.Entry::getKey,
                e -> e.getValue().isApprove()
            ));
        
        return new VoteResultDTO(
            result.getTargetTileId(),
            result.isApproved(),
            result.getYesVotes(),
            result.getNoVotes(),
            votes
        );
    }
}
