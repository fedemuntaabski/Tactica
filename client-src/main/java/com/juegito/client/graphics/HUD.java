package com.juegito.client.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.juegito.protocol.dto.PlayerInfoDTO;
import com.juegito.client.state.ClientGameState;

import java.util.ArrayList;
import java.util.List;

import static com.juegito.client.graphics.GraphicsConstants.*;

/**
 * HUD (Heads-Up Display) que muestra información de UI sobre el juego.
 * Muestra: turno actual, lista de jugadores conectados, log de acciones.
 * NO incluye timer según requerimientos.
 */
public class HUD {
    private final ClientGameState gameState;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    
    private OrthographicCamera hudCamera;
    private int screenWidth;
    private int screenHeight;
    
    // Usa constantes de GraphicsConstants
    
    private final List<String> actionLog = new ArrayList<>();
    private static final int MAX_LOG_ENTRIES = 10;
    
    public HUD(ClientGameState gameState, SpriteBatch spriteBatch, BitmapFont font) {
        this.gameState = gameState;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
        
        // Cámara para HUD (coordenadas de pantalla)
        this.hudCamera = new OrthographicCamera();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    
    /**
     * Renderiza todos los elementos del HUD.
     */
    public void render() {
        // Usar proyección de pantalla, no de cámara del mundo
        hudCamera.update();
        spriteBatch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        
        renderTurnInfo();
        renderPlayerList();
        renderActionLog();
    }
    
    /**
     * Renderiza información del turno actual.
     */
    private void renderTurnInfo() {
        if (gameState.getCurrentPhase() != ClientGameState.GamePhase.PLAYING) {
            return;
        }
        
        float panelWidth = 300f;
        float panelHeight = 80f;
        float x = screenWidth / 2f - panelWidth / 2f;
        float y = screenHeight - panelHeight - PANEL_PADDING;
        
        // Fondo del panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PANEL_BG);
        shapeRenderer.rect(x, y, panelWidth, panelHeight);
        shapeRenderer.end();
        
        // Texto
        spriteBatch.begin();
        
        String turnText = "TURNO " + gameState.getTurnNumber();
        font.setColor(TEXT_COLOR);
        font.draw(spriteBatch, turnText, x + PANEL_PADDING, y + panelHeight - PANEL_PADDING);
        
        if (gameState.isMyTurn()) {
            font.setColor(HIGHLIGHT_COLOR);
            font.draw(spriteBatch, ">>> ES TU TURNO <<<", x + PANEL_PADDING, y + panelHeight - PANEL_PADDING - LINE_HEIGHT);
        } else {
            font.setColor(TEXT_COLOR);
            font.draw(spriteBatch, "Esperando turno...", x + PANEL_PADDING, y + panelHeight - PANEL_PADDING - LINE_HEIGHT);
        }
        
        spriteBatch.end();
    }
    
    /**
     * Renderiza lista de jugadores conectados.
     */
    private void renderPlayerList() {
        List<PlayerInfoDTO> players = gameState.getLobbyPlayers();
        if (players.isEmpty()) {
            return;
        }
        
        float panelWidth = 200f;
        float panelHeight = PANEL_PADDING * 2 + LINE_HEIGHT * (players.size() + 1);
        float x = PANEL_PADDING;
        float y = screenHeight - panelHeight - PANEL_PADDING;
        
        // Fondo del panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PANEL_BG);
        shapeRenderer.rect(x, y, panelWidth, panelHeight);
        shapeRenderer.end();
        
        // Texto
        spriteBatch.begin();
        
        font.setColor(TEXT_COLOR);
        font.draw(spriteBatch, "JUGADORES", x + PANEL_PADDING, y + panelHeight - PANEL_PADDING);
        
        float currentY = y + panelHeight - PANEL_PADDING - LINE_HEIGHT;
        for (PlayerInfoDTO player : players) {
            boolean isMe = player.getPlayerId().equals(gameState.getPlayerId());
            boolean isActive = player.getPlayerId().equals(gameState.getCurrentTurnPlayerId());
            
            if (isMe) {
                font.setColor(HIGHLIGHT_COLOR);
            } else if (isActive) {
                font.setColor(Color.GREEN);
            } else {
                font.setColor(TEXT_COLOR);
            }
            
            String playerText = player.getPlayerName() + (isMe ? " (TÚ)" : "");
            if (isActive) {
                playerText = "> " + playerText;
            }
            
            font.draw(spriteBatch, playerText, x + PANEL_PADDING, currentY);
            currentY -= LINE_HEIGHT;
        }
        
        spriteBatch.end();
    }
    
    /**
     * Renderiza log de acciones recientes.
     */
    private void renderActionLog() {
        if (actionLog.isEmpty()) {
            return;
        }
        
        float panelWidth = 400f;
        float panelHeight = PANEL_PADDING * 2 + LINE_HEIGHT * Math.min(actionLog.size() + 1, MAX_LOG_ENTRIES + 1);
        float x = screenWidth - panelWidth - PANEL_PADDING;
        float y = PANEL_PADDING;
        
        // Fondo del panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PANEL_BG);
        shapeRenderer.rect(x, y, panelWidth, panelHeight);
        shapeRenderer.end();
        
        // Texto
        spriteBatch.begin();
        
        font.setColor(TEXT_COLOR);
        font.draw(spriteBatch, "LOG DE ACCIONES", x + PANEL_PADDING, y + panelHeight - PANEL_PADDING);
        
        float currentY = y + panelHeight - PANEL_PADDING - LINE_HEIGHT;
        int entriesToShow = Math.min(actionLog.size(), MAX_LOG_ENTRIES);
        for (int i = actionLog.size() - entriesToShow; i < actionLog.size(); i++) {
            font.setColor(Color.LIGHT_GRAY);
            font.draw(spriteBatch, actionLog.get(i), x + PANEL_PADDING, currentY);
            currentY -= LINE_HEIGHT;
        }
        
        spriteBatch.end();
    }
    
    /**
     * Agrega una entrada al log de acciones.
     */
    public void addLogEntry(String entry) {
        actionLog.add(entry);
        
        // Limitar tamaño del log
        while (actionLog.size() > MAX_LOG_ENTRIES * 2) {
            actionLog.remove(0);
        }
    }
    
    /**
     * Actualiza dimensiones del HUD cuando cambia el tamaño de ventana.
     */
    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        hudCamera.setToOrtho(false, width, height);
    }
    
    /**
     * Limpia el log de acciones.
     */
    public void clearLog() {
        actionLog.clear();
    }
}
