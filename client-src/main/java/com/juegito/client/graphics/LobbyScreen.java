package com.juegito.client.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.LobbyClientState;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.character.ClassInfoDTO;
import com.juegito.protocol.dto.lobby.*;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Pantalla del lobby del juego usando VisUI para una interfaz moderna.
 * Muestra lista de jugadores conectados, log de eventos, chat y controles.
 */
public class LobbyScreen extends ScreenAdapter implements LobbyClientState.LobbyStateListener {
    private static final Logger logger = LoggerFactory.getLogger(LobbyScreen.class);
    
    private final LobbyClientState lobbyState;
    private final ConnectionManager connectionManager;
    private final Runnable onGameStart;
    
    private Stage stage;
    private VisTable rootTable;
    private VisTable mainContent;
    
    // Componentes UI con VisUI
    private VisTable playerListTable;
    private VisTable lobbyLogTable;
    private VisScrollPane lobbyLogScrollPane;
    private VisTextField chatInputField;
    private VisScrollPane chatScrollPane;
    private VisTable chatTable;
    private VisTextButton readyButton;
    private VisTextButton startButton;
    private VisTextButton leaveButton;
    private VisTextButton sendChatButton;
    private VisLabel statusLabel;
    private VisWindow classSelectorWindow;
    private VisTextField nameChangeField;
    private VisTextButton changeNameButton;
    private VisTable mapPreviewPanel;
    
    private boolean isShowingMapPreview = false;
    
    private final List<String> lobbyLog;
    
    public LobbyScreen(LobbyClientState lobbyState, ConnectionManager connectionManager, Runnable onGameStart) {
        this.lobbyState = lobbyState;
        this.connectionManager = connectionManager;
        this.onGameStart = onGameStart;
        this.lobbyLog = new ArrayList<>();
        
        lobbyState.addListener(this);
    }
    
    @Override
    public void show() {
        logger.info("Showing lobby screen");
        
        // Cargar VisUI skin
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        // Crear UI moderna
        createModernUI();
        
        // Actualizar UI con datos actuales
        refreshUI();
    }
    
    /**
     * Crea la interfaz moderna con VisUI.
     */
    private void createModernUI() {
        rootTable = new VisTable();
        rootTable.setFillParent(true);
        rootTable.pad(20);
        stage.addActor(rootTable);
        
        // Header con título del lobby
        VisTable headerTable = new VisTable();
        VisLabel titleLabel = new VisLabel("LOBBY - ESPERANDO JUGADORES");
        titleLabel.setColor(Color.CYAN);
        headerTable.add(titleLabel).expandX().left().padBottom(20);
        rootTable.add(headerTable).fillX().row();
        
        // Contenedor principal dividido en paneles (guardamos referencia)
        mainContent = new VisTable();
        
        // Panel izquierdo: Jugadores + Log
        VisTable leftPanel = createPlayerPanel();
        mainContent.add(leftPanel).width(450).top().padRight(20);
        
        // Panel derecho: Chat (inicialmente)
        VisTable chatPanel = createChatPanel();
        mainContent.add(chatPanel).width(450).top();
        
        rootTable.add(mainContent).expand().fill().row();
        
        // Panel inferior: Controles
        VisTable controlsPanel = createControlsPanel();
        rootTable.add(controlsPanel).fillX().padTop(20).row();
        
        // Status bar
        statusLabel = new VisLabel("Estado: Conectado");
        statusLabel.setColor(Color.LIGHT_GRAY);
        rootTable.add(statusLabel).left().padTop(10);
    }
    
    private VisTable createPlayerPanel() {
        VisTable panel = new VisTable();
        panel.top();
        
        // Título de jugadores
        VisLabel playersTitle = new VisLabel("JUGADORES CONECTADOS");
        playersTitle.setColor(Color.YELLOW);
        panel.add(playersTitle).left().padBottom(15).row();
        
        // Lista de jugadores
        playerListTable = new VisTable();
        playerListTable.top().left();
        VisScrollPane playerScroll = new VisScrollPane(playerListTable);
        playerScroll.setFadeScrollBars(false);
        playerScroll.setScrollingDisabled(true, false);
        panel.add(playerScroll).width(450).height(250).fillX().row();
        
        // Log de eventos
        VisLabel logTitle = new VisLabel("LOG DE EVENTOS", "default");
        logTitle.setColor(Color.ORANGE);
        panel.add(logTitle).left().padTop(20).padBottom(10).row();
        
        lobbyLogTable = new VisTable();
        lobbyLogTable.top().left();
        lobbyLogScrollPane = new VisScrollPane(lobbyLogTable);
        lobbyLogScrollPane.setFadeScrollBars(false);
        lobbyLogScrollPane.setScrollingDisabled(true, false);
        lobbyLogScrollPane.setForceScroll(false, true);
        lobbyLogScrollPane.setFlickScroll(true);
        lobbyLogScrollPane.setOverscroll(false, false);
        panel.add(lobbyLogScrollPane).width(450).height(200).fillX();
        
        return panel;
    }
    
    private VisTable createChatPanel() {
        VisTable panel = new VisTable();
        panel.top();
        
        // Título del chat
        VisLabel chatTitle = new VisLabel("CHAT DEL LOBBY");
        chatTitle.setColor(Color.YELLOW);
        panel.add(chatTitle).left().padBottom(15).row();
        
        // Área de chat
        chatTable = new VisTable();
        chatTable.top().left();
        chatScrollPane = new VisScrollPane(chatTable);
        chatScrollPane.setFadeScrollBars(false);
        chatScrollPane.setScrollingDisabled(true, false);
        panel.add(chatScrollPane).width(450).height(400).fillX().row();
        
        // Input de chat
        VisTable inputPanel = new VisTable();
        chatInputField = new VisTextField();
        chatInputField.setMessageText("Escribe un mensaje...");
        chatInputField.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    sendChatMessage(chatInputField.getText());
                    chatInputField.setText("");
                    stage.setKeyboardFocus(null);
                    return true;
                }
                return false;
            }
        });
        
        sendChatButton = new VisTextButton("Enviar");
        sendChatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendChatMessage(chatInputField.getText());
                chatInputField.setText("");
            }
        });
        
        inputPanel.add(chatInputField).width(350).height(40).padRight(10);
        inputPanel.add(sendChatButton).width(90).height(40);
        panel.add(inputPanel).padTop(10);
        
        return panel;
    }
    
    private VisTable createMapPreviewPanel() {
        VisTable panel = new VisTable();
        panel.top();
        
        // Título
        VisLabel previewTitle = new VisLabel("PREVIEW DEL MAPA");
        previewTitle.setColor(Color.GREEN);
        panel.add(previewTitle).center().padBottom(15).row();
        
        // Descripción del mapa
        VisTable descriptionTable = new VisTable();
        descriptionTable.defaults().pad(10).left();
        
        VisLabel mapTypeLabel = new VisLabel("Tipo de Mapa: Hexagonal Procedural");
        mapTypeLabel.setColor(Color.CYAN);
        descriptionTable.add(mapTypeLabel).row();
        
        VisLabel biomeLabel = new VisLabel("Biomas: Bosque, Montaña, Desierto, Agua");
        biomeLabel.setColor(Color.LIGHT_GRAY);
        descriptionTable.add(biomeLabel).row();
        
        // Título de mecánicas
        VisLabel mechanicsTitle = new VisLabel("MECÁNICAS DEL JUEGO");
        mechanicsTitle.setColor(Color.YELLOW);
        descriptionTable.add(mechanicsTitle).padTop(20).row();
        
        // Lista de mecánicas
        String[] mechanics = {
            "• Movimiento por turnos en mapa hexagonal",
            "• Cada hexágono tiene diferentes tipos de terreno",
            "• Click en hexágono adyacente para mover",
            "• Scroll del mouse para hacer zoom",
            "• Sistema de combate por turnos",
            "• Enemigos y eventos aleatorios",
            "• Sistema de inventario y loot",
            "• Habilidades especiales por clase"
        };
        
        for (String mechanic : mechanics) {
            VisLabel mechanicLabel = new VisLabel(mechanic);
            mechanicLabel.setColor(Color.WHITE);
            mechanicLabel.setWrap(true);
            descriptionTable.add(mechanicLabel).width(420).row();
        }
        
        VisScrollPane scrollPane = new VisScrollPane(descriptionTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        panel.add(scrollPane).width(450).height(450).fillX().row();
        
        // Mensaje de inicio
        VisLabel startHint = new VisLabel("El host puede iniciar la partida cuando esté listo");
        startHint.setColor(Color.GREEN);
        startHint.setAlignment(1); // Center
        panel.add(startHint).padTop(15);
        
        return panel;
    }
    
    private VisTable createControlsPanel() {
        VisTable panel = new VisTable();
        panel.defaults().pad(5);
        
        // Panel de cambio de nombre (primera fila)
        VisTable namePanel = new VisTable();
        VisLabel nameLabel = new VisLabel("Cambiar nombre:");
        nameLabel.setColor(Color.CYAN);
        
        nameChangeField = new VisTextField("");
        nameChangeField.setMessageText("Nuevo nombre (3-20 caracteres)");
        
        changeNameButton = new VisTextButton("CAMBIAR");
        changeNameButton.setColor(Color.CYAN);
        changeNameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changePlayerName();
            }
        });
        
        namePanel.add(nameLabel).padRight(10);
        namePanel.add(nameChangeField).width(200).height(35).padRight(10);
        namePanel.add(changeNameButton).width(100).height(35);
        
        panel.add(namePanel).colspan(3).center().padBottom(10).row();
        
        // Botón de selección de clase
        VisTextButton selectClassButton = new VisTextButton("SELECCIONAR CLASE");
        selectClassButton.setColor(Color.CYAN);
        selectClassButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(() -> {
                    try {
                        showClassSelector();
                    } catch (Exception e) {
                        logger.error("Error showing class selector: {}", e.getMessage(), e);
                        addToLobbyLog("Error al abrir selector de clases: " + e.getMessage());
                    }
                });
            }
        });
        
        // Botón Ready
        readyButton = new VisTextButton("LISTO");
        readyButton.setColor(Color.GREEN);
        readyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleReady();
            }
        });
        
        // Botón Start
        startButton = new VisTextButton("INICIAR PARTIDA");
        startButton.setColor(Color.ORANGE);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                requestStartMatch();
            }
        });
        
        // Botón Leave
        leaveButton = new VisTextButton("SALIR DEL LOBBY");
        leaveButton.setColor(Color.RED);
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                leaveLobby();
            }
        });
        
        panel.add(selectClassButton).width(200).height(50);
        panel.add(readyButton).width(200).height(50);
        panel.add(startButton).width(200).height(50);
        panel.row();
        panel.add(leaveButton).colspan(3).width(620).height(50).center();
        
        return panel;
    }
    
    /**
     * Actualiza la UI con el estado actual del lobby.
     */
    private void refreshUI() {
        // Actualizar lista de jugadores
        refreshPlayerList();
        
        // Actualizar log de eventos
        refreshLobbyLog();
        
        // Actualizar chat (solo si está visible)
        if (chatTable != null && chatTable.hasParent()) {
            refreshChat();
        }
        
        // Actualizar visibilidad y estado de botones
        boolean isHost = lobbyState.isLocalPlayerHost();
        boolean isReady = lobbyState.isLocalPlayerReady();
        boolean allReady = lobbyState.areAllPlayersReady();
        int playerCount = lobbyState.getConnectedPlayerCount();
        
        // Cambiar entre chat y preview del mapa según el estado
        updateRightPanel(allReady);
        
        // Tanto el host como los demás jugadores necesitan el botón LISTO
        readyButton.setVisible(true);
        startButton.setVisible(isHost);
        startButton.setDisabled(!allReady || playerCount < 1);
        
        if (isReady) {
            readyButton.setText("NO LISTO");
            readyButton.setColor(Color.YELLOW);
        } else {
            readyButton.setText("LISTO");
            readyButton.setColor(Color.GREEN);
        }
        
        // Actualizar mensaje de estado
        updateStatusMessage();
    }
    
    /**
     * Actualiza el panel derecho entre chat y preview del mapa.
     */
    private void updateRightPanel(boolean showMapPreview) {
        // Solo cambiar si el estado cambió
        if (showMapPreview == isShowingMapPreview) {
            return; // No hay cambio necesario
        }
        
        isShowingMapPreview = showMapPreview;
        
        // Cambiar el panel
        mainContent.clear();
        
        // Panel izquierdo: Jugadores + Log
        VisTable leftPanel = createPlayerPanel();
        mainContent.add(leftPanel).width(450).top().padRight(20);
        
        if (showMapPreview) {
            // Mostrar preview del mapa
            mapPreviewPanel = createMapPreviewPanel();
            mainContent.add(mapPreviewPanel).width(450).top();
        } else {
            // Mostrar chat
            VisTable chatPanel = createChatPanel();
            mainContent.add(chatPanel).width(450).top();
        }
        
        // Re-refrescar todos los paneles
        refreshPlayerList();
        refreshLobbyLog();
        if (!showMapPreview) {
            refreshChat();
        }
    }
    
    private void refreshPlayerList() {
        playerListTable.clear();
        
        for (PlayerLobbyDataDTO player : lobbyState.getPlayers()) {
            VisTable playerRow = new VisTable();
            
            // Nombre del jugador
            VisLabel nameLabel = new VisLabel(player.getPlayerName());
            nameLabel.setColor(getClassColor(player.getSelectedClass()));
            
            // Estado del jugador
            VisLabel statusLabel = new VisLabel(getStatusText(player.getConnectionStatus()));
            statusLabel.setColor(getStatusColor(player.getConnectionStatus()));
            
            // Clase seleccionada
            String className = player.getSelectedClass() != null ? player.getSelectedClass() : "Sin clase";
            VisLabel classLabel = new VisLabel(" [" + className + "]");
            classLabel.setColor(Color.GRAY);
            
            // Indicador de host
            if (player.isHost()) {
                VisLabel hostLabel = new VisLabel(" (HOST)");
                hostLabel.setColor(Color.GOLD);
                playerRow.add(hostLabel).padRight(5);
            }
            
            playerRow.add(nameLabel).left().expandX();
            playerRow.add(classLabel).left();
            playerRow.add(statusLabel).right().padLeft(10);
            
            playerListTable.add(playerRow).width(430).left().padBottom(8);
            playerListTable.row();
        }
    }
    
    private void refreshLobbyLog() {
        lobbyLogTable.clear();
        for (String logEntry : lobbyLog) {
            VisLabel logLabel = new VisLabel(logEntry);
            logLabel.setWrap(true);
            lobbyLogTable.add(logLabel).width(430).left().padBottom(5);
            lobbyLogTable.row();
        }
        // Hacer scroll al final automáticamente
        if (lobbyLogScrollPane != null) {
            lobbyLogScrollPane.layout();
            lobbyLogScrollPane.scrollTo(0, 0, 0, 0);
        }
    }
    
    private void refreshChat() {
        chatTable.clear();
        for (ChatMessageDTO msg : lobbyState.getChatMessages()) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date(msg.getTimestamp()));
            String text = "[" + timestamp + "] " + msg.getPlayerName() + ": " + msg.getMessage();
            VisLabel chatLabel = new VisLabel(text);
            chatLabel.setWrap(true);
            
            // Resaltar mensajes propios
            if (msg.getPlayerId().equals(lobbyState.getLocalPlayerId())) {
                chatLabel.setColor(Color.LIME);
            }
            
            chatTable.add(chatLabel).width(430).left().padBottom(4);
            chatTable.row();
        }
        chatScrollPane.layout();
        chatScrollPane.scrollTo(0, 0, 0, 0);
    }
    
    private String getStatusText(ConnectionStatus status) {
        switch (status) {
            case READY: return "✓ LISTO";
            case CONNECTED: return "⊙ Conectado";
            case DISCONNECTED: return "✗ Desconectado";
            default: return "? Desconocido";
        }
    }
    
    private Color getStatusColor(ConnectionStatus status) {
        switch (status) {
            case READY: return Color.GREEN;
            case CONNECTED: return Color.YELLOW;
            case DISCONNECTED: return Color.RED;
            default: return Color.GRAY;
        }
    }
    
    private void addToLobbyLog(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] " + message;
        lobbyLog.add(logEntry);
        
        // Mantener solo últimas 20 entradas
        if (lobbyLog.size() > 20) {
            lobbyLog.remove(0);
        }
    }
    
    private void updateStatusMessage() {
        LobbyStatus status = lobbyState.getStatus();
        switch (status) {
            case WAITING:
                if (lobbyState.isLocalPlayerHost()) {
                    if (lobbyState.areAllPlayersReady()) {
                        statusLabel.setText("Todos listos. Puedes iniciar la partida.");
                        statusLabel.setColor(Color.GREEN);
                    } else {
                        statusLabel.setText("Esperando que todos estén listos...");
                        statusLabel.setColor(Color.YELLOW);
                    }
                } else {
                    if (lobbyState.isLocalPlayerReady()) {
                        statusLabel.setText("Esperando al host...");
                        statusLabel.setColor(Color.GREEN);
                    } else {
                        statusLabel.setText("Presiona 'LISTO' cuando estés preparado.");
                        statusLabel.setColor(Color.YELLOW);
                    }
                }
                break;
            case STARTING:
                statusLabel.setText("¡Iniciando partida!");
                statusLabel.setColor(Color.GREEN);
                break;
            case IN_GAME:
                statusLabel.setText("Partida en curso...");
                statusLabel.setColor(Color.CYAN);
                break;
        }
    }
    
    // Acciones
    
    private void toggleReady() {
        // Verificar que tengamos el ID del jugador local
        String localPlayerId = lobbyState.getLocalPlayerId();
        if (localPlayerId == null) {
            addToLobbyLog("Error: No se ha establecido tu ID de jugador.");
            logger.error("Local player ID is null when trying to toggle ready");
            return;
        }
        
        boolean newReady = !lobbyState.isLocalPlayerReady();
        
        // Si intenta ponerse listo, verificar que tenga clase seleccionada
        if (newReady) {
            PlayerLobbyDataDTO localPlayer = lobbyState.getLocalPlayer();
            if (localPlayer == null) {
                addToLobbyLog("Error: No se pudo obtener tu información del lobby.");
                logger.error("Local player not found in players list. LocalPlayerId: {}, Players count: {}", 
                    localPlayerId, lobbyState.getPlayers().size());
                return;
            }
            
            if (localPlayer.getSelectedClass() == null || localPlayer.getSelectedClass().isEmpty()) {
                addToLobbyLog("Debes seleccionar una clase antes de marcar como LISTO.");
                return;
            }
        }
        
        ReadyStatusChangeDTO request = new ReadyStatusChangeDTO(newReady);
        Message message = new Message(MessageType.READY_STATUS_CHANGE, localPlayerId, request);
        connectionManager.sendMessage(message);
        
        if (newReady) {
            addToLobbyLog("Marcaste como LISTO.");
        } else {
            addToLobbyLog("Cancelaste el estado LISTO.");
        }
        logger.info("Toggled ready status to: {}", newReady);
    }
    
    private void requestStartMatch() {
        StartMatchRequestDTO request = new StartMatchRequestDTO();
        Message message = new Message(MessageType.START_MATCH_REQUEST, lobbyState.getLocalPlayerId(), request);
        connectionManager.sendMessage(message);
        
        addToLobbyLog("Solicitando inicio de partida...");
        logger.info("Requested start match");
    }
    
    private void sendChatMessage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        
        String message = text.trim();
        if (message.length() > 200) {
            message = message.substring(0, 200);
        }
        
        ChatMessageRequestDTO request = new ChatMessageRequestDTO(message);
        Message msg = new Message(MessageType.CHAT_MESSAGE_REQUEST, lobbyState.getLocalPlayerId(), request);
        connectionManager.sendMessage(msg);
        logger.debug("Sent chat message: {}", message);
    }
    
    private void leaveLobby() {
        LeaveLobbyDTO request = new LeaveLobbyDTO();
        Message message = new Message(MessageType.LEAVE_LOBBY, lobbyState.getLocalPlayerId(), request);
        connectionManager.sendMessage(message);
        
        addToLobbyLog("Saliendo del lobby...");
        logger.info("Left lobby");
        Gdx.app.exit();
    }
    
    private void changePlayerName() {
        String newName = nameChangeField.getText();
        
        if (newName == null || newName.trim().isEmpty()) {
            addToLobbyLog("Error: El nombre no puede estar vacío.");
            return;
        }
        
        newName = newName.trim();
        
        if (newName.length() < 3 || newName.length() > 20) {
            addToLobbyLog("Error: El nombre debe tener entre 3 y 20 caracteres.");
            return;
        }
        
        // Verificar que no sea el mismo nombre actual
        PlayerLobbyDataDTO localPlayer = lobbyState.getLocalPlayer();
        if (localPlayer != null && newName.equals(localPlayer.getPlayerName())) {
            addToLobbyLog("Error: Ya tienes ese nombre.");
            nameChangeField.setText("");
            return;
        }
        
        ChangePlayerNameDTO request = new ChangePlayerNameDTO(newName);
        Message message = new Message(MessageType.CHANGE_PLAYER_NAME, lobbyState.getLocalPlayerId(), request);
        connectionManager.sendMessage(message);
        
        addToLobbyLog("Solicitando cambio de nombre a: " + newName);
        nameChangeField.setText("");
        logger.info("Requested name change to: {}", newName);
    }
    
    private void showClassSelector() {
        if (lobbyState.getAvailableClasses() == null || lobbyState.getAvailableClasses().isEmpty()) {
            addToLobbyLog("No hay clases disponibles aún.");
            return;
        }
        
        if (lobbyState.isLocalPlayerReady()) {
            addToLobbyLog("No puedes cambiar de clase mientras estás listo.");
            return;
        }
        
        if (classSelectorWindow != null && classSelectorWindow.hasParent()) {
            classSelectorWindow.remove();
        }
        
        classSelectorWindow = new VisWindow("Selecciona tu Clase");
        classSelectorWindow.setModal(true);
        classSelectorWindow.setMovable(false);
        
        List<ClassInfoDTO> classes = lobbyState.getAvailableClasses();
        if (classes == null) {
            classes = new ArrayList<>();
            logger.warn("Available classes is null, using empty list");
        }
        
        // Crear selector de clase moderno
        VisTable selectorContent = createClassSelectorContent(classes);
        classSelectorWindow.add(selectorContent).pad(20);
        classSelectorWindow.pack();
        classSelectorWindow.centerWindow();
        
        stage.addActor(classSelectorWindow);
        logger.info("Class selector window shown with {} classes", classes.size());
    }
    
    private VisTable createClassSelectorContent(List<ClassInfoDTO> classes) {
        VisTable content = new VisTable();
        content.defaults().pad(10);
        
        VisLabel title = new VisLabel("Selecciona tu clase");
        title.setColor(Color.CYAN);
        content.add(title).center().padBottom(20).row();
        
        // Lista de clases
        VisTable classList = new VisTable();
        classList.defaults().pad(5);
        
        if (classes.isEmpty()) {
            VisLabel emptyLabel = new VisLabel("No hay clases disponibles aun.\nEsperando al servidor...");
            emptyLabel.setWrap(true);
            classList.add(emptyLabel).width(500).center();
        } else {
            for (ClassInfoDTO classInfo : classes) {
                if (classInfo != null) {
                    classList.add(createClassCard(classInfo)).width(500).row();
                }
            }
        }
        
        VisScrollPane scrollPane = new VisScrollPane(classList);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        content.add(scrollPane).width(550).height(500).row();
        
        // Botón cerrar
        VisTextButton closeButton = new VisTextButton("Cerrar");
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (classSelectorWindow != null) {
                    classSelectorWindow.remove();
                }
            }
        });
        content.add(closeButton).width(150).height(40).padTop(10);
        
        return content;
    }
    
    private VisTable createClassCard(ClassInfoDTO classInfo) {
        VisTable card = new VisTable(true);
        card.defaults().pad(5).left();
        
        // Nombre y rol
        String displayName = classInfo.getDisplayName() != null ? classInfo.getDisplayName() : "Clase Desconocida";
        VisLabel nameLabel = new VisLabel(displayName);
        nameLabel.setColor(getClassColor(displayName));
        card.add(nameLabel).left().row();
        
        String role = classInfo.getRole() != null ? classInfo.getRole() : "";
        VisLabel roleLabel = new VisLabel(role);
        roleLabel.setColor(0.7f, 0.7f, 0.7f, 1f);
        card.add(roleLabel).left().padBottom(10).row();
        
        // Descripción
        String description = classInfo.getDescription() != null ? classInfo.getDescription() : "Sin descripción";
        VisLabel descLabel = new VisLabel(description);
        descLabel.setWrap(true);
        card.add(descLabel).width(450).left().padBottom(10).row();
        
        // Stats
        if (classInfo.getBaseStats() != null) {
            VisTable statsTable = new VisTable();
            statsTable.defaults().pad(3);
            
            VisLabel hpLabel = new VisLabel("HP: " + classInfo.getBaseStats().getHp());
            hpLabel.setColor(Color.RED);
            statsTable.add(hpLabel);
            
            VisLabel atkLabel = new VisLabel("ATK: " + classInfo.getBaseStats().getAttack());
            atkLabel.setColor(Color.ORANGE);
            statsTable.add(atkLabel);
            
            VisLabel defLabel = new VisLabel("DEF: " + classInfo.getBaseStats().getDefense());
            defLabel.setColor(Color.BLUE);
            statsTable.add(defLabel);
            
            VisLabel spdLabel = new VisLabel("SPD: " + classInfo.getBaseStats().getSpeed());
            spdLabel.setColor(Color.YELLOW);
            statsTable.add(spdLabel);
            
            card.add(statsTable).left().padBottom(10).row();
        }
        
        // Botón de selección
        VisTextButton selectButton = new VisTextButton("Seleccionar esta clase");
        selectButton.setColor(Color.GREEN);
        selectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onClassSelected(classInfo);
            }
        });
        
        card.add(selectButton).width(200).height(40).center().padTop(10).row();
        
        // Separador
        card.row();
        VisTable separator = new VisTable();
        separator.setBackground("default-pane");
        card.add(separator).fillX().height(2).padTop(10).padBottom(5);
        
        return card;
    }
    
    private void onClassSelected(ClassInfoDTO selectedClass) {
        if (selectedClass == null) {
            logger.warn("Selected class is null, ignoring");
            return;
        }
        
        try {
            // Enviar al servidor
            String classId = selectedClass.getClassId();
            if (classId == null || classId.isEmpty()) {
                logger.error("Class ID is null or empty");
                addToLobbyLog("Error: ID de clase inválido");
                return;
            }
            
            ClassSelectionDTO request = new ClassSelectionDTO(classId);
            Message message = new Message(MessageType.CLASS_SELECTION, lobbyState.getLocalPlayerId(), request);
            connectionManager.sendMessage(message);
            
            String displayName = selectedClass.getDisplayName() != null ? selectedClass.getDisplayName() : classId;
            addToLobbyLog("Seleccionaste: " + displayName);
            logger.info("Selected class: {}", displayName);
        } catch (Exception e) {
            logger.error("Error processing class selection: {}", e.getMessage(), e);
            addToLobbyLog("Error al seleccionar clase: " + e.getMessage());
            return;
        }
        
        if (classSelectorWindow != null) {
            classSelectorWindow.remove();
            classSelectorWindow = null;
        }
    }
    
    private Color getClassColor(String className) {
        if (className == null || className.equals("Sin clase")) {
            return Color.GRAY;
        }
        
        switch (className.toUpperCase()) {
            case "GUARDIAN":
            case "GUARDIÁN":
                return new Color(0.3f, 0.5f, 0.9f, 1);
            case "RANGER":
            case "EXPLORADOR":
                return new Color(0.2f, 0.8f, 0.3f, 1);
            case "MAGE":
            case "MAGO":
                return new Color(0.7f, 0.3f, 0.9f, 1);
            case "CLERIC":
            case "CLÉRIGO":
                return new Color(0.9f, 0.8f, 0.2f, 1);
            case "ROGUE":
            case "PÍCARO":
                return new Color(0.9f, 0.2f, 0.2f, 1);
            default:
                return Color.WHITE;
        }
    }
    
    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar y dibujar UI
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void onLobbyStateChange(LobbyClientState.LobbyStateEvent event) {
        // Actualizar UI en el siguiente frame (thread-safe para LibGDX)
        Gdx.app.postRunnable(() -> {
            // Agregar eventos al log
            switch (event) {
                case PLAYER_JOINED:
                    addToLobbyLog("Un jugador se unió al lobby.");
                    break;
                case PLAYER_LEFT:
                    addToLobbyLog("Un jugador salió del lobby.");
                    break;
                case PLAYER_UPDATED:
                    // No agregar al log para evitar spam
                    break;
                case CHAT_MESSAGE_RECEIVED:
                    // No log, ya se muestra en chat
                    break;
                case STATUS_CHANGED:
                    // Se maneja abajo
                    break;
                case SNAPSHOT_UPDATED:
                    // Actualización de snapshot, no log
                    break;
                case CLASSES_UPDATED:
                    addToLobbyLog("Clases disponibles recibidas del servidor.");
                    break;
                case INVALID_ACTION:
                    String error = lobbyState.consumeLastError();
                    if (error != null) {
                        addToLobbyLog("Error: " + error);
                    }
                    break;
            }
            
            refreshUI();
        });
        
        // Si la partida inició, transicionar
        if (event == LobbyClientState.LobbyStateEvent.STATUS_CHANGED &&
            lobbyState.getStatus() == LobbyStatus.IN_GAME) {
            Gdx.app.postRunnable(() -> {
                logger.info("Game starting, transitioning to game screen");
                if (onGameStart != null) {
                    onGameStart.run();
                }
            });
        }
    }
    
    @Override
    public void dispose() {
        lobbyState.removeListener(this);
        stage.dispose();
        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }
    }
}
