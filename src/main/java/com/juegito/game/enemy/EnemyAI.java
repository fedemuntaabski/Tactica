package com.juegito.game.enemy;

import com.juegito.model.GameMap;
import com.juegito.model.HexCoordinate;
import com.juegito.model.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * IA simple basada en reglas para enemigos.
 * El servidor controla completamente las decisiones de los enemigos.
 * Implementa KISS: reglas simples y predecibles.
 */
public class EnemyAI {
    private static final Logger logger = LoggerFactory.getLogger(EnemyAI.class);
    
    private final GameMap map;
    private final Random random;
    
    public EnemyAI(GameMap map) {
        this.map = map;
        this.random = new Random();
    }
    
    public EnemyAI(GameMap map, long seed) {
        this.map = map;
        this.random = new Random(seed);
    }
    
    /**
     * Decide la acción del enemigo basándose en reglas simples.
     * Implementa KISS: lógica de decisión en un solo método.
     */
    public EnemyAction decideAction(Enemy enemy, Map<String, HexCoordinate> playerPositions) {
        if (!enemy.isAlive()) {
            return EnemyAction.skip(enemy.getId(), "Enemy is dead");
        }
        
        logger.debug("Enemy {} deciding action at {}", enemy.getId(), enemy.getPosition());
        
        // Encontrar jugador más cercano
        String closestPlayerId = findClosestPlayer(enemy.getPosition(), playerPositions);
        
        if (closestPlayerId == null) {
            // No hay jugadores, moverse aleatoriamente
            return decideRandomMove(enemy);
        }
        
        HexCoordinate targetPos = playerPositions.get(closestPlayerId);
        int distance = enemy.getPosition().distanceTo(targetPos);
        
        // Si está en rango de ataque, atacar
        if (distance <= enemy.getAttackRange()) {
            logger.info("Enemy {} attacking player {} at distance {}", 
                enemy.getId(), closestPlayerId, distance);
            return EnemyAction.attack(enemy.getId(), closestPlayerId);
        }
        
        // Si no está en rango, moverse hacia el jugador más cercano
        HexCoordinate moveTarget = findMoveTowardsTarget(enemy, targetPos);
        
        if (moveTarget != null && !moveTarget.equals(enemy.getPosition())) {
            logger.info("Enemy {} moving towards player {} from {} to {}", 
                enemy.getId(), closestPlayerId, enemy.getPosition(), moveTarget);
            return EnemyAction.move(enemy.getId(), moveTarget);
        }
        
        // No puede moverse, pasar turno
        return EnemyAction.skip(enemy.getId(), "Cannot move towards target");
    }
    
    /**
     * Encuentra el jugador más cercano al enemigo.
     */
    private String findClosestPlayer(HexCoordinate enemyPos, Map<String, HexCoordinate> players) {
        return players.entrySet().stream()
            .min(Comparator.comparingInt(e -> enemyPos.distanceTo(e.getValue())))
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Encuentra la mejor casilla para moverse hacia el objetivo.
     * Implementa DRY: reutiliza la lógica de vecinos del mapa.
     */
    private HexCoordinate findMoveTowardsTarget(Enemy enemy, HexCoordinate target) {
        HexCoordinate currentPos = enemy.getPosition();
        
        // Obtener vecinos accesibles
        List<Tile> neighbors = map.getAccessibleNeighbors(currentPos);
        
        if (neighbors.isEmpty()) {
            return null;
        }
        
        // Encontrar vecino que acerque más al objetivo
        return neighbors.stream()
            .map(Tile::getCoordinate)
            .filter(coord -> !coord.equals(currentPos))
            .filter(coord -> !map.getTile(coord).isOccupied()) // No ocupado
            .min(Comparator.comparingInt(coord -> coord.distanceTo(target)))
            .orElse(null);
    }
    
    /**
     * Decide un movimiento aleatorio cuando no hay objetivos claros.
     */
    private EnemyAction decideRandomMove(Enemy enemy) {
        List<Tile> neighbors = map.getAccessibleNeighbors(enemy.getPosition());
        
        // Filtrar casillas no ocupadas
        List<HexCoordinate> validMoves = neighbors.stream()
            .filter(tile -> !tile.isOccupied())
            .map(Tile::getCoordinate)
            .collect(Collectors.toList());
        
        if (validMoves.isEmpty()) {
            return EnemyAction.skip(enemy.getId(), "No valid moves");
        }
        
        // Elegir movimiento aleatorio
        HexCoordinate randomMove = validMoves.get(random.nextInt(validMoves.size()));
        logger.info("Enemy {} moving randomly to {}", enemy.getId(), randomMove);
        
        return EnemyAction.move(enemy.getId(), randomMove);
    }
    
    /**
     * Acción que puede realizar un enemigo.
     */
    public static class EnemyAction {
        private final ActionType type;
        private final String enemyId;
        private final String targetPlayerId;
        private final HexCoordinate targetPosition;
        private final String reason;
        
        private EnemyAction(ActionType type, String enemyId, String targetPlayerId, 
                          HexCoordinate targetPosition, String reason) {
            this.type = type;
            this.enemyId = enemyId;
            this.targetPlayerId = targetPlayerId;
            this.targetPosition = targetPosition;
            this.reason = reason;
        }
        
        public static EnemyAction attack(String enemyId, String targetPlayerId) {
            return new EnemyAction(ActionType.ATTACK, enemyId, targetPlayerId, null, null);
        }
        
        public static EnemyAction move(String enemyId, HexCoordinate target) {
            return new EnemyAction(ActionType.MOVE, enemyId, null, target, null);
        }
        
        public static EnemyAction skip(String enemyId, String reason) {
            return new EnemyAction(ActionType.SKIP, enemyId, null, null, reason);
        }
        
        public ActionType getType() { return type; }
        public String getEnemyId() { return enemyId; }
        public String getTargetPlayerId() { return targetPlayerId; }
        public HexCoordinate getTargetPosition() { return targetPosition; }
        public String getReason() { return reason; }
        
        public enum ActionType {
            ATTACK,
            MOVE,
            SKIP
        }
    }
}
