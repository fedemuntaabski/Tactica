package com.juegito.client.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.juegito.client.network.ConnectionManager;
import com.juegito.client.state.LobbyClientState;
import com.juegito.protocol.Message;
import com.juegito.protocol.MessageType;
import com.juegito.protocol.dto.lobby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Pantalla del lobby del juego usando LibGDX.
 * Muestra lista de jugadores conectados, log de eventos, chat y controles.
 */
public class LobbyScreen extends ScreenAdapter implements LobbyClientState.LobbyStateListener {
    private static final Logger logger = LoggerFactory.getLogger(LobbyScreen.class);
    
    private final LobbyClientState lobbyState;
    private final ConnectionManager connectionManager;
    private final Runnable onGameStart;
    
    private Stage stage;
    private Skin skin;
    private Table rootTable;
    
    // Componentes UI
    private Table playerListTable;
    private Table lobbyLogTable;
    private TextField playerNameField;
    private TextField chatInputField;
    private ScrollPane chatScrollPane;
    private Table chatTable;
    private TextButton readyButton;
    private TextButton startButton;
    private TextButton leaveButton;
    private TextButton sendChatButton;
    private TextButton changeNameButton;
    private Label statusLabel;
    
    private SpriteBatch batch;
    private BitmapFont font;
    
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
        
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        // Crear skin básico (UI styling)
        skin = createSkin();
        
        // Crear UI
        createUI();
        
        // Actualizar UI con datos actuales
        refreshUI();
    }
    
    /**
     * Crea un skin básico para los componentes UI.
     */
    private Skin createSkin() {
        Skin skin = new Skin();
        
        // Fuente
        skin.add("default", new BitmapFont());
        
        // Estilos para Label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
        
        // Estilos para TextButton
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;
        skin.add("default", buttonStyle);
        
        // Estilos para TextField
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.messageFontColor = Color.GRAY;
        skin.add("default", textFieldStyle);
        
        // Estilos para ScrollPane
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        skin.add("default", scrollPaneStyle);
        
        return skin;
    }
    
    /**
     * Crea la interfaz de usuario del lobby.
     */
    private void createUI() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);
        stage.addActor(rootTable);
        
        // Título y cambio de nombre
        Label titleLabel = new Label("LOBBY DEL JUEGO", skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(Color.GOLD);
        rootTable.add(titleLabel).colspan(2).padBottom(10);
        rootTable.row();
        
        // Campo de cambio de nombre
        Table nameChangePanel = new Table();
        nameChangePanel.add(new Label("Tu nombre:", skin)).padRight(10);
        
        playerNameField = new TextField("", skin);
        playerNameField.setMessageText("Ingresa tu nombre");
        playerNameField.setMaxLength(20);
        nameChangePanel.add(playerNameField).width(200).padRight(10);
        
        changeNameButton = new TextButton("Cambiar", skin);
        changeNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changePlayerName();
            }
        });
        nameChangePanel.add(changeNameButton).width(100);
        
        rootTable.add(nameChangePanel).colspan(2).padBottom(20);
        rootTable.row();
        
        // Panel izquierdo: Lista de jugadores y log
        playerListTable = new Table();
        playerListTable.top().left();
        ScrollPane playerListScroll = new ScrollPane(playerListTable, skin);
        playerListScroll.setFadeScrollBars(false);
        
        Table playerPanel = new Table();
        Label playerLabel = new Label("JUGADORES CONECTADOS", skin);
        playerLabel.setFontScale(1.3f);
        playerPanel.add(playerLabel).padBottom(15);
        playerPanel.row();
        playerPanel.add(playerListScroll).width(400).height(300).fillX();
        playerPanel.row();
        
        // Log del lobby
        lobbyLogTable = new Table();
        lobbyLogTable.top().left();
        ScrollPane lobbyLogScroll = new ScrollPane(lobbyLogTable, skin);
        lobbyLogScroll.setFadeScrollBars(false);
        
        Label logLabel = new Label("LOG DE EVENTOS", skin);
        logLabel.setFontScale(1.1f);
        playerPanel.add(logLabel).padTop(20).padBottom(10);
        playerPanel.row();
        playerPanel.add(lobbyLogScroll).width(400).height(150).fillX();
        
        rootTable.add(playerPanel).top().left().padRight(30);
        
        // Panel derecho: Chat
        chatTable = new Table();
        chatTable.top().left();
        chatScrollPane = new ScrollPane(chatTable, skin);
        chatScrollPane.setFadeScrollBars(false);
        
        chatInputField = new TextField("", skin);
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
        
        sendChatButton = new TextButton("Enviar", skin);
        sendChatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendChatMessage(chatInputField.getText());
                chatInputField.setText("");
            }
        });
        
        Table chatPanel = new Table();
        Label chatLabel = new Label("CHAT DEL LOBBY", skin);
        chatLabel.setFontScale(1.3f);
        chatPanel.add(chatLabel).padBottom(15);
        chatPanel.row();
        chatPanel.add(chatScrollPane).width(400).height(370).fillX();
        chatPanel.row();
        
        Table chatInputPanel = new Table();
        chatInputPanel.add(chatInputField).width(300).height(40).padRight(10);
        chatInputPanel.add(sendChatButton).width(90).height(40);
        chatPanel.add(chatInputPanel).padTop(10);
        
        rootTable.add(chatPanel).top().left();
        rootTable.row();
        
        // Panel inferior: Controles
        Table controlsTable = new Table();
        controlsTable.pad(20);
        
        // Botón Ready (solo para jugadores no-host)
        readyButton = new TextButton("LISTO", skin);
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleReady();
            }
        });
        
        // Botón Start (solo para host)
        startButton = new TextButton("INICIAR PARTIDA", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                requestStartMatch();
            }
        });
        
        // Botón Leave
        leaveButton = new TextButton("SALIR DEL LOBBY", skin);
        leaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                leaveLobby();
            }
        });
        
        controlsTable.add(readyButton).width(180).height(60).padRight(15);
        controlsTable.add(startButton).width(200).height(60).padRight(15);
        controlsTable.add(leaveButton).width(180).height(60);
        
        rootTable.add(controlsTable).colspan(2).padTop(30);
        rootTable.row();
        
        // Label de estado
        statusLabel = new Label("Esperando jugadores...", skin);
        statusLabel.setFontScale(1.2f);
        statusLabel.setColor(Color.YELLOW);
        rootTable.add(statusLabel).colspan(2).padTop(15);
    }
    
    /**
     * Actualiza la UI con los datos actuales del lobby.
     */
    private void refreshUI() {
        // Actualizar nombre del jugador local
        if (lobbyState.getLocalPlayerName() != null && playerNameField.getText().isEmpty()) {
            playerNameField.setText(lobbyState.getLocalPlayerName());
        }
        
        // Actualizar lista de jugadores
        playerListTable.clear();
        
        // Encabezado
        Label headerName = new Label("NOMBRE", skin);
        headerName.setFontScale(1.1f);
        headerName.setColor(Color.CYAN);
        
        Label headerStatus = new Label("ESTADO", skin);
        headerStatus.setFontScale(1.1f);
        headerStatus.setColor(Color.CYAN);
        
        playerListTable.add(headerName).width(220).padBottom(10).left();
        playerListTable.add(headerStatus).width(140).padBottom(10).left();
        playerListTable.row();
        
        // Separador
        playerListTable.add(new Label("─────────────────────", skin)).colspan(2).padBottom(5);
        playerListTable.row();
        
        // Jugadores
        for (PlayerLobbyDataDTO player : lobbyState.getPlayers()) {
            Label nameLabel = new Label(player.getPlayerName(), skin);
            nameLabel.setFontScale(1.2f);
            
            if (player.isHost()) {
                nameLabel.setText("★ " + player.getPlayerName() + " (HOST)");
                nameLabel.setColor(Color.GOLD);
            } else if (player.getPlayerId().equals(lobbyState.getLocalPlayerId())) {
                nameLabel.setText(player.getPlayerName() + " (Tú)");
                nameLabel.setColor(Color.GREEN);
            }
            
            Label statusLabelItem = new Label(getStatusText(player.getConnectionStatus()), skin);
            statusLabelItem.setFontScale(1.2f);
            statusLabelItem.setColor(getStatusColor(player.getConnectionStatus()));
            
            playerListTable.add(nameLabel).left().padBottom(8);
            playerListTable.add(statusLabelItem).left().padBottom(8);
            playerListTable.row();
        }
        
        // Mostrar contador de jugadores
        playerListTable.row();
        Label countLabel = new Label(
            String.format("Jugadores: %d/%d", 
                lobbyState.getConnectedPlayerCount(), 
                lobbyState.getMaxPlayers()),
            skin
        );
        countLabel.setFontScale(1.1f);
        countLabel.setColor(Color.LIGHT_GRAY);
        playerListTable.add(countLabel).colspan(2).padTop(15).left();
        
        // Actualizar log del lobby
        lobbyLogTable.clear();
        for (String logEntry : lobbyLog) {
            Label logLabel = new Label(logEntry, skin);
            logLabel.setWrap(true);
            logLabel.setAlignment(Align.left);
            lobbyLogTable.add(logLabel).width(380).left().padBottom(5);
            lobbyLogTable.row();
        }
        
        // Actualizar chat
        chatTable.clear();
        for (ChatMessageDTO msg : lobbyState.getChatMessages()) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date(msg.getTimestamp()));
            String text = "[" + timestamp + "] " + msg.getPlayerName() + ": " + msg.getMessage();
            Label chatLabel = new Label(text, skin);
            chatLabel.setWrap(true);
            chatLabel.setAlignment(Align.left);
            
            // Resaltar mensajes propios
            if (msg.getPlayerId().equals(lobbyState.getLocalPlayerId())) {
                chatLabel.setColor(Color.LIME);
            }
            
            chatTable.add(chatLabel).width(380).left().padBottom(4);
            chatTable.row();
        }
        chatScrollPane.layout();
        chatScrollPane.scrollTo(0, 0, 0, 0);
        
        // Actualizar visibilidad y estado de botones
        boolean isHost = lobbyState.isLocalPlayerHost();
        boolean isReady = lobbyState.isLocalPlayerReady();
        boolean allReady = lobbyState.areAllPlayersReady();
        int playerCount = lobbyState.getConnectedPlayerCount();
        
        readyButton.setVisible(!isHost);
        startButton.setVisible(isHost);
        startButton.setDisabled(!allReady || playerCount < 1);
        
        if (isReady) {
            readyButton.setText("NO LISTO");
            readyButton.setColor(Color.GREEN);
        } else {
            readyButton.setText("LISTO");
            readyButton.setColor(Color.WHITE);
        }
        
        // Actualizar mensaje de estado
        updateStatusMessage();
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
    
    private void changePlayerName() {
        String newName = playerNameField.getText().trim();
        if (newName.isEmpty() || newName.length() < 3) {
            addToLobbyLog("El nombre debe tener al menos 3 caracteres.");
            return;
        }
        
        if (newName.length() > 20) {
            newName = newName.substring(0, 20);
            playerNameField.setText(newName);
        }
        
        // Enviar solicitud de cambio de nombre al servidor
        ChangePlayerNameDTO request = new ChangePlayerNameDTO(newName);
        Message message = new Message(MessageType.CHANGE_PLAYER_NAME, lobbyState.getLocalPlayerId(), request);
        connectionManager.sendMessage(message);
        
        addToLobbyLog("Solicitando cambio de nombre a: " + newName);
        logger.info("Requested name change to: {}", newName);
    }
    
    private void toggleReady() {
        boolean newReady = !lobbyState.isLocalPlayerReady();
        ReadyStatusChangeDTO request = new ReadyStatusChangeDTO(newReady);
        Message message = new Message(MessageType.READY_STATUS_CHANGE, lobbyState.getLocalPlayerId(), request);
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
        skin.dispose();
        batch.dispose();
        font.dispose();
    }
}
