package com.juegito.client.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor simple de assets para el cliente.
 * Preparado para futuro reemplazo de shapes por sprites.
 * Implementa KISS: Gestión básica sin complejidad innecesaria.
 */
public class SimpleAssetManager {
    private static final Logger logger = LoggerFactory.getLogger(SimpleAssetManager.class);
    
    private final Map<String, Texture> textures;
    private final Map<String, TextureRegion> regions;
    private BitmapFont defaultFont;
    
    private boolean loaded;
    
    public SimpleAssetManager() {
        this.textures = new HashMap<>();
        this.regions = new HashMap<>();
        this.loaded = false;
    }
    
    /**
     * Carga assets básicos.
     * Por ahora solo fuente, preparado para sprites futuros.
     */
    public void load() {
        if (loaded) {
            logger.warn("Assets already loaded");
            return;
        }
        
        logger.info("Loading assets...");
        
        // Fuente por defecto
        defaultFont = new BitmapFont();
        defaultFont.getData().setScale(1.5f);
        
        // Crear texturas procedurales simples (pixmaps de colores)
        // Esto permite tener texturas básicas sin necesidad de archivos externos
        createProceduralTextures();
        
        loaded = true;
        logger.info("Assets loaded successfully");
    }
    
    /**
     * Crea texturas procedurales básicas para el juego.
     * Implementa KISS: texturas simples de colores sólidos como placeholders.
     */
    private void createProceduralTextures() {
        com.badlogic.gdx.graphics.Pixmap pixmap;
        
        // Textura de jugador (círculo azul)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.3f, 0.6f, 1.0f, 1.0f);
        pixmap.fillCircle(16, 16, 14);
        textures.put("player", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de enemigo (círculo rojo)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1.0f, 0.3f, 0.3f, 1.0f);
        pixmap.fillCircle(16, 16, 14);
        textures.put("enemy", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de tile normal (hexágono verde claro)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.6f, 0.8f, 0.6f, 1.0f);
        pixmap.fill();
        textures.put("tile_normal", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de tile montaña (hexágono gris)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1.0f);
        pixmap.fill();
        textures.put("tile_mountain", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de tile agua (hexágono azul)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(64, 64, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.4f, 0.6f, 0.9f, 1.0f);
        pixmap.fill();
        textures.put("tile_water", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de ítem (cuadrado dorado)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(24, 24, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1.0f, 0.85f, 0.0f, 1.0f);
        pixmap.fill();
        textures.put("item", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de UI panel (fondo semi-transparente)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        pixmap.fill();
        textures.put("ui_panel", new Texture(pixmap));
        pixmap.dispose();
        
        // Textura de UI botón (fondo claro)
        pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.3f, 0.3f, 0.4f, 0.9f);
        pixmap.fill();
        textures.put("ui_button", new Texture(pixmap));
        pixmap.dispose();
        
        logger.info("Created {} procedural textures", textures.size());
    }
    
    /**
     * Carga una textura desde archivo.
     * Preparado para uso futuro.
     */
    public void loadTexture(String key, String path) {
        try {
            Texture texture = new Texture(path);
            textures.put(key, texture);
            logger.debug("Loaded texture: {}", key);
        } catch (Exception e) {
            logger.error("Failed to load texture {}: {}", key, e.getMessage());
        }
    }
    
    /**
     * Crea una región de textura.
     * Útil para spritesheets.
     */
    public void createRegion(String key, String textureKey, int x, int y, int width, int height) {
        Texture texture = textures.get(textureKey);
        if (texture == null) {
            logger.warn("Texture not found for region: {}", textureKey);
            return;
        }
        
        TextureRegion region = new TextureRegion(texture, x, y, width, height);
        regions.put(key, region);
    }
    
    /**
     * Obtiene una textura cargada.
     */
    public Texture getTexture(String key) {
        return textures.get(key);
    }
    
    /**
     * Obtiene una región de textura.
     */
    public TextureRegion getRegion(String key) {
        return regions.get(key);
    }
    
    /**
     * Obtiene la fuente por defecto.
     */
    public BitmapFont getDefaultFont() {
        return defaultFont;
    }
    
    /**
     * Verifica si tiene una textura.
     */
    public boolean hasTexture(String key) {
        return textures.containsKey(key);
    }
    
    /**
     * Verifica si los assets están cargados.
     */
    public boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Libera todos los recursos.
     */
    public void dispose() {
        logger.info("Disposing assets...");
        
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
        regions.clear();
        
        if (defaultFont != null) {
            defaultFont.dispose();
        }
        
        loaded = false;
        logger.info("Assets disposed");
    }
}
