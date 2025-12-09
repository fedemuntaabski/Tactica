package com.juegito.client.graphics;

import com.badlogic.gdx.Game;
import com.juegito.client.game.ActionExecutor;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.LobbyClientState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aplicación principal de LibGDX.
 * Gestiona las pantallas del juego (lobby y partida).
 */
public class GameApplication extends Game {
    private static final Logger logger = LoggerFactory.getLogger(GameApplication.class);
    
    private final ClientGameState gameState;
    private final LobbyClientState lobbyState;
    private final ConnectionManager connectionManager;
    private ActionExecutor actionExecutor;
    
    private LobbyScreen lobbyScreen;
    private GameScreen gameScreen;
    
    public GameApplication(ClientGameState gameState, LobbyClientState lobbyState, ConnectionManager connectionManager) {
        this.gameState = gameState;
        this.lobbyState = lobbyState;
        this.connectionManager = connectionManager;
    }
    
    /**
     * Inyecta dependencias que se crean después del constructor.
     */
    public void setActionExecutor(ActionExecutor actionExecutor) {
        this.actionExecutor = actionExecutor;
    }
    
    @Override
    public void create() {
        logger.info("Initializing LibGDX GameApplication");
        
        // Crear pantalla del lobby
        lobbyScreen = new LobbyScreen(lobbyState, connectionManager, this::transitionToGame);
        
        // Iniciar en la pantalla del lobby
        setScreen(lobbyScreen);
        
        logger.info("LibGDX initialization complete - Starting in lobby");
    }
    
    /**
     * Transiciona del lobby al juego.
     */
    private void transitionToGame() {
        logger.info("Transitioning from lobby to game");
        
        // Crear pantalla del juego si no existe
        if (gameScreen == null) {
            gameScreen = new GameScreen(gameState, actionExecutor);
        }
        
        // Cambiar a la pantalla del juego
        setScreen(gameScreen);
    }
    
    @Override
    public void dispose() {
        logger.info("Disposing LibGDX resources");
        if (lobbyScreen != null) {
            lobbyScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }
}
