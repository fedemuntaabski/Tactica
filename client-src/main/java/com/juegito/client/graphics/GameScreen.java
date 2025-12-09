package com.juegito.client.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.juegito.client.game.ActionExecutor;
import com.juegito.client.state.ClientGameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pantalla principal del juego (durante la partida).
 * Renderiza el mapa hexagonal, jugadores y HUD.
 */
public class GameScreen extends ScreenAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GameScreen.class);
    
    private final ClientGameState gameState;
    private final ActionExecutor actionExecutor;
    
    private OrthographicCamera camera;
    private CameraController cameraController;
    
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    
    private HexMapRenderer mapRenderer;
    private PlayerRenderer playerRenderer;
    private HUD hud;
    private GameInputProcessor inputProcessor;
    
    public GameScreen(ClientGameState gameState, ActionExecutor actionExecutor) {
        this.gameState = gameState;
        this.actionExecutor = actionExecutor;
    }
    
    @Override
    public void show() {
        logger.info("Initializing game screen");
        
        // Configurar cámara 2D
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraController = new CameraController(camera);
        
        // Inicializar renderizadores
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        // Crear componentes gráficos
        mapRenderer = new HexMapRenderer(gameState, shapeRenderer, camera);
        playerRenderer = new PlayerRenderer(gameState, shapeRenderer, spriteBatch, font, camera);
        hud = new HUD(gameState, spriteBatch, font);
        
        // Configurar input processor
        inputProcessor = new GameInputProcessor(gameState, camera, cameraController, mapRenderer);
        inputProcessor.setActionExecutor(actionExecutor);
        Gdx.input.setInputProcessor(inputProcessor);
        
        logger.info("Game screen initialized");
    }
    
    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar cámara
        cameraController.update(delta);
        camera.update();
        
        // Renderizar componentes
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        
        // Renderizar mapa
        mapRenderer.render();
        
        // Renderizar jugadores
        playerRenderer.render();
        
        // Renderizar HUD (usa proyección de pantalla, no de cámara)
        hud.render();
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        hud.resize(width, height);
    }
    
    @Override
    public void dispose() {
        logger.info("Disposing game screen resources");
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
    
    public HUD getHud() {
        return hud;
    }
}
