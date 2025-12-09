package com.juegito.protocol;

import com.juegito.game.loot.Item;
import com.juegito.protocol.dto.ItemDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversor para items.
 */
public class ItemDTOConverter {
    
    public static ItemDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }
        
        return new ItemDTO(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getType().name(),
            item.getRarity().name(),
            item.getValue(),
            item.isConsumable()
        );
    }
    
    public static List<ItemDTO> toDTOList(List<Item> items) {
        List<ItemDTO> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toDTO(item));
        }
        return dtos;
    }
}
