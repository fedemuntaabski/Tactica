package com.juegito.protocol.dto.character;

import java.util.List;

/**
 * Información completa de una clase para mostrar en el lobby.
 */
public class ClassInfoDTO {
    private String classId;
    private String displayName;
    private String role;
    private String description;
    private BaseStatsDTO baseStats;
    private List<String> initialEquipment;
    private AbilityInfoDTO initialAbility;
    private List<AbilityInfoDTO> unlockableAbilities;
    
    public ClassInfoDTO() {}
    
    public ClassInfoDTO(String classId, String displayName, String role, String description,
                        BaseStatsDTO baseStats, List<String> initialEquipment,
                        AbilityInfoDTO initialAbility, List<AbilityInfoDTO> unlockableAbilities) {
        this.classId = classId;
        this.displayName = displayName;
        this.role = role;
        this.description = description;
        this.baseStats = baseStats;
        this.initialEquipment = initialEquipment;
        this.initialAbility = initialAbility;
        this.unlockableAbilities = unlockableAbilities;
    }
    
    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BaseStatsDTO getBaseStats() { return baseStats; }
    public void setBaseStats(BaseStatsDTO baseStats) { this.baseStats = baseStats; }
    
    public List<String> getInitialEquipment() { return initialEquipment; }
    public void setInitialEquipment(List<String> initialEquipment) { this.initialEquipment = initialEquipment; }
    
    public AbilityInfoDTO getInitialAbility() { return initialAbility; }
    public void setInitialAbility(AbilityInfoDTO initialAbility) { this.initialAbility = initialAbility; }
    
    public List<AbilityInfoDTO> getUnlockableAbilities() { return unlockableAbilities; }
    public void setUnlockableAbilities(List<AbilityInfoDTO> unlockableAbilities) { this.unlockableAbilities = unlockableAbilities; }
}
