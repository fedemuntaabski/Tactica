package com.juegito.client.ui;

import com.juegito.client.state.ClientGameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Renderiza el mapa/mundo del juego.
 * Clase base preparada para mostrar el estado visual del juego.
 */
public class MapRenderer {
    private static final Logger logger = LoggerFactory.getLogger(MapRenderer.class);
    
    private final ClientGameState gameState;
    private final RenderListener renderListener;
    
    public MapRenderer(ClientGameState gameState, RenderListener renderListener) {
        this.gameState = gameState;
        this.renderListener = renderListener;
    }
    
    /**
     * Renderiza el estado actual del mundo.
     */
    public void render() {
        if (gameState.getCurrentPhase() != ClientGameState.GamePhase.PLAYING) {
            return;
        }
        
        renderTurnInfo();
        renderWorldState();
        renderPlayerInfo();
    }
    
    private void renderTurnInfo() {
        StringBuilder turnInfo = new StringBuilder();
        turnInfo.append("\n╔════════════════════════════════════════╗\n");
        turnInfo.append(String.format("║  TURNO %d                              ║\n", 
            gameState.getTurnNumber()));
        
        if (gameState.isMyTurn()) {
            turnInfo.append("║  >>> ES TU TURNO <<<                  ║\n");
        } else {
            turnInfo.append("║  Esperando turno del oponente...      ║\n");
        }
        
        turnInfo.append("╚════════════════════════════════════════╝\n");
        
        displayText(turnInfo.toString());
    }
    
    private void renderWorldState() {
        Map<String, Object> worldState = gameState.getWorldState();
        
        if (worldState.isEmpty()) {
            displayText("\n[Mundo vacío - esperando datos del servidor]\n");
            return;
        }
        
        StringBuilder world = new StringBuilder();
        world.append("\n┌─ Estado del Mundo ──────────────────┐\n");
        
        // Renderizar elementos del mundo
        // Esta es una implementación base - se puede extender según las necesidades del juego
        for (Map.Entry<String, Object> entry : worldState.entrySet()) {
            world.append(String.format("│ %s: %s\n", entry.getKey(), entry.getValue()));
        }
        
        world.append("└─────────────────────────────────────┘\n");
        
        displayText(world.toString());
    }
    
    private void renderPlayerInfo() {
        StringBuilder info = new StringBuilder();
        info.append("\n┌─ Tu Información ────────────────────┐\n");
        info.append(String.format("│ Jugador: %s\n", gameState.getPlayerName()));
        info.append(String.format("│ ID: %s\n", gameState.getPlayerId()));
        info.append("└─────────────────────────────────────┘\n");
        
        displayText(info.toString());
    }
    
    /**
     * Renderiza una acción ejecutada por un jugador.
     */
    public void renderAction(String playerId, String actionType, Object actionData) {
        String playerName = playerId.equals(gameState.getPlayerId()) 
            ? "Tú" 
            : playerId;
        
        String message = String.format("\n>>> %s ejecutó: %s\n", playerName, actionType);
        displayText(message);
        
        if (renderListener != null) {
            renderListener.onActionRendered(playerId, actionType);
        }
    }
    
    /**
     * Renderiza el resultado de un combate.
     */
    public void renderCombatResult(CombatResult result) {
        StringBuilder combat = new StringBuilder();
        combat.append("\n╔═══ RESULTADO DE COMBATE ════════════╗\n");
        combat.append(String.format("║ Atacante: %s\n", result.attacker));
        combat.append(String.format("║ Defensor: %s\n", result.defender));
        combat.append(String.format("║ Resultado: %s\n", result.outcome));
        combat.append("╚═════════════════════════════════════╝\n");
        
        displayText(combat.toString());
        
        if (renderListener != null) {
            renderListener.onCombatResultRendered(result);
        }
    }
    
    /**
     * Renderiza cambios en el mapa.
     */
    public void renderMapChanges(String changeDescription) {
        String message = String.format("\n[Cambio en el mapa: %s]\n", changeDescription);
        displayText(message);
    }
    
    /**
     * Muestra los controles disponibles.
     */
    public void renderControls() {
        StringBuilder controls = new StringBuilder();
        controls.append("\n┌─ Controles ─────────────────────────┐\n");
        
        if (gameState.isMyTurn()) {
            controls.append("│ [M] - Mover\n");
            controls.append("│ [A] - Atacar\n");
            controls.append("│ [D] - Defender\n");
            controls.append("│ [S] - Saltar turno\n");
        } else {
            controls.append("│ Esperando tu turno...\n");
        }
        
        controls.append("│ [Q] - Salir del juego\n");
        controls.append("└─────────────────────────────────────┘\n");
        
        displayText(controls.toString());
    }
    
    private void displayText(String text) {
        if (renderListener != null) {
            renderListener.onRenderText(text);
        } else {
            System.out.print(text);
        }
    }
    
    /**
     * Interfaz para callbacks de renderizado.
     */
    public interface RenderListener {
        void onRenderText(String text);
        void onActionRendered(String playerId, String actionType);
        void onCombatResultRendered(CombatResult result);
    }
    
    /**
     * Clase para encapsular resultados de combate.
     */
    public static class CombatResult {
        private final String attacker;
        private final String defender;
        private final String outcome;
        private final Map<String, Object> details;
        
        public CombatResult(String attacker, String defender, String outcome, Map<String, Object> details) {
            this.attacker = attacker;
            this.defender = defender;
            this.outcome = outcome;
            this.details = details;
        }
        
        public String getAttacker() {
            return attacker;
        }
        
        public String getDefender() {
            return defender;
        }
        
        public String getOutcome() {
            return outcome;
        }
        
        public Map<String, Object> getDetails() {
            return details;
        }
    }
}
