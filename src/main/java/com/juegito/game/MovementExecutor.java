package com.juegito.game;

import com.juegito.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Ejecuta movimientos validados de jugadores en el mapa.
 * Actualiza el estado del mapa tras movimientos exitosos.
 */
public class MovementExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MovementExecutor.class);
    
    private final GameMap map;
    private final MovementValidator validator;
    
    public MovementExecutor(GameMap map) {
        this.map = map;
        this.validator = new MovementValidator(map);
    }
    
    /**
     * Ejecuta un movimiento de jugador si es válido.
     */
    public MovementResult executeMovement(String playerId, HexCoordinate destination) {
        logger.debug("Attempting to move player {} to {}", playerId, destination);
        
        // Validar el movimiento
        MovementValidator.MovementValidation validation = 
            validator.validateMovement(playerId, destination);
        
        if (!validation.isValid()) {
            logger.warn("Invalid movement for player {}: {}", 
                playerId, validation.getErrorMessage());
            return MovementResult.failure(validation.getErrorMessage());
        }
        
        // Obtener posición anterior
        HexCoordinate oldPosition = map.getPlayerPosition(playerId);
        
        // Ejecutar el movimiento
        boolean success = map.placePlayer(playerId, destination);
        
        if (!success) {
            logger.error("Failed to place player {} at {}", playerId, destination);
            return MovementResult.failure("Error al ejecutar el movimiento");
        }
        
        logger.info("Player {} moved from {} to {} (cost: {})", 
            playerId, oldPosition, destination, validation.getCost());
        
        // Verificar efectos del bioma en destino
        Tile destinationTile = map.getTile(destination);
        String biomeEffect = getBiomeEffect(destinationTile);
        
        return MovementResult.success(
            oldPosition, 
            destination, 
            validation.getPath(),
            validation.getCost(),
            biomeEffect
        );
    }
    
    /**
     * Obtiene el efecto descriptivo del bioma de una casilla.
     */
    private String getBiomeEffect(Tile tile) {
        switch (tile.getBiome()) {
            case FOREST:
                return "Entraste a un bosque (+1 defensa)";
            case MOUNTAIN:
                return "Escalaste una montaña (+2 defensa, movimiento costoso)";
            case PLAINS:
                return "Estás en llanuras (terreno neutral)";
            default:
                return "";
        }
    }
    
    /**
     * Obtiene las casillas alcanzables desde la posición actual de un jugador.
     */
    public List<HexCoordinate> getReachablePositions(String playerId) {
        HexCoordinate currentPosition = map.getPlayerPosition(playerId);
        
        if (currentPosition == null) {
            logger.warn("Cannot get reachable positions: player {} has no position", playerId);
            return List.of();
        }
        
        return validator.getReachableTiles(currentPosition);
    }
    
    /**
     * Resultado de la ejecución de un movimiento.
     */
    public static class MovementResult {
        private final boolean success;
        private final String message;
        private final HexCoordinate from;
        private final HexCoordinate to;
        private final List<HexCoordinate> path;
        private final int cost;
        private final String biomeEffect;
        
        private MovementResult(boolean success, String message, HexCoordinate from,
                              HexCoordinate to, List<HexCoordinate> path, 
                              int cost, String biomeEffect) {
            this.success = success;
            this.message = message;
            this.from = from;
            this.to = to;
            this.path = path;
            this.cost = cost;
            this.biomeEffect = biomeEffect;
        }
        
        public static MovementResult success(HexCoordinate from, HexCoordinate to,
                                            List<HexCoordinate> path, int cost,
                                            String biomeEffect) {
            return new MovementResult(true, "Movimiento exitoso", from, to, path, cost, biomeEffect);
        }
        
        public static MovementResult failure(String message) {
            return new MovementResult(false, message, null, null, null, 0, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public HexCoordinate getFrom() {
            return from;
        }
        
        public HexCoordinate getTo() {
            return to;
        }
        
        public List<HexCoordinate> getPath() {
            return path;
        }
        
        public int getCost() {
            return cost;
        }
        
        public String getBiomeEffect() {
            return biomeEffect;
        }
    }
}
