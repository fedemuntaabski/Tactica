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
        
        // TODO: Cargar texturas cuando estén disponibles
        // loadTexture("player", "assets/player.png");
        // loadTexture("tiles", "assets/tileset.png");
        
        loaded = true;
        logger.info("Assets loaded successfully");
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
