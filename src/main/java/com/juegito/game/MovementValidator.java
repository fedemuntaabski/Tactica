package com.juegito.game;

import com.juegito.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Valida movimientos de jugadores en el mapa hexagonal.
 * Verifica restricciones de distancia, accesibilidad y ocupación.
 */
public class MovementValidator {
    private static final Logger logger = LoggerFactory.getLogger(MovementValidator.class);
    private static final int MAX_MOVEMENT_RANGE = 3; // Alcance máximo de movimiento por turno
    
    private final GameMap map;
    
    public MovementValidator(GameMap map) {
        this.map = map;
    }
    
    /**
     * Valida si un jugador puede moverse a una coordenada destino.
     */
    public MovementValidation validateMovement(String playerId, HexCoordinate destination) {
        // Verificar que el destino existe en el mapa
        Tile destinationTile = map.getTile(destination);
        if (destinationTile == null) {
            return MovementValidation.invalid("Destino fuera del mapa");
        }
        
        // Verificar que el destino es accesible
        if (!destinationTile.isAccessible()) {
            return MovementValidation.invalid("Destino bloqueado");
        }
        
        // Verificar que el destino no está ocupado por otro jugador
        if (destinationTile.isOccupied() && 
            !playerId.equals(destinationTile.getOccupyingPlayerId())) {
            return MovementValidation.invalid("Casilla ocupada por otro jugador");
        }
        
        // Obtener posición actual del jugador
        HexCoordinate currentPosition = map.getPlayerPosition(playerId);
        if (currentPosition == null) {
            return MovementValidation.invalid("Jugador no tiene posición inicial");
        }
        
        // Verificar que no está intentando quedarse en el mismo lugar
        if (currentPosition.equals(destination)) {
            return MovementValidation.invalid("Ya estás en esa posición");
        }
        
        // Calcular camino y verificar alcance
        List<HexCoordinate> path = findPath(currentPosition, destination);
        if (path == null || path.isEmpty()) {
            return MovementValidation.invalid("No hay camino disponible al destino");
        }
        
        // Calcular costo total del movimiento
        int movementCost = calculatePathCost(path);
        if (movementCost > MAX_MOVEMENT_RANGE) {
            return MovementValidation.invalid(
                String.format("Destino demasiado lejos (costo: %d, máximo: %d)", 
                    movementCost, MAX_MOVEMENT_RANGE));
        }
        
        return MovementValidation.valid(path, movementCost);
    }
    
    /**
     * Encuentra el camino más corto entre dos coordenadas usando A*.
     */
    private List<HexCoordinate> findPath(HexCoordinate start, HexCoordinate goal) {
        Map<HexCoordinate, HexCoordinate> cameFrom = new HashMap<>();
        Map<HexCoordinate, Integer> gScore = new HashMap<>();
        Map<HexCoordinate, Integer> fScore = new HashMap<>();
        
        PriorityQueue<HexCoordinate> openSet = new PriorityQueue<>(
            Comparator.comparingInt(coord -> fScore.getOrDefault(coord, Integer.MAX_VALUE))
        );
        
        gScore.put(start, 0);
        fScore.put(start, start.distanceTo(goal));
        openSet.add(start);
        
        while (!openSet.isEmpty()) {
            HexCoordinate current = openSet.poll();
            
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }
            
            for (Tile neighbor : map.getAccessibleNeighbors(current)) {
                HexCoordinate neighborCoord = neighbor.getCoordinate();
                
                // Saltar si está ocupado (excepto el destino)
                if (neighbor.isOccupied() && !neighborCoord.equals(goal)) {
                    continue;
                }
                
                int tentativeGScore = gScore.get(current) + neighbor.getMovementCost();
                
                if (tentativeGScore < gScore.getOrDefault(neighborCoord, Integer.MAX_VALUE)) {
                    cameFrom.put(neighborCoord, current);
                    gScore.put(neighborCoord, tentativeGScore);
                    fScore.put(neighborCoord, tentativeGScore + neighborCoord.distanceTo(goal));
                    
                    if (!openSet.contains(neighborCoord)) {
                        openSet.add(neighborCoord);
                    }
                }
            }
        }
        
        return null; // No hay camino
    }
    
    /**
     * Reconstruye el camino desde el destino al inicio.
     */
    private List<HexCoordinate> reconstructPath(
            Map<HexCoordinate, HexCoordinate> cameFrom, 
            HexCoordinate current) {
        
        List<HexCoordinate> path = new ArrayList<>();
        path.add(current);
        
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(0, current);
        }
        
        return path;
    }
    
    /**
     * Calcula el costo total de un camino.
     */
    private int calculatePathCost(List<HexCoordinate> path) {
        if (path.size() <= 1) {
            return 0;
        }
        
        int cost = 0;
        // Saltar el primer elemento (posición actual)
        for (int i = 1; i < path.size(); i++) {
            Tile tile = map.getTile(path.get(i));
            if (tile != null) {
                cost += tile.getMovementCost();
            }
        }
        
        return cost;
    }
    
    /**
     * Obtiene todas las casillas alcanzables desde una posición.
     */
    public List<HexCoordinate> getReachableTiles(HexCoordinate from) {
        List<HexCoordinate> reachable = new ArrayList<>();
        Set<HexCoordinate> visited = new HashSet<>();
        Queue<PathNode> queue = new LinkedList<>();
        
        queue.add(new PathNode(from, 0));
        visited.add(from);
        
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            
            if (current.cost > MAX_MOVEMENT_RANGE) {
                continue;
            }
            
            reachable.add(current.coordinate);
            
            for (Tile neighbor : map.getAccessibleNeighbors(current.coordinate)) {
                HexCoordinate neighborCoord = neighbor.getCoordinate();
                
                if (!visited.contains(neighborCoord) && !neighbor.isOccupied()) {
                    int newCost = current.cost + neighbor.getMovementCost();
                    if (newCost <= MAX_MOVEMENT_RANGE) {
                        visited.add(neighborCoord);
                        queue.add(new PathNode(neighborCoord, newCost));
                    }
                }
            }
        }
        
        return reachable;
    }
    
    /**
     * Clase auxiliar para pathfinding.
     */
    private static class PathNode {
        final HexCoordinate coordinate;
        final int cost;
        
        PathNode(HexCoordinate coordinate, int cost) {
            this.coordinate = coordinate;
            this.cost = cost;
        }
    }
    
    /**
     * Resultado de validación de movimiento.
     */
    public static class MovementValidation {
        private final boolean valid;
        private final String errorMessage;
        private final List<HexCoordinate> path;
        private final int cost;
        
        private MovementValidation(boolean valid, String errorMessage, 
                                   List<HexCoordinate> path, int cost) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.path = path;
            this.cost = cost;
        }
        
        public static MovementValidation valid(List<HexCoordinate> path, int cost) {
            return new MovementValidation(true, null, path, cost);
        }
        
        public static MovementValidation invalid(String errorMessage) {
            return new MovementValidation(false, errorMessage, null, 0);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public List<HexCoordinate> getPath() {
            return path;
        }
        
        public int getCost() {
            return cost;
        }
    }
}
