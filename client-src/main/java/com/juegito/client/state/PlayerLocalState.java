package com.juegito.client.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Estado local del jugador.
 * Mantiene información específica del jugador: HP, inventario, posición, cooldowns.
 * Implementa KISS: Solo gestiona datos del jugador local, no lógica de juego.
 */
public class PlayerLocalState {
    private static final Logger logger = LoggerFactory.getLogger(PlayerLocalState.class);
    
    private String playerId;
    private String playerName;
    
    // Estadísticas del jugador
    private int currentHP;
    private int maxHP;
    private boolean isDead;
    
    // Posición
    private int posQ;
    private int posR;
    
    // Inventario
    private final List<String> inventory;
    private static final int MAX_INVENTORY_SIZE = 10;
    
    // Cooldowns de habilidades
    private final Map<String, Long> cooldowns;
    
    // Estado de buffs/debuffs
    private final Map<String, Integer> statusEffects;
    
    public PlayerLocalState() {
        this.inventory = new ArrayList<>();
        this.cooldowns = new HashMap<>();
        this.statusEffects = new HashMap<>();
        this.currentHP = 100;
        this.maxHP = 100;
        this.isDead = false;
    }
    
    /**
     * Actualiza HP del jugador.
     */
    public void updateHP(int newHP) {
        this.currentHP = Math.max(0, Math.min(newHP, maxHP));
        this.isDead = this.currentHP == 0;
        
        logger.debug("HP updated: {}/{}", currentHP, maxHP);
        
        if (isDead) {
            logger.warn("Player is dead!");
        }
    }
    
    /**
     * Aplica daño al jugador.
     */
    public void takeDamage(int damage) {
        updateHP(currentHP - damage);
        logger.info("Took {} damage, HP: {}/{}", damage, currentHP, maxHP);
    }
    
    /**
     * Cura al jugador.
     */
    public void heal(int amount) {
        updateHP(currentHP + amount);
        logger.info("Healed {} HP, current: {}/{}", amount, currentHP, maxHP);
    }
    
    /**
     * Actualiza posición del jugador.
     */
    public void updatePosition(int q, int r) {
        this.posQ = q;
        this.posR = r;
        logger.debug("Position updated to ({}, {})", q, r);
    }
    
    /**
     * Agrega un item al inventario.
     */
    public boolean addItem(String itemId) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            logger.warn("Inventory is full");
            return false;
        }
        
        inventory.add(itemId);
        logger.info("Item added to inventory: {}", itemId);
        return true;
    }
    
    /**
     * Remueve un item del inventario.
     */
    public boolean removeItem(String itemId) {
        boolean removed = inventory.remove(itemId);
        if (removed) {
            logger.info("Item removed from inventory: {}", itemId);
        }
        return removed;
    }
    
    /**
     * Verifica si tiene un item.
     */
    public boolean hasItem(String itemId) {
        return inventory.contains(itemId);
    }
    
    /**
     * Establece cooldown para una habilidad.
     */
    public void setCooldown(String abilityId, long durationMs) {
        long expirationTime = System.currentTimeMillis() + durationMs;
        cooldowns.put(abilityId, expirationTime);
        logger.debug("Cooldown set for {}: {}ms", abilityId, durationMs);
    }
    
    /**
     * Verifica si una habilidad está en cooldown.
     */
    public boolean isOnCooldown(String abilityId) {
        Long expirationTime = cooldowns.get(abilityId);
        if (expirationTime == null) {
            return false;
        }
        
        boolean onCooldown = System.currentTimeMillis() < expirationTime;
        if (!onCooldown) {
            cooldowns.remove(abilityId);
        }
        return onCooldown;
    }
    
    /**
     * Obtiene tiempo restante de cooldown en milisegundos.
     */
    public long getCooldownRemaining(String abilityId) {
        Long expirationTime = cooldowns.get(abilityId);
        if (expirationTime == null) {
            return 0;
        }
        
        long remaining = expirationTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    /**
     * Agrega un efecto de estado.
     */
    public void addStatusEffect(String effectId, int duration) {
        statusEffects.put(effectId, duration);
        logger.info("Status effect added: {} for {} turns", effectId, duration);
    }
    
    /**
     * Remueve un efecto de estado.
     */
    public void removeStatusEffect(String effectId) {
        statusEffects.remove(effectId);
        logger.info("Status effect removed: {}", effectId);
    }
    
    /**
     * Verifica si tiene un efecto activo.
     */
    public boolean hasStatusEffect(String effectId) {
        return statusEffects.containsKey(effectId);
    }
    
    /**
     * Reduce duración de efectos (llamar al inicio de turno).
     */
    public void updateStatusEffects() {
        Iterator<Map.Entry<String, Integer>> iterator = statusEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            int newDuration = entry.getValue() - 1;
            if (newDuration <= 0) {
                logger.info("Status effect expired: {}", entry.getKey());
                iterator.remove();
            } else {
                entry.setValue(newDuration);
            }
        }
    }
    
    /**
     * Resetea el estado del jugador.
     */
    public void reset() {
        currentHP = maxHP;
        isDead = false;
        inventory.clear();
        cooldowns.clear();
        statusEffects.clear();
        logger.info("Player state reset");
    }
    
    // Getters
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getCurrentHP() {
        return currentHP;
    }
    
    public int getMaxHP() {
        return maxHP;
    }
    
    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
        this.currentHP = Math.min(currentHP, maxHP);
    }
    
    public boolean isDead() {
        return isDead;
    }
    
    public int getPosQ() {
        return posQ;
    }
    
    public int getPosR() {
        return posR;
    }
    
    public List<String> getInventory() {
        return Collections.unmodifiableList(inventory);
    }
    
    public int getInventorySize() {
        return inventory.size();
    }
    
    public int getMaxInventorySize() {
        return MAX_INVENTORY_SIZE;
    }
    
    public Map<String, Integer> getStatusEffects() {
        return Collections.unmodifiableMap(statusEffects);
    }
    
    public float getHPPercentage() {
        return maxHP > 0 ? (float) currentHP / maxHP : 0f;
    }
}
