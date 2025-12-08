package com.juegito.client.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.juegito.protocol.dto.GameMapDTO;
import com.juegito.protocol.dto.HexCoordinateDTO;
import com.juegito.protocol.dto.TileDTO;
import com.juegito.client.state.ClientGameState;

import java.util.HashMap;
import java.util.Map;

import static com.juegito.client.graphics.GraphicsConstants.*;

/**
 * Renderiza el mapa hexagonal usando LibGDX.
 * Bajo acoplamiento: solo conoce GameMapDTO, no la lógica de red.
 * Alta cohesión: solo se encarga de renderizar el mapa.
 */
public class HexMapRenderer {
    private final ClientGameState gameState;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    
    // Constantes de renderizado hexagonal
    private static final float HEX_WIDTH = HEX_SIZE * 2;
    private static final float HEX_HEIGHT = (float) (Math.sqrt(3) * HEX_SIZE);
    
    // Colores por bioma (usa GraphicsConstants)
    private static final Map<String, Color> BIOME_COLORS = new HashMap<>();
    static {
        BIOME_COLORS.put("FOREST", COLOR_FOREST);
        BIOME_COLORS.put("MOUNTAIN", COLOR_MOUNTAIN);
        BIOME_COLORS.put("PLAINS", COLOR_PLAINS);
    }
    
    // Colores por tipo de tile (usa GraphicsConstants)
    private static final Map<String, Color> TILE_COLORS = new HashMap<>();
    static {
        TILE_COLORS.put("SPAWN", COLOR_SPAWN);
        TILE_COLORS.put("RESOURCE", COLOR_RESOURCE);
        TILE_COLORS.put("STRATEGIC", COLOR_STRATEGIC);
        TILE_COLORS.put("BLOCKED", COLOR_BLOCKED);
    }
    
    private HexCoordinateDTO selectedTile;
    
    public HexMapRenderer(ClientGameState gameState, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.gameState = gameState;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }
    
    /**
     * Renderiza el mapa completo.
     */
    public void render() {
        GameMapDTO map = gameState.getGameMap();
        if (map == null || map.getTiles() == null) {
            return;
        }
        
        // Renderizar tiles rellenos
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (TileDTO tile : map.getTiles()) {
            renderTileFilled(tile);
        }
        shapeRenderer.end();
        
        // Renderizar bordes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (TileDTO tile : map.getTiles()) {
            renderTileBorder(tile);
        }
        shapeRenderer.end();
        
        // Renderizar highlight si hay tile seleccionado
        if (selectedTile != null) {
            renderHighlight(selectedTile);
        }
    }
    
    /**
     * Renderiza un tile relleno con color según bioma/tipo.
     */
    private void renderTileFilled(TileDTO tile) {
        Color color = getTileColor(tile);
        shapeRenderer.setColor(color);
        drawHexagon(tile.getCoordinate(), ShapeRenderer.ShapeType.Filled);
    }
    
    /**
     * Renderiza el borde de un tile.
     */
    private void renderTileBorder(TileDTO tile) {
        shapeRenderer.setColor(HEX_BORDER_COLOR);
        drawHexagon(tile.getCoordinate(), ShapeRenderer.ShapeType.Line);
    }
    
    /**
     * Renderiza highlight del jugador activo o tile seleccionado.
     */
    private void renderHighlight(HexCoordinateDTO coord) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(HIGHLIGHT_COLOR);
        drawHexagon(coord, ShapeRenderer.ShapeType.Filled);
        shapeRenderer.end();
    }
    
    /**
     * Dibuja un hexágono en las coordenadas especificadas.
     */
    private void drawHexagon(HexCoordinateDTO coord, ShapeRenderer.ShapeType type) {
        Vector2 center = hexToPixel(coord);
        
        // Calcular vértices del hexágono (flat-top)
        float[] vertices = new float[12]; // 6 vértices * 2 coordenadas
        for (int i = 0; i < 6; i++) {
            float angle = (float) (Math.PI / 3 * i);
            vertices[i * 2] = center.x + HEX_SIZE * (float) Math.cos(angle);
            vertices[i * 2 + 1] = center.y + HEX_SIZE * (float) Math.sin(angle);
        }
        
        // Dibujar hexágono
        if (type == ShapeRenderer.ShapeType.Filled) {
            // Dibujar como triángulos desde el centro
            for (int i = 0; i < 6; i++) {
                int next = (i + 1) % 6;
                shapeRenderer.triangle(
                    center.x, center.y,
                    vertices[i * 2], vertices[i * 2 + 1],
                    vertices[next * 2], vertices[next * 2 + 1]
                );
            }
        } else {
            // Dibujar bordes
            for (int i = 0; i < 6; i++) {
                int next = (i + 1) % 6;
                shapeRenderer.line(
                    vertices[i * 2], vertices[i * 2 + 1],
                    vertices[next * 2], vertices[next * 2 + 1]
                );
            }
        }
    }
    
    /**
     * Convierte coordenadas hexagonales a píxeles (flat-top hexagons).
     */
    public Vector2 hexToPixel(HexCoordinateDTO coord) {
        float x = HEX_SIZE * 3f/2f * coord.getQ();
        float y = HEX_SIZE * (float) Math.sqrt(3) * (coord.getR() + coord.getQ() / 2f);
        return new Vector2(x, y);
    }
    
    /**
     * Convierte coordenadas de píxeles a hexagonales (aproximado).
     */
    public HexCoordinateDTO pixelToHex(float x, float y) {
        // Convertir a coordenadas fraccionarias
        float q = (2f/3f * x) / HEX_SIZE;
        float r = ((-1f/3f * x) + ((float) Math.sqrt(3)/3f * y)) / HEX_SIZE;
        
        // Redondear a coordenadas enteras
        return roundHex(q, r);
    }
    
    /**
     * Redondea coordenadas hexagonales fraccionarias.
     */
    private HexCoordinateDTO roundHex(float q, float r) {
        float s = -q - r;
        
        int rq = Math.round(q);
        int rr = Math.round(r);
        int rs = Math.round(s);
        
        float qDiff = Math.abs(rq - q);
        float rDiff = Math.abs(rr - r);
        float sDiff = Math.abs(rs - s);
        
        if (qDiff > rDiff && qDiff > sDiff) {
            rq = -rr - rs;
        } else if (rDiff > sDiff) {
            rr = -rq - rs;
        }
        
        HexCoordinateDTO result = new HexCoordinateDTO();
        result.setQ(rq);
        result.setR(rr);
        return result;
    }
    
    /**
     * Obtiene el color apropiado para un tile.
     */
    private Color getTileColor(TileDTO tile) {
        // Prioridad: tipo especial > bioma
        if (!"NORMAL".equals(tile.getType()) && TILE_COLORS.containsKey(tile.getType())) {
            return TILE_COLORS.get(tile.getType());
        }
        
        return BIOME_COLORS.getOrDefault(tile.getBiome(), Color.GRAY);
    }
    
    /**
     * Establece el tile seleccionado para highlight.
     */
    public void setSelectedTile(HexCoordinateDTO coord) {
        this.selectedTile = coord;
    }
    
    /**
     * Limpia la selección.
     */
    public void clearSelection() {
        this.selectedTile = null;
    }
    
    public HexCoordinateDTO getSelectedTile() {
        return selectedTile;
    }
}
