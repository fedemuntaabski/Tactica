package com.juegito.game.character;

import java.util.ArrayList;
import java.util.List;

/**
 * Define una habilidad de jugador.
 */
public class Ability {
    private final String id;
    private final String name;
    private final String description;
    private final AbilityType type;
    private final int cooldownTurns;
    private final int range;
    private final int basePower;
    private final List<String> requiredClasses;
    
    public Ability(String id, String name, String description, AbilityType type,
                   int cooldownTurns, int range, int basePower, List<String> requiredClasses) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.cooldownTurns = cooldownTurns;
        this.range = range;
        this.basePower = basePower;
        this.requiredClasses = new ArrayList<>(requiredClasses);
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public AbilityType getType() { return type; }
    public int getCooldownTurns() { return cooldownTurns; }
    public int getRange() { return range; }
    public int getBasePower() { return basePower; }
    public List<String> getRequiredClasses() { return new ArrayList<>(requiredClasses); }
    
    public boolean canBeUsedBy(String classId) {
        return requiredClasses.isEmpty() || requiredClasses.contains(classId);
    }
    
    public enum AbilityType {
        DAMAGE,
        HEAL,
        BUFF,
        DEBUFF,
        UTILITY
    }
}
