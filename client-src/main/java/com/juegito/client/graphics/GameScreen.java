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
import com.juegito.client.state.PlayerLocalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pantalla principal del juego (durante la partida).
 * Renderiza el mapa hexagonal, jugadores y HUD.
 * Gestiona el ciclo de vida: inicialización, render, resize y dispose.
 * Implementa KISS: Delega responsabilidades a renderers especializados.
 */
public class GameScreen extends ScreenAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GameScreen.class);
    
    private final ClientGameState gameState;
    private final PlayerLocalState playerState;
    private final ActionExecutor actionExecutor;
    
    // Componentes de renderizado
    private OrthographicCamera camera;
    private CameraController cameraController;
    
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private SimpleAssetManager assetManager;
    
    // Renderers especializados
    private HexMapRenderer mapRenderer;
    private PlayerRenderer playerRenderer;
    private HUD hud;
    private GameInputProcessor inputProcessor;
    
    public GameScreen(ClientGameState gameState, PlayerLocalState playerState, ActionExecutor actionExecutor) {
        this.gameState = gameState;
        this.playerState = playerState;
        this.actionExecutor = actionExecutor;
    }
    
    @Override
    public void show() {
        logger.info("Initializing game screen");
        
        initializeAssets();
        initializeCamera();
        initializeRenderers();
        initializeInput();
        
        logger.info("Game screen initialized successfully");
    }
    
    /**
     * Inicializa assets y recursos gráficos.
     * DRY: Separado en método para mejor organización.
     */
    private void initializeAssets() {
        assetManager = new SimpleAssetManager();
        assetManager.load();
        
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = assetManager.getDefaultFont();
    }
    
    /**
     * Inicializa la cámara y su controlador.
     * DRY: Configuración centralizada de la cámara.
     */
    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraController = new CameraController(camera);
    }
    
    /**
     * Inicializa los renderers especializados.
     * DRY: Creación de renderers en un solo lugar.
     */
    private void initializeRenderers() {
        mapRenderer = new HexMapRenderer(gameState, shapeRenderer, camera);
        playerRenderer = new PlayerRenderer(gameState, playerState, shapeRenderer, spriteBatch, font, camera);
        hud = new HUD(gameState, spriteBatch, font);
    }
    
    /**
     * Configura procesamiento de inputs.
     * DRY: Separado para claridad.
     */
    private void initializeInput() {
        inputProcessor = new GameInputProcessor(gameState, camera, cameraController, mapRenderer);
        inputProcessor.setActionExecutor(actionExecutor);
        Gdx.input.setInputProcessor(inputProcessor);
    }
    
    @Override
    public void render(float delta) {
        // Limpiar pantalla con color de fondo
        Gdx.gl.glClearColor(
            GraphicsConstants.BACKGROUND_COLOR.r,
            GraphicsConstants.BACKGROUND_COLOR.g,
            GraphicsConstants.BACKGROUND_COLOR.b,
            GraphicsConstants.BACKGROUND_COLOR.a
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar sistemas
        updateSystems(delta);
        
        // Renderizar escena
        renderScene();
    }
    
    /**
     * Actualiza sistemas del juego.
     * DRY: Lógica de actualización separada.
     */
    private void updateSystems(float delta) {
        cameraController.update(delta);
        camera.update();
    }
    
    /**
     * Renderiza todos los componentes de la escena.
     * DRY: Orden de renderizado centralizado.
     */
    private void renderScene() {
        // Configurar matrices de proyección para mundo
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        
        // Renderizar en orden: mapa -> entidades -> UI
        mapRenderer.render();
        playerRenderer.render();
        hud.render(); // HUD usa su propia cámara
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
        
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        if (assetManager != null) assetManager.dispose();
        
        logger.info("Game screen disposed");
    }
    
    public HUD getHud() {
        return hud;
    }
}
