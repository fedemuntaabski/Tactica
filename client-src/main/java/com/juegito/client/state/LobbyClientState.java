package com.juegito.client.state;

import com.juegito.protocol.dto.lobby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Estado del lobby en el cliente.
 * Sincronizado con los mensajes LobbySnapshot del servidor.
 */
public class LobbyClientState {
    private static final Logger logger = LoggerFactory.getLogger(LobbyClientState.class);
    
    private String lobbyId;
    private String hostId;
    private int maxPlayers;
    private LobbyStatus status;
    private LobbyConfigDTO settings;
    
    private List<PlayerLobbyDataDTO> players;
    private List<ChatMessageDTO> chatMessages;
    
    private String localPlayerId;
    private String localPlayerName;
    
    private final List<LobbyStateListener> listeners;
    
    public LobbyClientState() {
        this.players = new ArrayList<>();
        this.chatMessages = new CopyOnWriteArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.status = LobbyStatus.WAITING;
    }
    
    /**
     * Actualiza el estado del lobby con un snapshot del servidor.
     */
    public void updateFromSnapshot(LobbySnapshotDTO snapshot) {
        this.lobbyId = snapshot.getLobbyId();
        this.hostId = snapshot.getHostId();
        this.maxPlayers = snapshot.getMaxPlayers();
        this.status = snapshot.getLobbyStatus();
        this.settings = snapshot.getLobbySettings();
        this.players = new ArrayList<>(snapshot.getPlayers());
        
        notifyListeners(LobbyStateEvent.SNAPSHOT_UPDATED);
        logger.debug("Lobby snapshot updated: {} players, status: {}", players.size(), status);
    }
    
    /**
     * Agrega un jugador al lobby.
     */
    public void addPlayer(PlayerLobbyDataDTO player) {
        players.add(player);
        notifyListeners(LobbyStateEvent.PLAYER_JOINED);
        logger.info("Player joined: {}", player.getPlayerName());
    }
    
    /**
     * Remueve un jugador del lobby.
     */
    public void removePlayer(String playerId) {
        players.removeIf(p -> p.getPlayerId().equals(playerId));
        notifyListeners(LobbyStateEvent.PLAYER_LEFT);
        logger.info("Player left: {}", playerId);
    }
    
    /**
     * Actualiza un jugador del lobby.
     */
    public void updatePlayer(PlayerLobbyDataDTO updatedPlayer) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerId().equals(updatedPlayer.getPlayerId())) {
                players.set(i, updatedPlayer);
                notifyListeners(LobbyStateEvent.PLAYER_UPDATED);
                logger.debug("Player updated: {}", updatedPlayer.getPlayerName());
                return;
            }
        }
    }
    
    /**
     * Agrega un mensaje de chat.
     */
    public void addChatMessage(ChatMessageDTO message) {
        chatMessages.add(message);
        notifyListeners(LobbyStateEvent.CHAT_MESSAGE_RECEIVED);
        logger.debug("Chat message from {}: {}", message.getPlayerName(), message.getMessage());
    }
    
    /**
     * Obtiene el jugador local.
     */
    public PlayerLobbyDataDTO getLocalPlayer() {
        if (localPlayerId == null) {
            return null;
        }
        return players.stream()
            .filter(p -> p.getPlayerId().equals(localPlayerId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Verifica si el jugador local es el host.
     */
    public boolean isLocalPlayerHost() {
        return localPlayerId != null && localPlayerId.equals(hostId);
    }
    
    /**
     * Verifica si el jugador local está listo.
     */
    public boolean isLocalPlayerReady() {
        PlayerLobbyDataDTO localPlayer = getLocalPlayer();
        return localPlayer != null && localPlayer.getConnectionStatus() == ConnectionStatus.READY;
    }
    
    /**
     * Verifica si todos los jugadores están listos (excepto el host).
     */
    public boolean areAllPlayersReady() {
        return players.stream()
            .filter(p -> !p.isHost())
            .allMatch(p -> p.getConnectionStatus() == ConnectionStatus.READY);
    }
    
    /**
     * Obtiene el número de jugadores conectados.
     */
    public int getConnectedPlayerCount() {
        return (int) players.stream()
            .filter(p -> p.getConnectionStatus() != ConnectionStatus.DISCONNECTED)
            .count();
    }
    
    // Listeners
    
    public void addListener(LobbyStateListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(LobbyStateListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(LobbyStateEvent event) {
        for (LobbyStateListener listener : listeners) {
            listener.onLobbyStateChange(event);
        }
    }
    
    // Getters y setters
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public String getHostId() {
        return hostId;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public LobbyStatus getStatus() {
        return status;
    }
    
    public void setStatus(LobbyStatus status) {
        this.status = status;
        notifyListeners(LobbyStateEvent.STATUS_CHANGED);
    }
    
    public LobbyConfigDTO getSettings() {
        return settings;
    }
    
    public List<PlayerLobbyDataDTO> getPlayers() {
        return new ArrayList<>(players);
    }
    
    public List<ChatMessageDTO> getChatMessages() {
        return new ArrayList<>(chatMessages);
    }
    
    public String getLocalPlayerId() {
        return localPlayerId;
    }
    
    public void setLocalPlayerId(String localPlayerId) {
        this.localPlayerId = localPlayerId;
    }
    
    public String getLocalPlayerName() {
        return localPlayerName;
    }
    
    public void setLocalPlayerName(String localPlayerName) {
        this.localPlayerName = localPlayerName;
    }
    
    /**
     * Interfaz para escuchar cambios en el estado del lobby.
     */
    public interface LobbyStateListener {
        void onLobbyStateChange(LobbyStateEvent event);
    }
    
    /**
     * Eventos de cambio de estado del lobby.
     */
    public enum LobbyStateEvent {
        SNAPSHOT_UPDATED,
        PLAYER_JOINED,
        PLAYER_LEFT,
        PLAYER_UPDATED,
        CHAT_MESSAGE_RECEIVED,
        STATUS_CHANGED
    }
}
