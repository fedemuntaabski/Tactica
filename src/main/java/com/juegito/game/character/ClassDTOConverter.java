package com.juegito.game.character;

import com.juegito.protocol.dto.character.AbilityInfoDTO;
import com.juegito.protocol.dto.character.AvailableClassesDTO;
import com.juegito.protocol.dto.character.BaseStatsDTO;
import com.juegito.protocol.dto.character.ClassInfoDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversor entre PlayerClass/Ability y DTOs.
 */
public class ClassDTOConverter {
    
    /**
     * Convierte todas las clases a AvailableClassesDTO.
     */
    public static AvailableClassesDTO toAvailableClassesDTO() {
        List<ClassInfoDTO> classDTOs = new ArrayList<>();
        
        for (PlayerClass playerClass : PlayerClass.values()) {
            classDTOs.add(toClassInfoDTO(playerClass));
        }
        
        return new AvailableClassesDTO(classDTOs);
    }
    
    /**
     * Convierte una clase a ClassInfoDTO.
     */
    public static ClassInfoDTO toClassInfoDTO(PlayerClass playerClass) {
        BaseStatsDTO stats = new BaseStatsDTO(
            playerClass.getBaseStats().getHp(),
            playerClass.getBaseStats().getDefense(),
            playerClass.getBaseStats().getAttack(),
            playerClass.getBaseStats().getSpeed()
        );
        
        Ability initialAbility = ClassAbilities.getInitialAbility(playerClass);
        AbilityInfoDTO initialAbilityDTO = toAbilityInfoDTO(initialAbility);
        
        List<Ability> unlockableAbilities = ClassAbilities.getUnlockableAbilities(playerClass);
        List<AbilityInfoDTO> unlockableDTO = new ArrayList<>();
        for (Ability ability : unlockableAbilities) {
            unlockableDTO.add(toAbilityInfoDTO(ability));
        }
        
        return new ClassInfoDTO(
            playerClass.name(),
            playerClass.getDisplayName(),
            playerClass.getRole(),
            playerClass.getDescription(),
            stats,
            playerClass.getInitialEquipment(),
            initialAbilityDTO,
            unlockableDTO
        );
    }
    
    /**
     * Convierte una habilidad a AbilityInfoDTO.
     */
    public static AbilityInfoDTO toAbilityInfoDTO(Ability ability) {
        if (ability == null) {
            return null;
        }
        
        return new AbilityInfoDTO(
            ability.getId(),
            ability.getName(),
            ability.getDescription(),
            ability.getType().name()
        );
    }
}
