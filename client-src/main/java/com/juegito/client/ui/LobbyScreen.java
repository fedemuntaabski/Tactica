package com.juegito.client.ui;

import com.juegito.client.protocol.dto.PlayerInfoDTO;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.ServerUpdateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Pantalla de lobby que muestra jugadores conectados.
 * Permite marcar al jugador como ready y esperar inicio del juego.
 */
public class LobbyScreen implements ServerUpdateProcessor.StateChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(LobbyScreen.class);
    
    private final ClientGameState gameState;
    private final LobbyUIListener uiListener;
    
    public LobbyScreen(ClientGameState gameState, LobbyUIListener uiListener) {
        this.gameState = gameState;
        this.uiListener = uiListener;
    }
    
    /**
     * Renderiza la pantalla del lobby.
     */
    public void render() {
        if (gameState.getCurrentPhase() != ClientGameState.GamePhase.LOBBY) {
            return;
        }
        
        displayLobbyHeader();
        displayPlayerList();
        displayControls();
    }
    
    private void displayLobbyHeader() {
        StringBuilder header = new StringBuilder();
        header.append("\n╔════════════════════════════════════════╗\n");
        header.append("║           LOBBY - JUEGITO              ║\n");
        header.append("╚════════════════════════════════════════╝\n");
        
        int playerCount = gameState.getLobbyPlayers().size();
        int maxPlayers = gameState.getMaxPlayers();
        header.append(String.format("Jugadores: %d/%d\n", playerCount, maxPlayers));
        
        if (uiListener != null) {
            uiListener.onDisplayText(header.toString());
        } else {
            System.out.println(header);
        }
    }
    
    private void displayPlayerList() {
        List<PlayerInfoDTO> players = gameState.getLobbyPlayers();
        
        StringBuilder playerList = new StringBuilder();
        playerList.append("\n┌─ Jugadores ─────────────────────────┐\n");
        
        for (PlayerInfoDTO player : players) {
            String readyStatus = player.isReady() ? "✓ LISTO" : "  Esperando...";
            String isYou = player.getPlayerId().equals(gameState.getPlayerId()) ? " (TÚ)" : "";
            
            playerList.append(String.format("│ %s %s%s\n", 
                readyStatus, 
                player.getPlayerName(),
                isYou));
        }
        
        playerList.append("└─────────────────────────────────────┘\n");
        
        if (uiListener != null) {
            uiListener.onDisplayText(playerList.toString());
        } else {
            System.out.println(playerList);
        }
    }
    
    private void displayControls() {
        StringBuilder controls = new StringBuilder();
        controls.append("\nComandos:\n");
        
        if (!gameState.isReady()) {
            controls.append("  [R] - Marcar como listo\n");
        } else {
            controls.append("  [U] - Cancelar listo\n");
        }
        
        controls.append("  [Q] - Salir del lobby\n");
        controls.append("\n");
        
        if (uiListener != null) {
            uiListener.onDisplayText(controls.toString());
        } else {
            System.out.println(controls);
        }
    }
    
    /**
     * Maneja la acción de marcar como ready.
     */
    public void toggleReady() {
        boolean newReadyState = !gameState.isReady();
        gameState.setReady(newReadyState);
        
        logger.info("Ready state changed to: {}", newReadyState);
        
        if (uiListener != null) {
            uiListener.onReadyStateChanged(newReadyState);
        }
        
        render();
    }
    
    @Override
    public void onStateChange(ServerUpdateProcessor.StateChangeType type, Object data) {
        switch (type) {
            case LOBBY_UPDATED:
                render();
                break;
                
            case GAME_STARTING:
                displayGameStarting();
                break;
                
            case PLAYER_DISCONNECTED:
                logger.info("A player has disconnected");
                render();
                break;
                
            default:
                // Ignorar otros eventos
                break;
        }
    }
    
    private void displayGameStarting() {
        String message = "\n" +
            "╔════════════════════════════════════════╗\n" +
            "║     ¡EL JUEGO ESTÁ COMENZANDO!        ║\n" +
            "╚════════════════════════════════════════╝\n";
        
        if (uiListener != null) {
            uiListener.onDisplayText(message);
            uiListener.onGameStarting();
        } else {
            System.out.println(message);
        }
    }
    
    /**
     * Interfaz para callbacks de la UI del lobby.
     */
    public interface LobbyUIListener {
        void onDisplayText(String text);
        void onReadyStateChanged(boolean ready);
        void onGameStarting();
    }
}
