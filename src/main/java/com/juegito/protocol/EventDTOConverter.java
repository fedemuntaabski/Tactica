package com.juegito.protocol;

import com.juegito.game.event.RandomEvent;
import com.juegito.game.event.RandomEventSystem;
import com.juegito.protocol.dto.EventResultDTO;
import com.juegito.protocol.dto.HexCoordinateDTO;
import com.juegito.protocol.dto.RandomEventDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversor para eventos aleatorios.
 */
public class EventDTOConverter {
    
    public static RandomEventDTO toDTO(RandomEvent event) {
        if (event == null) {
            return null;
        }
        
        List<RandomEventDTO.EventOptionDTO> optionDTOs = new ArrayList<>();
        for (RandomEvent.EventOption option : event.getOptions()) {
            optionDTOs.add(new RandomEventDTO.EventOptionDTO(
                option.getIndex(),
                option.getText(),
                option.getDescription()
            ));
        }
        
        HexCoordinateDTO location = new HexCoordinateDTO(
            event.getLocation().getQ(),
            event.getLocation().getR(),
            -event.getLocation().getQ() - event.getLocation().getR()
        );
        
        return new RandomEventDTO(
            event.getId(),
            event.getTitle(),
            event.getDescription(),
            event.getType().name(),
            optionDTOs,
            location
        );
    }
    
    public static EventResultDTO toDTO(RandomEventSystem.EventResult result, String eventId) {
        if (result == null) {
            return null;
        }
        
        return new EventResultDTO(
            eventId,
            result.isSuccess(),
            result.getMessage(),
            ItemDTOConverter.toDTOList(result.getItems()),
            result.getHpChange(),
            result.getEffect()
        );
    }
}
