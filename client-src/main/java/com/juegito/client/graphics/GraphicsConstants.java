package com.juegito.client.graphics;

import com.badlogic.gdx.graphics.Color;

/**
 * Constantes para rendering gráfico.
 * Sigue principio DRY: evita magic numbers duplicados.
 */
public final class GraphicsConstants {
    
    // Prevenir instanciación
    private GraphicsConstants() {}
    
    // === HEX MAP RENDERING ===
    public static final float HEX_SIZE = 40f;
    
    // === PLAYER RENDERING ===
    public static final float PLAYER_RADIUS = 20f;
    public static final float HP_BAR_WIDTH = 40f;
    public static final float HP_BAR_HEIGHT = 5f;
    public static final float HP_BAR_OFFSET_Y = 30f;  // Actualizado para coincidir con PlayerRenderer
    public static final float NAME_OFFSET_Y = 45f;  // Offset para nombres de jugador
    public static final float PLAYER_NAME_OFFSET_Y = 35f;  // Alias legacy
    
    // === HUD ===
    public static final float HUD_MARGIN = 10f;
    public static final float PANEL_PADDING = 10f;  // Alias para HUD_MARGIN
    public static final float LINE_HEIGHT = 20f;
    public static final float FONT_SCALE = 1.5f;
    public static final int MAX_LOG_ENTRIES = 10;
    
    // === CAMERA ===
    public static final float MIN_ZOOM = 0.5f;
    public static final float MAX_ZOOM = 3.0f;
    public static final float ZOOM_SPEED = 0.1f;
    public static final float PAN_SPEED = 500f;
    public static final float CAMERA_LERP_FACTOR = 0.1f;
    
    // === COLORS ===
    public static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.15f, 1);
    public static final Color HEX_BORDER_COLOR = new Color(0.1f, 0.1f, 0.1f, 1);
    public static final Color HIGHLIGHT_COLOR = new Color(1, 1, 0, 0.5f);
    public static final Color PANEL_BG = new Color(0, 0, 0, 0.7f);  // HUD panel background
    public static final Color HUD_HIGHLIGHT_COLOR = new Color(1, 1, 0, 1);  // Yellow for HUD
    
    // Biome Colors
    public static final Color COLOR_MOUNTAIN = new Color(0.5f, 0.5f, 0.5f, 1);
    public static final Color COLOR_FOREST = new Color(0.2f, 0.6f, 0.2f, 1);
    public static final Color COLOR_PLAINS = new Color(0.8f, 0.8f, 0.6f, 1);
    
    // Tile Type Colors
    public static final Color COLOR_SPAWN = new Color(0.3f, 0.3f, 0.8f, 1);
    public static final Color COLOR_RESOURCE = new Color(0.8f, 0.6f, 0.2f, 1);
    public static final Color COLOR_STRATEGIC = new Color(0.6f, 0.2f, 0.6f, 1);
    public static final Color COLOR_BLOCKED = new Color(0.3f, 0.1f, 0.1f, 1);
    
    // Player Colors (predefinidos)
    public static final Color PLAYER_COLOR_1 = Color.RED;
    public static final Color PLAYER_COLOR_2 = Color.BLUE;
    public static final Color PLAYER_COLOR_3 = Color.GREEN;
    public static final Color PLAYER_COLOR_4 = Color.YELLOW;
    public static final Color MY_PLAYER_COLOR = new Color(1f, 0.8f, 0.2f, 1);  // Dorado para jugador local
    
    // Health Bar Colors
    public static final Color HP_BAR_BACKGROUND = Color.DARK_GRAY;
    public static final Color HP_BAR_BG = new Color(0.3f, 0.3f, 0.3f, 1);  // Alias específico
    public static final Color HP_BAR_FILL = Color.GREEN;
    public static final Color HP_BAR_FG = new Color(0.2f, 0.8f, 0.2f, 1);  // Alias específico
    
    // UI Colors
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 0.7f);
}
