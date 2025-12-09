package com.juegito.game.inventory;

/**
 * Piezas de armadura equipables.
 */
public enum ArmorPiece {
    HELMET("Casco/Sombrero", EquipmentSlot.HELMET),
    CHEST("Pecho", EquipmentSlot.CHEST),
    BOOTS("Botas", EquipmentSlot.BOOTS),
    CAPE("Capa", EquipmentSlot.CAPE),
    SHIELD("Escudo", EquipmentSlot.SHIELD);
    
    private final String displayName;
    private final EquipmentSlot slot;
    
    ArmorPiece(String displayName, EquipmentSlot slot) {
        this.displayName = displayName;
        this.slot = slot;
    }
    
    public String getDisplayName() { return displayName; }
    public EquipmentSlot getSlot() { return slot; }
}
