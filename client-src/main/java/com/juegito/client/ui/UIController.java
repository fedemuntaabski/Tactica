package com.juegito.client.ui;

import com.juegito.client.game.ActionExecutor;
import com.juegito.client.game.TurnManager;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.ServerUpdateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador principal de la interfaz de usuario.
 * Coordina las diferentes pantallas y maneja la transición entre ellas.
 */
public class UIController implements ServerUpdateProcessor.StateChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(UIController.class);
    
    private final ClientGameState gameState;
    private final ConnectionManager connectionManager;
    private final TurnManager turnManager;
    
    private LobbyScreen lobbyScreen;
    private MapRenderer mapRenderer;
    
    private Screen currentScreen;
    
    public UIController(ClientGameState gameState, 
                       ConnectionManager connectionManager,
                       TurnManager turnManager) {
        this.gameState = gameState;
        this.connectionManager = connectionManager;
        this.turnManager = turnManager;
        this.currentScreen = Screen.NONE;
        
        initializeScreens();
    }
    
    private void initializeScreens() {
        // Inicializar lobby screen
        lobbyScreen = new LobbyScreen(gameState, new LobbyScreen.LobbyUIListener() {
            @Override
            public void onDisplayText(String text) {
                System.out.print(text);
            }
            
            @Override
            public void onReadyStateChanged(boolean ready) {
                logger.info("Ready state changed: {}", ready);
                // Aquí se podría enviar un mensaje al servidor
            }
            
            @Override
            public void onGameStarting() {
                switchScreen(Screen.GAME);
            }
        });
        
        // Inicializar map renderer
        mapRenderer = new MapRenderer(gameState, new MapRenderer.RenderListener() {
            @Override
            public void onRenderText(String text) {
                System.out.print(text);
            }
            
            @Override
            public void onActionRendered(String playerId, String actionType) {
                logger.debug("Action rendered: {} by {}", actionType, playerId);
            }
            
            @Override
            public void onCombatResultRendered(MapRenderer.CombatResult result) {
                logger.info("Combat result rendered: {}", result.getOutcome());
            }
        });
    }
    
    /**
     * Cambia a una pantalla diferente.
     */
    public void switchScreen(Screen screen) {
        logger.info("Switching to screen: {}", screen);
        currentScreen = screen;
        render();
    }
    
    /**
     * Renderiza la pantalla actual.
     */
    public void render() {
        switch (currentScreen) {
            case LOBBY:
                lobbyScreen.render();
                break;
                
            case GAME:
                mapRenderer.render();
                mapRenderer.renderControls();
                break;
                
            case NONE:
            default:
                break;
        }
    }
    
    @Override
    public void onStateChange(ServerUpdateProcessor.StateChangeType type, Object data) {
        switch (type) {
            case PLAYER_CONNECTED:
                switchScreen(Screen.LOBBY);
                break;
                
            case LOBBY_UPDATED:
                if (currentScreen == Screen.LOBBY) {
                    render();
                }
                break;
                
            case GAME_STARTING:
                switchScreen(Screen.GAME);
                break;
                
            case GAME_STATE_UPDATED:
                if (currentScreen == Screen.GAME) {
                    render();
                }
                break;
                
            case TURN_STARTED:
                if (currentScreen == Screen.GAME) {
                    render();
                }
                break;
                
            case ACTION_ACCEPTED:
            case ACTION_REJECTED:
                if (currentScreen == Screen.GAME) {
                    render();
                }
                break;
                
            default:
                break;
        }
    }
    
    public LobbyScreen getLobbyScreen() {
        return lobbyScreen;
    }
    
    public MapRenderer getMapRenderer() {
        return mapRenderer;
    }
    
    public Screen getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Pantallas disponibles en la UI.
     */
    public enum Screen {
        NONE,
        LOBBY,
        GAME
    }
}
