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
 * Implementa KISS: Renderizado directo sin complejidad innecesaria.
 */
public class HexMapRenderer {
    private final ClientGameState gameState;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    
    // Constantes de renderizado hexagonal
    private static final float HEX_WIDTH = HEX_SIZE * 2;
    private static final float HEX_HEIGHT = (float) (Math.sqrt(3) * HEX_SIZE);
    private static final float HEX_3_2 = HEX_SIZE * 3f / 2f;
    private static final float HEX_SQRT3 = HEX_SIZE * (float) Math.sqrt(3);
    
    // Colores por bioma (usa GraphicsConstants)
    private static final Map<String, Color> BIOME_COLORS = createBiomeColors();
    private static final Map<String, Color> TILE_COLORS = createTileColors();
    
    private HexCoordinateDTO selectedTile;
    
    public HexMapRenderer(ClientGameState gameState, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        this.gameState = gameState;
        this.shapeRenderer = shapeRenderer;
        this.camera = camera;
    }
    
    /**
     * Crea mapa de colores de biomas.
     * DRY: Inicialización estática para evitar recrear constantemente.
     */
    private static Map<String, Color> createBiomeColors() {
        Map<String, Color> colors = new HashMap<>();
        colors.put("FOREST", COLOR_FOREST);
        colors.put("MOUNTAIN", COLOR_MOUNTAIN);
        colors.put("PLAINS", COLOR_PLAINS);
        return colors;
    }
    
    /**
     * Crea mapa de colores de tipos de tile.
     * DRY: Inicialización estática para evitar recrear constantemente.
     */
    private static Map<String, Color> createTileColors() {
        Map<String, Color> colors = new HashMap<>();
        colors.put("SPAWN", COLOR_SPAWN);
        colors.put("RESOURCE", COLOR_RESOURCE);
        colors.put("STRATEGIC", COLOR_STRATEGIC);
        colors.put("BLOCKED", COLOR_BLOCKED);
        return colors;
    }
    
    /**
     * Renderiza el mapa completo.
     * KISS: Tres pasadas simples - relleno, bordes, highlight.
     */
    public void render() {
        GameMapDTO map = gameState.getGameMap();
        if (map == null || map.getTiles() == null) {
            return;
        }
        
        renderTilesFilled(map);
        renderTilesBorders(map);
        renderSelection();
    }
    
    /**
     * Renderiza tiles rellenos.
     * DRY: Pasada única para todos los tiles.
     */
    private void renderTilesFilled(GameMapDTO map) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (TileDTO tile : map.getTiles()) {
            Color color = getTileColor(tile);
            shapeRenderer.setColor(color);
            drawHexagon(tile.getCoordinate(), ShapeRenderer.ShapeType.Filled);
        }
        shapeRenderer.end();
    }
    
    /**
     * Renderiza bordes de tiles.
     * DRY: Pasada única para todos los bordes.
     */
    private void renderTilesBorders(GameMapDTO map) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(HEX_BORDER_COLOR);
        for (TileDTO tile : map.getTiles()) {
            drawHexagon(tile.getCoordinate(), ShapeRenderer.ShapeType.Line);
        }
        shapeRenderer.end();
    }
    
    /**
     * Renderiza tile seleccionado.
     * DRY: Lógica de highlight separada.
     */
    private void renderSelection() {
        if (selectedTile != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(HIGHLIGHT_COLOR);
            drawHexagon(selectedTile, ShapeRenderer.ShapeType.Filled);
            shapeRenderer.end();
        }
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
    /**
     * Convierte coordenadas hexagonales a píxeles (flat-top hexagons).
     * DRY: Usa constantes precalculadas.
     */
    public Vector2 hexToPixel(HexCoordinateDTO coord) {
        float x = HEX_3_2 * coord.getQ();
        float y = HEX_SQRT3 * (coord.getR() + coord.getQ() / 2f);
        return new Vector2(x, y);
    }
    
    /**
     * Convierte coordenadas de píxeles a hexagonales (aproximado).
     * Utiliza algoritmo de redondeo para obtener hex correcto.
     */
    public HexCoordinateDTO pixelToHex(float x, float y) {
        float q = (2f / 3f * x) / HEX_SIZE;
        float r = ((-1f / 3f * x) + ((float) Math.sqrt(3) / 3f * y)) / HEX_SIZE;
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
