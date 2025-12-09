package com.juegito.protocol;

import com.juegito.game.enemy.Enemy;
import com.juegito.protocol.dto.EnemyDTO;
import com.juegito.protocol.dto.HexCoordinateDTO;

/**
 * Conversor para enemigos.
 */
public class EnemyDTOConverter {
    
    public static EnemyDTO toDTO(Enemy enemy) {
        if (enemy == null) {
            return null;
        }
        
        HexCoordinateDTO position = new HexCoordinateDTO(
            enemy.getPosition().getQ(),
            enemy.getPosition().getR(),
            -enemy.getPosition().getQ() - enemy.getPosition().getR()  // s = -q - r en coordenadas cúbicas
        );
        
        return new EnemyDTO(
            enemy.getId(),
            enemy.getName(),
            enemy.getType().name(),
            position,
            enemy.getCurrentHP(),
            enemy.getMaxHP(),
            enemy.getBaseDamage(),
            enemy.getMoveRange(),
            enemy.getAttackRange(),
            enemy.isAlive()
        );
    }
}
