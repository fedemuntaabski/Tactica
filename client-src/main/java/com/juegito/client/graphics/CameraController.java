package com.juegito.client.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Controlador de cámara 2D con soporte para zoom y pan.
 * Aplicando KISS: funcionalidad simple y directa.
 */
public class CameraController {
    private final OrthographicCamera camera;
    
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 3.0f;
    private static final float ZOOM_SPEED = 0.1f;
    private static final float PAN_SPEED = 500f;
    
    private final Vector2 targetPosition;
    private float targetZoom;
    
    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
        this.targetPosition = new Vector2(camera.position.x, camera.position.y);
        this.targetZoom = camera.zoom;
    }
    
    /**
     * Actualiza la posición y zoom de la cámara suavemente.
     */
    public void update(float delta) {
        // Interpolar zoom suavemente
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, 0.1f);
        
        // Interpolar posición suavemente
        camera.position.x = MathUtils.lerp(camera.position.x, targetPosition.x, 0.1f);
        camera.position.y = MathUtils.lerp(camera.position.y, targetPosition.y, 0.1f);
    }
    
    /**
     * Ajusta el nivel de zoom.
     */
    public void zoom(float amount) {
        targetZoom = MathUtils.clamp(targetZoom + amount * ZOOM_SPEED, MIN_ZOOM, MAX_ZOOM);
    }
    
    /**
     * Mueve la cámara en dirección x, y.
     */
    public void pan(float x, float y, float delta) {
        targetPosition.x += x * PAN_SPEED * delta * camera.zoom;
        targetPosition.y += y * PAN_SPEED * delta * camera.zoom;
    }
    
    /**
     * Centra la cámara en una posición específica.
     */
    public void centerOn(float x, float y) {
        targetPosition.set(x, y);
    }
    
    /**
     * Resetea la cámara a posición y zoom por defecto.
     */
    public void reset() {
        targetPosition.set(0, 0);
        targetZoom = 1.0f;
    }
    
    public float getZoom() {
        return camera.zoom;
    }
}
