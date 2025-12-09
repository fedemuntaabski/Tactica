package com.juegito.client.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.juegito.protocol.dto.GameMapDTO;
import com.juegito.protocol.dto.HexCoordinateDTO;
import com.juegito.client.state.ClientGameState;
import com.juegito.client.state.PlayerLocalState;

import java.util.Map;

import static com.juegito.client.graphics.GraphicsConstants.*;

/**
 * Renderiza jugadores en el mapa.
 * Muestra sprites simples (círculos), barras de HP e ID del jugador.
 */
public class PlayerRenderer {
    private final ClientGameState gameState;
    private final PlayerLocalState playerState;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final HexMapRenderer hexMapRenderer;
    
    // Usa constantes de GraphicsConstants para evitar duplicación
    private static final Color[] PLAYER_COLORS = {
        PLAYER_COLOR_2,  // Azul
        PLAYER_COLOR_1,  // Rojo
        PLAYER_COLOR_3,  // Verde
        PLAYER_COLOR_4   // Amarillo
    };
    
    public PlayerRenderer(ClientGameState gameState, PlayerLocalState playerState,
                         ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, 
                         BitmapFont font, OrthographicCamera camera) {
        this.gameState = gameState;
        this.playerState = playerState;
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = camera;
        this.hexMapRenderer = new HexMapRenderer(gameState, shapeRenderer, camera);
    }
    
    /**
     * Renderiza todos los jugadores visibles.
     * KISS: Tres pasadas - círculos, barras HP, nombres.
     */
    public void render() {
        GameMapDTO map = gameState.getGameMap();
        if (map == null || map.getPlayerPositions() == null) {
            return;
        }
        
        renderPlayerCircles(map);
        renderHealthBars(map);
        renderPlayerNames(map);
    }
    
    /**
     * Renderiza círculos de todos los jugadores.
     * DRY: Una pasada para todos los círculos.
     */
    private void renderPlayerCircles(GameMapDTO map) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Map.Entry<String, HexCoordinateDTO> entry : map.getPlayerPositions().entrySet()) {
            renderPlayerCircle(entry.getKey(), entry.getValue());
        }
        shapeRenderer.end();
    }
    
    /**
     * Renderiza barras de HP de todos los jugadores.
     * DRY: Una pasada para todas las barras.
     */
    private void renderHealthBars(GameMapDTO map) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Map.Entry<String, HexCoordinateDTO> entry : map.getPlayerPositions().entrySet()) {
            renderHealthBar(entry.getKey(), entry.getValue());
        }
        shapeRenderer.end();
    }
    
    /**
     * Renderiza nombres de todos los jugadores.
     * DRY: Una pasada para todos los nombres.
     */
    private void renderPlayerNames(GameMapDTO map) {
        spriteBatch.begin();
        for (Map.Entry<String, HexCoordinateDTO> entry : map.getPlayerPositions().entrySet()) {
            renderPlayerName(entry.getKey(), entry.getValue());
        }
        spriteBatch.end();
    }
    
    /**
     * Renderiza el círculo del jugador.
     */
    private void renderPlayerCircle(String playerId, HexCoordinateDTO position) {
        Vector2 pixelPos = hexMapRenderer.hexToPixel(position);
        
        Color color = getPlayerColor(playerId);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(pixelPos.x, pixelPos.y, PLAYER_RADIUS);
        
        // Borde más oscuro
        shapeRenderer.setColor(color.r * 0.5f, color.g * 0.5f, color.b * 0.5f, 1);
        shapeRenderer.circle(pixelPos.x, pixelPos.y, PLAYER_RADIUS + 2);
    }
    
    /**
     * Renderiza la barra de HP del jugador.
     */
    private void renderHealthBar(String playerId, HexCoordinateDTO position) {
        Vector2 pixelPos = hexMapRenderer.hexToPixel(position);
        float barX = pixelPos.x - HP_BAR_WIDTH / 2;
        float barY = pixelPos.y + HP_BAR_OFFSET_Y;
        
        // Fondo de la barra
        shapeRenderer.setColor(HP_BAR_BG);
        shapeRenderer.rect(barX, barY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        
        // Barra de HP (placeholder: 100% por ahora, extensible desde worldState)
        float hpPercent = getPlayerHP(playerId);
        shapeRenderer.setColor(HP_BAR_FG);
        shapeRenderer.rect(barX, barY, HP_BAR_WIDTH * hpPercent, HP_BAR_HEIGHT);
    }
    
    /**
     * Renderiza el nombre/ID del jugador.
     */
    private void renderPlayerName(String playerId, HexCoordinateDTO position) {
        Vector2 pixelPos = hexMapRenderer.hexToPixel(position);
        
        String displayName = getPlayerDisplayName(playerId);
        
        // Centrar texto
        float textWidth = font.draw(spriteBatch, displayName, 0, 0).width;
        float textX = pixelPos.x - textWidth / 2;
        float textY = pixelPos.y + NAME_OFFSET_Y;
        
        // Sombra del texto
        font.setColor(Color.BLACK);
        font.draw(spriteBatch, displayName, textX + 1, textY - 1);
        
        // Texto principal
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, displayName, textX, textY);
    }
    
    /**
     * Obtiene el color único para un jugador.
     */
    private Color getPlayerColor(String playerId) {
        if (playerId.equals(gameState.getPlayerId())) {
            return MY_PLAYER_COLOR;
        }
        
        // Asignar color según hash del ID
        int hash = Math.abs(playerId.hashCode());
        return PLAYER_COLORS[hash % PLAYER_COLORS.length];
    }
    
    /**
     * Obtiene el HP del jugador.
     * Usa PlayerLocalState para el jugador local, placeholder para otros.
     */
    private float getPlayerHP(String playerId) {
        if (playerId.equals(gameState.getPlayerId())) {
            return playerState.getHPPercentage();
        }
        
        // TODO: Obtener HP de otros jugadores desde gameState.getWorldStateValue("hp_" + playerId)
        return 1.0f;
    }
    
    /**
     * Obtiene el nombre a mostrar del jugador.
     */
    private String getPlayerDisplayName(String playerId) {
        if (playerId.equals(gameState.getPlayerId())) {
            return "TÚ";
        }
        
        // Buscar nombre en lista de jugadores del lobby
        return gameState.getLobbyPlayers().stream()
            .filter(p -> p.getPlayerId().equals(playerId))
            .map(p -> p.getPlayerName())
            .findFirst()
            .orElse(playerId.substring(0, Math.min(8, playerId.length())));
    }
}
