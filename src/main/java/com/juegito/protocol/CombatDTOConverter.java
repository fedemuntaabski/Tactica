package com.juegito.protocol;

import com.juegito.game.combat.CombatSystem;
import com.juegito.protocol.dto.CombatResultDTO;

/**
 * Conversor para resultados de combate.
 */
public class CombatDTOConverter {
    
    public static CombatResultDTO toDTO(CombatSystem.CombatResult result) {
        if (result == null) {
            return null;
        }
        
        return new CombatResultDTO(
            result.getAttackerId(),
            result.getTargetId(),
            result.getAttackType().name(),
            result.isHit(),
            result.getDamage(),
            result.isCritical(),
            result.getDefenseBonus(),
            0,  // targetRemainingHP - debe ser calculado por GameServer
            false  // targetDead - debe ser determinado por GameServer
        );
    }
}
