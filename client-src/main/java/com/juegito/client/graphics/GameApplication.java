package com.juegito.client.graphics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.juegito.client.game.ActionExecutor;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.ClientGameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aplicación principal de LibGDX.
 * Maneja el ciclo de vida de renderizado y coordina todos los componentes gráficos.
 */
public class GameApplication extends ApplicationAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GameApplication.class);
    
    private final ClientGameState gameState;
    private ActionExecutor actionExecutor;
    private ConnectionManager connectionManager;
    
    private OrthographicCamera camera;
    private CameraController cameraController;
    
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    
    private HexMapRenderer mapRenderer;
    private PlayerRenderer playerRenderer;
    private HUD hud;
    private GameInputProcessor inputProcessor;
    
    public GameApplication(ClientGameState gameState) {
        this.gameState = gameState;
    }
    
    /**
     * Inyecta dependencias que se crean después del constructor.
     */
    public void setDependencies(ActionExecutor actionExecutor, ConnectionManager connectionManager) {
        this.actionExecutor = actionExecutor;
        this.connectionManager = connectionManager;
        
        if (inputProcessor != null) {
            inputProcessor.setActionExecutor(actionExecutor);
        }
    }
    
    @Override
    public void create() {
        logger.info("Initializing LibGDX GameApplication");
        
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
        Gdx.input.setInputProcessor(inputProcessor);
        
        logger.info("LibGDX initialization complete");
    }
    
    @Override
    public void render() {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar cámara
        float delta = Gdx.graphics.getDeltaTime();
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
        logger.info("Disposing LibGDX resources");
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
    }
    
    public HUD getHud() {
        return hud;
    }
}
