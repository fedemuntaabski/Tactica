package com.juegito.game.ability;

import com.juegito.model.HexCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sistema de habilidades de jugadores.
 * El servidor valida y controla el uso de habilidades.
 * Implementa KISS: validación simple y ejecución clara.
 */
public class AbilitySystem {
    private static final Logger logger = LoggerFactory.getLogger(AbilitySystem.class);
    
    private final Map<String, Ability> abilities;
    private final Map<String, Map<String, Integer>> playerCooldowns; // playerId -> (abilityId -> turnsRemaining)
    
    public AbilitySystem() {
        this.abilities = new HashMap<>();
        this.playerCooldowns = new HashMap<>();
        initializeAbilities();
    }
    
    /**
     * Inicializa las habilidades disponibles por clase.
     * Implementa DRY: definición centralizada de habilidades.
     */
    private void initializeAbilities() {
        // Warrior (Guerrero)
        addAbility(new Ability("power_strike", "Golpe Poderoso", 
            "Ataque cuerpo a cuerpo con daño aumentado",
            Ability.AbilityType.DAMAGE, 3, 1, 20, Arrays.asList("Warrior")));
        
        addAbility(new Ability("shield_bash", "Golpe de Escudo",
            "Ataca y reduce defensa del enemigo",
            Ability.AbilityType.DEBUFF, 4, 1, 10, Arrays.asList("Warrior")));
        
        // Mage (Mago)
        addAbility(new Ability("fireball", "Bola de Fuego",
            "Proyectil de fuego a distancia",
            Ability.AbilityType.DAMAGE, 2, 4, 25, Arrays.asList("Mage")));
        
        addAbility(new Ability("heal", "Curación",
            "Restaura HP de un aliado",
            Ability.AbilityType.HEAL, 4, 3, 20, Arrays.asList("Mage")));
        
        // Ranger (Explorador)
        addAbility(new Ability("aimed_shot", "Disparo Certero",
            "Disparo de precisión con chance aumentada de crítico",
            Ability.AbilityType.DAMAGE, 3, 4, 18, Arrays.asList("Ranger")));
        
        addAbility(new Ability("quick_step", "Paso Rápido",
            "Aumenta rango de movimiento por 1 turno",
            Ability.AbilityType.BUFF, 5, 0, 0, Arrays.asList("Ranger")));
        
        // Rogue (Pícaro)
        addAbility(new Ability("backstab", "Apuñalar",
            "Ataque sorpresa con daño crítico garantizado",
            Ability.AbilityType.DAMAGE, 4, 1, 15, Arrays.asList("Rogue")));
        
        addAbility(new Ability("smoke_bomb", "Bomba de Humo",
            "Reduce precisión de enemigos cercanos",
            Ability.AbilityType.DEBUFF, 5, 2, 0, Arrays.asList("Rogue")));
        
        logger.info("Initialized {} abilities", abilities.size());
    }
    
    private void addAbility(Ability ability) {
        abilities.put(ability.getId(), ability);
    }
    
    /**
     * Obtiene las habilidades disponibles para una clase.
     */
    public List<Ability> getAbilitiesForClass(String classId) {
        List<Ability> classAbilities = new ArrayList<>();
        for (Ability ability : abilities.values()) {
            if (ability.canBeUsedBy(classId)) {
                classAbilities.add(ability);
            }
        }
        return classAbilities;
    }
    
    /**
     * Valida si un jugador puede usar una habilidad.
     * Implementa KISS: validaciones claras en orden lógico.
     */
    public AbilityValidation canUseAbility(String playerId, String abilityId, 
                                          String playerClass, HexCoordinate playerPos,
                                          HexCoordinate targetPos) {
        
        // Verificar que la habilidad existe
        Ability ability = abilities.get(abilityId);
        if (ability == null) {
            return AbilityValidation.invalid("Habilidad no existe");
        }
        
        // Verificar que la clase puede usar esta habilidad
        if (!ability.canBeUsedBy(playerClass)) {
            return AbilityValidation.invalid(
                String.format("La clase %s no puede usar %s", playerClass, ability.getName()));
        }
        
        // Verificar cooldown
        if (isOnCooldown(playerId, abilityId)) {
            int turnsRemaining = getCooldownRemaining(playerId, abilityId);
            return AbilityValidation.invalid(
                String.format("Habilidad en cooldown (%d turnos restantes)", turnsRemaining));
        }
        
        // Verificar rango (si requiere objetivo)
        if (targetPos != null && playerPos != null) {
            int distance = playerPos.distanceTo(targetPos);
            if (distance > ability.getRange()) {
                return AbilityValidation.invalid(
                    String.format("Objetivo fuera de rango (distancia: %d, máximo: %d)", 
                        distance, ability.getRange()));
            }
        }
        
        return AbilityValidation.valid(ability);
    }
    
    /**
     * Registra el uso de una habilidad y activa su cooldown.
     * @return true si se usó exitosamente, false si no existe
     */
    public boolean useAbility(String playerId, String abilityId) {
        Ability ability = abilities.get(abilityId);
        if (ability == null) {
            return false;
        }
        
        setCooldown(playerId, abilityId, ability.getCooldownTurns());
        logger.info("Player {} used ability {} (cooldown: {} turns)", 
            playerId, ability.getName(), ability.getCooldownTurns());
        return true;
    }
    
    /**
     * Reduce cooldowns de un jugador al inicio de su turno.
     */
    public void updateCooldowns(String playerId) {
        Map<String, Integer> cooldowns = playerCooldowns.get(playerId);
        if (cooldowns == null) {
            return;
        }
        
        Iterator<Map.Entry<String, Integer>> iterator = cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int newValue = entry.getValue() - 1;
            if (newValue <= 0) {
                iterator.remove();
                logger.debug("Ability {} cooldown expired for player {}", 
                    entry.getKey(), playerId);
            } else {
                entry.setValue(newValue);
            }
        }
    }
    
    private void setCooldown(String playerId, String abilityId, int turns) {
        playerCooldowns
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .put(abilityId, turns);
    }
    
    private boolean isOnCooldown(String playerId, String abilityId) {
        Map<String, Integer> cooldowns = playerCooldowns.get(playerId);
        return cooldowns != null && cooldowns.containsKey(abilityId);
    }
    
    private int getCooldownRemaining(String playerId, String abilityId) {
        Map<String, Integer> cooldowns = playerCooldowns.get(playerId);
        return cooldowns != null ? cooldowns.getOrDefault(abilityId, 0) : 0;
    }
    
    /**
     * Verifica si una habilidad está disponible (sin cooldown).
     */
    public boolean isAbilityAvailable(String playerId, String abilityId) {
        return !isOnCooldown(playerId, abilityId);
    }
    
    /**
     * Obtiene los turnos restantes de cooldown de una habilidad.
     */
    public int getRemainingCooldown(String playerId, String abilityId) {
        return getCooldownRemaining(playerId, abilityId);
    }
    
    public Ability getAbility(String abilityId) {
        return abilities.get(abilityId);
    }
    
    /**
     * Resultado de validación de habilidad.
     */
    public static class AbilityValidation {
        private final boolean valid;
        private final Ability ability;
        private final String errorMessage;
        
        private AbilityValidation(boolean valid, Ability ability, String errorMessage) {
            this.valid = valid;
            this.ability = ability;
            this.errorMessage = errorMessage;
        }
        
        public static AbilityValidation valid(Ability ability) {
            return new AbilityValidation(true, ability, null);
        }
        
        public static AbilityValidation invalid(String errorMessage) {
            return new AbilityValidation(false, null, errorMessage);
        }
        
        public boolean isValid() { return valid; }
        public Ability getAbility() { return ability; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    /**
     * Obtiene todos los cooldowns de un jugador.
     * Retorna un mapa con abilityId -> turnosRestantes.
     */
    public Map<String, Integer> getPlayerCooldowns(String playerId) {
        return new HashMap<>(playerCooldowns.getOrDefault(playerId, new HashMap<>()));
    }
}
