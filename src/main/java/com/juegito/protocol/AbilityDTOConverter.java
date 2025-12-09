package com.juegito.protocol;

import com.juegito.game.character.Ability;
import com.juegito.game.character.AbilitySystem;
import com.juegito.protocol.dto.AbilityDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversor para habilidades.
 */
public class AbilityDTOConverter {
    
    public static AbilityDTO toDTO(Ability ability, int currentCooldown) {
        if (ability == null) {
            return null;
        }
        
        return new AbilityDTO(
            ability.getId(),
            ability.getName(),
            ability.getDescription(),
            ability.getType().name(),
            ability.getCooldownTurns(),
            ability.getRange(),
            ability.getBasePower(),
            currentCooldown
        );
    }
    
    public static List<AbilityDTO> toDTOList(List<Ability> abilities, AbilitySystem abilitySystem, String playerId) {
        List<AbilityDTO> dtos = new ArrayList<>();
        for (Ability ability : abilities) {
            int cooldown = abilitySystem.getRemainingCooldown(playerId, ability.getId());
            dtos.add(toDTO(ability, cooldown));
        }
        return dtos;
    }
}
