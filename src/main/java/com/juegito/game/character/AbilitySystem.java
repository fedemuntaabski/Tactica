package com.juegito.game.character;

import com.juegito.model.HexCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sistema de habilidades de jugadores.
 * Usa ClassAbilities para obtener las habilidades por clase.
 */
public class AbilitySystem {
    private static final Logger logger = LoggerFactory.getLogger(AbilitySystem.class);
    
    private final Map<String, Map<String, Integer>> playerCooldowns;
    
    public AbilitySystem() {
        this.playerCooldowns = new HashMap<>();
    }
    
    public List<Ability> getAbilitiesForClass(PlayerClass playerClass) {
        return ClassAbilities.getAbilitiesForClass(playerClass);
    }
    
    public Ability getInitialAbility(PlayerClass playerClass) {
        return ClassAbilities.getInitialAbility(playerClass);
    }
    
    public List<Ability> getUnlockableAbilities(PlayerClass playerClass) {
        return ClassAbilities.getUnlockableAbilities(playerClass);
    }
    
    public AbilityValidation canUseAbility(String playerId, String abilityId, 
                                          PlayerClass playerClass, HexCoordinate playerPos,
                                          HexCoordinate targetPos) {
        
        List<Ability> classAbilities = getAbilitiesForClass(playerClass);
        Ability ability = classAbilities.stream()
            .filter(a -> a.getId().equals(abilityId))
            .findFirst()
            .orElse(null);
        
        if (ability == null) {
            return AbilityValidation.invalid("Habilidad no existe");
        }
        
        if (!ability.canBeUsedBy(playerClass.name())) {
            return AbilityValidation.invalid(
                String.format("La clase %s no puede usar %s", playerClass.name(), ability.getName()));
        }
        
        if (isOnCooldown(playerId, abilityId)) {
            int turnsRemaining = getCooldownRemaining(playerId, abilityId);
            return AbilityValidation.invalid(
                String.format("Habilidad en cooldown (%d turnos restantes)", turnsRemaining));
        }
        
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
    
    public boolean useAbility(String playerId, String abilityId, PlayerClass playerClass) {
        List<Ability> classAbilities = getAbilitiesForClass(playerClass);
        Ability ability = classAbilities.stream()
            .filter(a -> a.getId().equals(abilityId))
            .findFirst()
            .orElse(null);
        
        if (ability == null) {
            return false;
        }
        
        setCooldown(playerId, abilityId, ability.getCooldownTurns());
        logger.info("Player {} used ability {} (cooldown: {} turns)", 
            playerId, ability.getName(), ability.getCooldownTurns());
        return true;
    }
    
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
    
    public boolean isAbilityAvailable(String playerId, String abilityId) {
        return !isOnCooldown(playerId, abilityId);
    }
    
    public int getRemainingCooldown(String playerId, String abilityId) {
        return getCooldownRemaining(playerId, abilityId);
    }
    
    public Map<String, Integer> getPlayerCooldowns(String playerId) {
        return new HashMap<>(playerCooldowns.getOrDefault(playerId, new HashMap<>()));
    }
    
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
}
