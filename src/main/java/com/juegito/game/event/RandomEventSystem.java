package com.juegito.game.event;

import com.juegito.game.loot.Item;
import com.juegito.game.loot.LootSystem;
import com.juegito.model.GameMap;
import com.juegito.model.HexCoordinate;
import com.juegito.model.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sistema de eventos aleatorios.
 * El servidor decide cuándo y dónde ocurren eventos.
 * Implementa KISS: generación simple basada en probabilidades.
 */
public class RandomEventSystem {
    private static final Logger logger = LoggerFactory.getLogger(RandomEventSystem.class);
    
    private final GameMap map;
    private final Random random;
    private final LootSystem lootSystem;
    
    private final Map<String, RandomEvent> activeEvents; // eventId -> event
    private int eventIdCounter = 0;
    
    // Probabilidades de eventos
    private static final double EVENT_CHANCE_PER_TURN = 0.15; // 15% por turno
    
    public RandomEventSystem(GameMap map, LootSystem lootSystem) {
        this.map = map;
        this.random = new Random();
        this.lootSystem = lootSystem;
        this.activeEvents = new HashMap<>();
    }
    
    public RandomEventSystem(GameMap map, LootSystem lootSystem, long seed) {
        this.map = map;
        this.random = new Random(seed);
        this.lootSystem = lootSystem;
        this.activeEvents = new HashMap<>();
    }
    
    /**
     * Decide si se genera un evento este turno.
     * Implementa KISS: probabilidad simple por turno.
     */
    public RandomEvent maybeGenerateEvent(int turnNumber, List<HexCoordinate> occupiedPositions) {
        // Probabilidad de evento aumenta con el número de turno
        double adjustedChance = Math.min(EVENT_CHANCE_PER_TURN * (1 + turnNumber * 0.02), 0.4);
        
        if (random.nextDouble() > adjustedChance) {
            return null;
        }
        
        // Seleccionar tipo de evento
        RandomEvent.EventType type = selectEventType();
        
        // Seleccionar ubicación válida
        HexCoordinate location = selectEventLocation(occupiedPositions);
        if (location == null) {
            return null;
        }
        
        // Generar evento
        RandomEvent event = createEvent(type, location);
        if (event != null) {
            activeEvents.put(event.getId(), event);
            logger.info("Generated {} event at {} (turn {})", type, location, turnNumber);
        }
        
        return event;
    }
    
    private RandomEvent.EventType selectEventType() {
        double roll = random.nextDouble();
        
        // Distribución de tipos de eventos
        if (roll < 0.35) return RandomEvent.EventType.CHEST;
        if (roll < 0.55) return RandomEvent.EventType.TRAP;
        if (roll < 0.75) return RandomEvent.EventType.ENCOUNTER;
        if (roll < 0.90) return RandomEvent.EventType.SHRINE;
        return RandomEvent.EventType.MERCHANT;
    }
    
    private HexCoordinate selectEventLocation(List<HexCoordinate> occupiedPositions) {
        List<HexCoordinate> candidates = new ArrayList<>();
        
        // Buscar nodos de recursos o estratégicos no ocupados
        for (HexCoordinate resource : map.getResourceNodes()) {
            if (!occupiedPositions.contains(resource)) {
                candidates.add(resource);
            }
        }
        
        for (HexCoordinate strategic : map.getStrategicNodes()) {
            if (!occupiedPositions.contains(strategic)) {
                candidates.add(strategic);
            }
        }
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    /**
     * Crea un evento del tipo especificado.
     * Implementa DRY: método factory para eventos.
     */
    private RandomEvent createEvent(RandomEvent.EventType type, HexCoordinate location) {
        String id = "event_" + (eventIdCounter++);
        
        switch (type) {
            case CHEST:
                return createChestEvent(id, location);
            case TRAP:
                return createTrapEvent(id, location);
            case ENCOUNTER:
                return createEncounterEvent(id, location);
            case SHRINE:
                return createShrineEvent(id, location);
            case MERCHANT:
                return createMerchantEvent(id, location);
            default:
                return null;
        }
    }
    
    private RandomEvent createChestEvent(String id, HexCoordinate location) {
        List<RandomEvent.EventOption> options = Arrays.asList(
            new RandomEvent.EventOption(0, "Abrir cuidadosamente", 
                "Abre el cofre con precaución", 0.8),
            new RandomEvent.EventOption(1, "Forzar la cerradura", 
                "Intenta forzar el cofre", 0.6),
            new RandomEvent.EventOption(2, "Ignorar", 
                "Dejar el cofre cerrado", 1.0)
        );
        
        return new RandomEvent(id, "Cofre Misterioso", 
            "Encuentras un cofre antiguo. ¿Qué haces?",
            RandomEvent.EventType.CHEST, options, location);
    }
    
    private RandomEvent createTrapEvent(String id, HexCoordinate location) {
        List<RandomEvent.EventOption> options = Arrays.asList(
            new RandomEvent.EventOption(0, "Desactivar", 
                "Intenta desactivar la trampa", 0.7),
            new RandomEvent.EventOption(1, "Saltar sobre ella", 
                "Intenta esquivar la trampa", 0.5),
            new RandomEvent.EventOption(2, "Activarla a distancia", 
                "Activa la trampa desde lejos", 1.0)
        );
        
        return new RandomEvent(id, "¡Trampa!", 
            "Detectas una trampa en el suelo. ¿Cómo procedes?",
            RandomEvent.EventType.TRAP, options, location);
    }
    
    private RandomEvent createEncounterEvent(String id, HexCoordinate location) {
        String[] encounters = {
            "Un viajero perdido te pide ayuda",
            "Encuentras inscripciones antiguas en la pared",
            "Un animal herido yace en el camino",
            "Escuchas voces extrañas en la distancia"
        };
        
        String description = encounters[random.nextInt(encounters.length)];
        
        List<RandomEvent.EventOption> options = Arrays.asList(
            new RandomEvent.EventOption(0, "Ayudar", "Ofreces tu ayuda", 0.8),
            new RandomEvent.EventOption(1, "Investigar", "Investigas más a fondo", 0.6),
            new RandomEvent.EventOption(2, "Continuar", "Sigues tu camino", 1.0)
        );
        
        return new RandomEvent(id, "Encuentro Extraño", description,
            RandomEvent.EventType.ENCOUNTER, options, location);
    }
    
    private RandomEvent createShrineEvent(String id, HexCoordinate location) {
        List<RandomEvent.EventOption> options = Arrays.asList(
            new RandomEvent.EventOption(0, "Orar", 
                "Rezas en el santuario (+buff temporal)", 1.0),
            new RandomEvent.EventOption(1, "Dejar ofrenda", 
                "Ofreces un objeto (posible mejora)", 0.7),
            new RandomEvent.EventOption(2, "Seguir adelante", 
                "Ignoras el santuario", 1.0)
        );
        
        return new RandomEvent(id, "Santuario Antiguo", 
            "Descubres un santuario sagrado. ¿Qué haces?",
            RandomEvent.EventType.SHRINE, options, location);
    }
    
    private RandomEvent createMerchantEvent(String id, HexCoordinate location) {
        List<RandomEvent.EventOption> options = Arrays.asList(
            new RandomEvent.EventOption(0, "Comprar", 
                "Mira los objetos disponibles", 1.0),
            new RandomEvent.EventOption(1, "Vender", 
                "Ofrece tus objetos", 1.0),
            new RandomEvent.EventOption(2, "Marcharse", 
                "Declinas comerciar", 1.0)
        );
        
        return new RandomEvent(id, "Mercader Ambulante", 
            "Un mercader te ofrece sus mercancías.",
            RandomEvent.EventType.MERCHANT, options, location);
    }
    
    /**
     * Resuelve la interacción de un jugador con un evento.
     * Implementa KISS: lógica simple de resolución por tipo.
     */
    public EventResult resolveEvent(String eventId, int optionIndex) {
        RandomEvent event = activeEvents.get(eventId);
        if (event == null) {
            return EventResult.failure("Evento no encontrado");
        }
        
        if (optionIndex < 0 || optionIndex >= event.getOptions().size()) {
            return EventResult.failure("Opción inválida");
        }
        
        RandomEvent.EventOption option = event.getOptions().get(optionIndex);
        boolean success = random.nextDouble() < option.getSuccessChance();
        
        // Resolver según tipo de evento
        EventResult result = resolveByType(event, option, success);
        
        // Remover evento activo
        activeEvents.remove(eventId);
        
        logger.info("Event {} resolved: option={}, success={}", 
            event.getTitle(), option.getText(), success);
        
        return result;
    }
    
    private EventResult resolveByType(RandomEvent event, RandomEvent.EventOption option, 
                                     boolean success) {
        switch (event.getType()) {
            case CHEST:
                return resolveChest(option, success);
            case TRAP:
                return resolveTrap(option, success);
            case ENCOUNTER:
                return resolveEncounter(option, success);
            case SHRINE:
                return resolveShrine(option, success);
            case MERCHANT:
                return resolveMerchant(option, success);
            default:
                return EventResult.failure("Tipo de evento desconocido");
        }
    }
    
    private EventResult resolveChest(RandomEvent.EventOption option, boolean success) {
        if (option.getIndex() == 2) { // Ignorar
            return EventResult.success("Decides no abrir el cofre.", null, 0, null);
        }
        
        if (success) {
            // Generar loot
            List<Item> loot = lootSystem.generateLoot(
                LootSystem.LootSource.CHEST, random.nextInt(3) + 1);
            return EventResult.success("¡Abriste el cofre exitosamente!", loot, 0, null);
        } else {
            return EventResult.success("El cofre estaba vacío o se activó una trampa.", 
                null, -10, null);
        }
    }
    
    private EventResult resolveTrap(RandomEvent.EventOption option, boolean success) {
        if (option.getIndex() == 2) { // Activar a distancia
            return EventResult.success("Activaste la trampa de forma segura.", null, 0, null);
        }
        
        if (success) {
            return EventResult.success("¡Evitaste la trampa exitosamente!", null, 0, null);
        } else {
            return EventResult.success("La trampa se activó y te causó daño.", 
                null, -15, null);
        }
    }
    
    private EventResult resolveEncounter(RandomEvent.EventOption option, boolean success) {
        if (success) {
            // Pequeña recompensa por ayudar/investigar
            List<Item> reward = lootSystem.generateLoot(
                LootSystem.LootSource.EVENT, 1);
            return EventResult.success("Tu acción fue recompensada.", reward, 0, null);
        } else {
            return EventResult.success("Nada interesante sucedió.", null, 0, null);
        }
    }
    
    private EventResult resolveShrine(RandomEvent.EventOption option, boolean success) {
        if (option.getIndex() == 2) { // Seguir adelante
            return EventResult.success("Continúas tu camino.", null, 0, null);
        }
        
        if (option.getIndex() == 0) { // Orar
            return EventResult.success("Recibes una bendición temporal.", 
                null, 0, "BLESSING:+2_DAMAGE:3_TURNS");
        }
        
        if (success) {
            return EventResult.success("Tu ofrenda fue aceptada. Te sientes fortalecido.", 
                null, 0, "BLESSING:+3_DEFENSE:5_TURNS");
        }
        
        return EventResult.success("Tu ofrenda fue en vano.", null, 0, null);
    }
    
    private EventResult resolveMerchant(RandomEvent.EventOption option, boolean success) {
        // Simplificado: solo mensaje por ahora
        return EventResult.success("Comerciaste con el mercader.", null, 0, null);
    }
    
    public RandomEvent getEvent(String eventId) {
        return activeEvents.get(eventId);
    }
    
    /**
     * Resultado de resolver un evento.
     */
    public static class EventResult {
        private final boolean success;
        private final String message;
        private final List<Item> items;
        private final int hpChange;
        private final String effect;
        
        private EventResult(boolean success, String message, List<Item> items,
                          int hpChange, String effect) {
            this.success = success;
            this.message = message;
            this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
            this.hpChange = hpChange;
            this.effect = effect;
        }
        
        public static EventResult success(String message, List<Item> items,
                                         int hpChange, String effect) {
            return new EventResult(true, message, items, hpChange, effect);
        }
        
        public static EventResult failure(String message) {
            return new EventResult(false, message, null, 0, null);
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<Item> getItems() { return new ArrayList<>(items); }
        public int getHpChange() { return hpChange; }
        public String getEffect() { return effect; }
    }
    
    /**
     * Obtiene un evento activo por su ID.
     */
    public RandomEvent getActiveEvent(String eventId) {
        return activeEvents.get(eventId);
    }
    
    /**
     * Remueve un evento activo.
     */
    public void removeActiveEvent(String eventId) {
        activeEvents.remove(eventId);
        logger.debug("Removed event {}", eventId);
    }
    
    /**
     * Obtiene todos los eventos activos.
     */
    public Collection<RandomEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents.values());
    }
}
