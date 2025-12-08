package com.juegito.client.graphics;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.juegito.client.game.ActionExecutor;
import com.juegito.protocol.dto.HexCoordinateDTO;
import com.juegito.client.state.ClientGameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Procesa inputs del jugador (clicks, teclado).
 * Bajo acoplamiento: usa ActionExecutor para enviar comandos, no conoce la red.
 */
public class GameInputProcessor implements InputProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GameInputProcessor.class);
    
    private final ClientGameState gameState;
    private final OrthographicCamera camera;
    private final CameraController cameraController;
    private final HexMapRenderer mapRenderer;
    
    private ActionExecutor actionExecutor;
    
    public GameInputProcessor(ClientGameState gameState, OrthographicCamera camera,
                             CameraController cameraController, HexMapRenderer mapRenderer) {
        this.gameState = gameState;
        this.camera = camera;
        this.cameraController = cameraController;
        this.mapRenderer = mapRenderer;
    }
    
    /**
     * Inyecta el ActionExecutor cuando esté disponible.
     */
    public void setActionExecutor(ActionExecutor actionExecutor) {
        this.actionExecutor = actionExecutor;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Click izquierdo: seleccionar/mover
        if (button == 0) {
            handleLeftClick(screenX, screenY);
            return true;
        }
        
        return false;
    }
    
    /**
     * Maneja click izquierdo: selección de tile y movimiento.
     */
    private void handleLeftClick(int screenX, int screenY) {
        // Convertir coordenadas de pantalla a mundo
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        
        // Convertir a coordenadas hexagonales
        HexCoordinateDTO hexCoord = mapRenderer.pixelToHex(worldCoords.x, worldCoords.y);
        
        logger.debug("Clicked hex: ({}, {})", hexCoord.getQ(), hexCoord.getR());
        
        // Solo procesar si es el turno del jugador
        if (!gameState.isMyTurn()) {
            logger.info("Not your turn");
            mapRenderer.setSelectedTile(hexCoord);
            return;
        }
        
        // Si ya hay un tile seleccionado y es diferente, intentar mover
        HexCoordinateDTO selected = mapRenderer.getSelectedTile();
        if (selected != null && !coordEquals(selected, hexCoord)) {
            // Intentar mover al tile clickeado
            sendMovementAction(hexCoord);
            mapRenderer.clearSelection();
        } else {
            // Seleccionar el tile
            mapRenderer.setSelectedTile(hexCoord);
        }
    }
    
    /**
     * Envía acción de movimiento al servidor.
     */
    private void sendMovementAction(HexCoordinateDTO target) {
        if (actionExecutor == null) {
            logger.warn("ActionExecutor not set");
            return;
        }
        
        logger.info("Sending movement to ({}, {})", target.getQ(), target.getR());
        actionExecutor.sendMovementAction(target.getQ(), target.getR());
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom con scroll del mouse
        cameraController.zoom(amountY);
        return true;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        // Teclas de control de cámara se pueden agregar aquí
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    
    /**
     * Compara dos coordenadas hexagonales.
     */
    private boolean coordEquals(HexCoordinateDTO a, HexCoordinateDTO b) {
        return a.getQ() == b.getQ() && a.getR() == b.getR();
    }
}
