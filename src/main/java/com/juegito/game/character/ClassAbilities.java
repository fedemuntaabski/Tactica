package com.juegito.game.character;

import java.util.*;

/**
 * Define las habilidades específicas de cada clase.
 * Sistema de progresión: habilidad inicial + desbloqueos.
 */
public class ClassAbilities {
    
    private static final Map<PlayerClass, List<Ability>> CLASS_ABILITIES = new HashMap<>();
    
    static {
        // GUARDIAN - Guarda del Alba
        CLASS_ABILITIES.put(PlayerClass.GUARDIAN, Arrays.asList(
            new Ability("muralla_viviente", "Muralla Viviente",
                "Aumenta su defensa por 1 turno y obliga a enemigos cercanos a atacarlo",
                Ability.AbilityType.BUFF, 4, 0, 0, Arrays.asList("GUARDIAN")),
            new Ability("golpe_escudo", "Golpe de Escudo",
                "Aturde a un solo enemigo",
                Ability.AbilityType.DEBUFF, 3, 1, 15, Arrays.asList("GUARDIAN")),
            new Ability("proteccion_sagrada", "Protección Sagrada",
                "Da un escudo temporal a un aliado",
                Ability.AbilityType.BUFF, 5, 3, 0, Arrays.asList("GUARDIAN"))
        ));
        
        // RANGER - Cazador Errante
        CLASS_ABILITIES.put(PlayerClass.RANGER, Arrays.asList(
            new Ability("disparo_preciso", "Disparo Preciso",
                "Flecha que aumenta probabilidad de crítico",
                Ability.AbilityType.DAMAGE, 3, 4, 20, Arrays.asList("RANGER")),
            new Ability("flecha_penetrante", "Flecha Penetrante",
                "Atraviesa múltiples objetivos en línea",
                Ability.AbilityType.DAMAGE, 5, 5, 18, Arrays.asList("RANGER")),
            new Ability("paso_ligero", "Paso Ligero",
                "Mueve +1 casilla o evade un ataque",
                Ability.AbilityType.UTILITY, 4, 0, 0, Arrays.asList("RANGER"))
        ));
        
        // MAGE - Erudito Arcano
        CLASS_ABILITIES.put(PlayerClass.MAGE, Arrays.asList(
            new Ability("chispa_arcana", "Chispa Arcana",
                "Daño leve en área pequeña (radio 1)",
                Ability.AbilityType.DAMAGE, 3, 3, 18, Arrays.asList("MAGE")),
            new Ability("prision_energia", "Prisión de Energía",
                "Ralentiza enemigos en un área",
                Ability.AbilityType.DEBUFF, 5, 4, 0, Arrays.asList("MAGE")),
            new Ability("descarga_runica", "Descarga Rúnica",
                "Daño masivo a un solo objetivo cargado",
                Ability.AbilityType.DAMAGE, 6, 4, 35, Arrays.asList("MAGE"))
        ));
        
        // CLERIC - Clérigo del Sendero
        CLASS_ABILITIES.put(PlayerClass.CLERIC, Arrays.asList(
            new Ability("rezo_curativo", "Rezo Curativo",
                "Cura leve a un aliado",
                Ability.AbilityType.HEAL, 2, 3, 20, Arrays.asList("CLERIC")),
            new Ability("luz_restauradora", "Luz Restauradora",
                "Cura en área pequeña (radio 1)",
                Ability.AbilityType.HEAL, 4, 2, 15, Arrays.asList("CLERIC")),
            new Ability("bendicion_firme", "Bendición Firme",
                "Buff de defensa temporal a un aliado",
                Ability.AbilityType.BUFF, 4, 4, 0, Arrays.asList("CLERIC"))
        ));
        
        // ROGUE - Pícaro Sombrío
        CLASS_ABILITIES.put(PlayerClass.ROGUE, Arrays.asList(
            new Ability("golpe_sombra", "Golpe Sombra",
                "Ataque con daño aumentado si el objetivo no te atacó aún",
                Ability.AbilityType.DAMAGE, 3, 1, 25, Arrays.asList("ROGUE")),
            new Ability("trampa_espinas", "Trampa de Espinas",
                "Área pequeña que hace daño cuando pasa un enemigo",
                Ability.AbilityType.UTILITY, 5, 2, 15, Arrays.asList("ROGUE")),
            new Ability("evasion_perfecta", "Evasión Perfecta",
                "Evita el siguiente daño recibido",
                Ability.AbilityType.UTILITY, 6, 0, 0, Arrays.asList("ROGUE"))
        ));
    }
    
    /**
     * Obtiene todas las habilidades de una clase.
     */
    public static List<Ability> getAbilitiesForClass(PlayerClass playerClass) {
        return new ArrayList<>(CLASS_ABILITIES.getOrDefault(playerClass, Collections.emptyList()));
    }
    
    /**
     * Obtiene la habilidad inicial de una clase.
     */
    public static Ability getInitialAbility(PlayerClass playerClass) {
        List<Ability> abilities = CLASS_ABILITIES.get(playerClass);
        return abilities != null && !abilities.isEmpty() ? abilities.get(0) : null;
    }
    
    /**
     * Obtiene las habilidades desbloqueables (todas menos la inicial).
     */
    public static List<Ability> getUnlockableAbilities(PlayerClass playerClass) {
        List<Ability> abilities = CLASS_ABILITIES.get(playerClass);
        if (abilities == null || abilities.size() <= 1) {
            return Collections.emptyList();
        }
        return new ArrayList<>(abilities.subList(1, abilities.size()));
    }
    
    /**
     * Verifica si una habilidad pertenece a una clase.
     */
    public static boolean abilityBelongsToClass(String abilityId, PlayerClass playerClass) {
        List<Ability> abilities = CLASS_ABILITIES.get(playerClass);
        if (abilities == null) {
            return false;
        }
        return abilities.stream().anyMatch(a -> a.getId().equals(abilityId));
    }
}
