package com.juegito.protocol.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO para distribución de loot a jugadores.
 */
public class LootDistributionDTO {
    private Map<String, List<ItemDTO>> playerLoot; // playerId -> items
    private String source; // Descripción de la fuente del loot
    
    public LootDistributionDTO() {
        this.playerLoot = new HashMap<>();
    }
    
    public LootDistributionDTO(Map<String, List<ItemDTO>> playerLoot, String source) {
        this.playerLoot = new HashMap<>(playerLoot);
        this.source = source;
    }
    
    public Map<String, List<ItemDTO>> getPlayerLoot() {
        return new HashMap<>(playerLoot);
    }
    
    public void setPlayerLoot(Map<String, List<ItemDTO>> playerLoot) {
        this.playerLoot = new HashMap<>(playerLoot);
    }
    
    public List<ItemDTO> getLootForPlayer(String playerId) {
        return playerLoot.getOrDefault(playerId, new ArrayList<>());
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
}
