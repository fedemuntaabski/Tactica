package com.juegito.game.combat;

import com.juegito.model.GameMap;
import com.juegito.model.HexCoordinate;
import com.juegito.model.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Sistema de combate por turnos.
 * El servidor es la autoridad completa sobre el combate.
 * Implementa KISS: lógica simple y clara de combate.
 */
public class CombatSystem {
    private static final Logger logger = LoggerFactory.getLogger(CombatSystem.class);
    
    private final GameMap map;
    private final Random random;
    
    // Constantes de combate
    private static final int MELEE_RANGE = 1;
    private static final int RANGED_RANGE = 3;
    private static final double CRITICAL_CHANCE = 0.15; // 15%
    private static final double CRITICAL_MULTIPLIER = 2.0;
    private static final double MISS_CHANCE = 0.05; // 5%
    
    public CombatSystem(GameMap map) {
        this.map = map;
        this.random = new Random();
    }
    
    public CombatSystem(GameMap map, long seed) {
        this.map = map;
        this.random = new Random(seed);
    }
    
    /**
     * Resuelve un ataque entre dos entidades.
     * Aplica KISS: un método que hace todo el cálculo de combate.
     */
    public CombatResult resolveAttack(String attackerId, String targetId, 
                                     AttackType attackType, int baseDamage) {
        
        logger.debug("Resolving {} attack from {} to {}", attackType, attackerId, targetId);
        
        // Validar posiciones
        HexCoordinate attackerPos = map.getPlayerPosition(attackerId);
        HexCoordinate targetPos = map.getPlayerPosition(targetId);
        
        if (attackerPos == null || targetPos == null) {
            return CombatResult.invalid("Atacante o objetivo no tiene posición válida");
        }
        
        // Validar rango
        int distance = attackerPos.distanceTo(targetPos);
        if (!isInRange(distance, attackType)) {
            return CombatResult.invalid(
                String.format("Objetivo fuera de rango (%d casillas, máximo %d)", 
                    distance, getMaxRange(attackType))
            );
        }
        
        // Determinar si falla
        if (random.nextDouble() < MISS_CHANCE) {
            logger.info("Attack from {} to {} missed!", attackerId, targetId);
            return CombatResult.miss(attackerId, targetId, attackType);
        }
        
        // Calcular defensa del terreno
        Tile targetTile = map.getTile(targetPos);
        int defenseBonus = targetTile != null ? targetTile.getDefenseBonus() : 0;
        
        // Determinar si es crítico
        boolean isCritical = random.nextDouble() < CRITICAL_CHANCE;
        
        // Calcular daño
        int finalDamage = calculateDamage(baseDamage, defenseBonus, isCritical);
        
        logger.info("Attack from {} to {}: {} damage (base: {}, defense: {}, critical: {})", 
            attackerId, targetId, finalDamage, baseDamage, defenseBonus, isCritical);
        
        return CombatResult.hit(attackerId, targetId, attackType, finalDamage, 
                               isCritical, defenseBonus);
    }
    
    /**
     * Verifica si un objetivo está en rango para un tipo de ataque.
     */
    public boolean isInRange(HexCoordinate attacker, HexCoordinate target, AttackType type) {
        if (attacker == null || target == null) {
            return false;
        }
        return isInRange(attacker.distanceTo(target), type);
    }
    
    private boolean isInRange(int distance, AttackType type) {
        return distance <= getMaxRange(type);
    }
    
    private int getMaxRange(AttackType type) {
        switch (type) {
            case MELEE:
                return MELEE_RANGE;
            case RANGED:
                return RANGED_RANGE;
            default:
                return 0;
        }
    }
    
    /**
     * Calcula el daño final considerando defensa y críticos.
     * Implementa DRY: una sola fórmula de cálculo de daño.
     */
    private int calculateDamage(int baseDamage, int defenseBonus, boolean isCritical) {
        double damage = baseDamage;
        
        // Aplicar crítico
        if (isCritical) {
            damage *= CRITICAL_MULTIPLIER;
        }
        
        // Reducir por defensa (mínimo 1 de daño)
        damage = Math.max(1, damage - defenseBonus);
        
        return (int) Math.round(damage);
    }
    
    /**
     * Tipos de ataque disponibles.
     */
    public enum AttackType {
        MELEE,   // Cuerpo a cuerpo (rango 1)
        RANGED   // A distancia (rango 3)
    }
    
    /**
     * Resultado de un ataque.
     * Inmutable para thread-safety.
     */
    public static class CombatResult {
        private final boolean valid;
        private final boolean hit;
        private final String attackerId;
        private final String targetId;
        private final AttackType attackType;
        private final int damage;
        private final boolean critical;
        private final int defenseBonus;
        private final String errorMessage;
        
        private CombatResult(boolean valid, boolean hit, String attackerId, String targetId,
                           AttackType attackType, int damage, boolean critical, 
                           int defenseBonus, String errorMessage) {
            this.valid = valid;
            this.hit = hit;
            this.attackerId = attackerId;
            this.targetId = targetId;
            this.attackType = attackType;
            this.damage = damage;
            this.critical = critical;
            this.defenseBonus = defenseBonus;
            this.errorMessage = errorMessage;
        }
        
        public static CombatResult hit(String attackerId, String targetId, 
                                      AttackType type, int damage, 
                                      boolean critical, int defenseBonus) {
            return new CombatResult(true, true, attackerId, targetId, type, 
                                  damage, critical, defenseBonus, null);
        }
        
        public static CombatResult miss(String attackerId, String targetId, AttackType type) {
            return new CombatResult(true, false, attackerId, targetId, type, 
                                  0, false, 0, null);
        }
        
        public static CombatResult invalid(String errorMessage) {
            return new CombatResult(false, false, null, null, null, 
                                  0, false, 0, errorMessage);
        }
        
        public boolean isValid() { return valid; }
        public boolean isHit() { return hit; }
        public String getAttackerId() { return attackerId; }
        public String getTargetId() { return targetId; }
        public AttackType getAttackType() { return attackType; }
        public int getDamage() { return damage; }
        public boolean isCritical() { return critical; }
        public int getDefenseBonus() { return defenseBonus; }
        public String getErrorMessage() { return errorMessage; }
    }
}
