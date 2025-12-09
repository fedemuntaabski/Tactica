package com.juegito.game.lobby;

import com.juegito.protocol.dto.lobby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Estado del lobby en el servidor.
 * Maneja toda la lógica de gestión de jugadores, validaciones y transiciones.
 */
public class LobbyState {
    private static final Logger logger = LoggerFactory.getLogger(LobbyState.class);
    private static final AtomicInteger playerIdCounter = new AtomicInteger(1);
    
    private final String lobbyId;
    private String hostId; // Ya no final, se asigna cuando se une el primer jugador
    private final int maxPlayers;
    private final long createdTimestamp;
    
    private final Map<String, PlayerLobbyData> players;
    private final LobbyConfig lobbySettings;
    private LobbyStatus status;
    
    private final Set<String> usedColors;
    
    public LobbyState() {
        this.lobbyId = UUID.randomUUID().toString();
        this.hostId = null; // Se asignará al primer jugador
        this.maxPlayers = 6;
        this.createdTimestamp = System.currentTimeMillis();
        this.players = new ConcurrentHashMap<>();
        this.lobbySettings = new LobbyConfig();
        this.status = LobbyStatus.WAITING;
        this.usedColors = ConcurrentHashMap.newKeySet();
        
        logger.info("Lobby creado: {} - esperando primer jugador", lobbyId);
    }
    
    /**
     * Agrega un jugador automáticamente al lobby con su ID ya asignado.
     * Usado cuando el servidor crea la conexión directamente.
     */
    public PlayerLobbyData autoAddPlayer(String playerId, String playerName, String ipAddress) {
        PlayerLobbyData player = createPlayer(playerId, playerName, ipAddress);
        logger.info("Jugador {} ({}) agregado automáticamente al lobby {}", playerName, playerId, lobbyId);
        return player;
    }
    
    /**
     * Crea y agrega un jugador al lobby (lógica común - DRY).
     */
    private PlayerLobbyData createPlayer(String playerId, String playerName, String ipAddress) {
        boolean isHost = (hostId == null);
        if (isHost) {
            hostId = playerId;
            logger.info("Primer jugador '{}' ({}) establecido como host", playerName, playerId);
        }
        
        PlayerLobbyData player = new PlayerLobbyData(playerId, playerName, isHost);
        player.setIpAddress(ipAddress);
        players.put(playerId, player);
        
        return player;
    }
    
    /**
     * Valida si un jugador puede unirse al lobby.
     */
    public JoinResponseDTO validateJoin(String playerName, String ipAddress) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return JoinResponseDTO.failure("Nombre de jugador inválido");
        }
        
        if (status != LobbyStatus.WAITING) {
            return JoinResponseDTO.failure("El lobby ya ha iniciado");
        }
        
        if (players.size() >= maxPlayers) {
            return JoinResponseDTO.failure("Lobby lleno");
        }
        
        // Validar nombre duplicado
        boolean nameDuplicated = players.values().stream()
            .anyMatch(p -> playerName.trim().equalsIgnoreCase(p.getPlayerName()));
        
        if (nameDuplicated) {
            return JoinResponseDTO.failure("Ya existe un jugador con ese nombre");
        }
        
        // Validar IP duplicada (opcional, podría configurarse)
        boolean ipDuplicated = players.values().stream()
            .anyMatch(p -> ipAddress.equals(p.getIpAddress()));
        
        if (ipDuplicated) {
            logger.warn("IP duplicada detectada: {}", ipAddress);
            // Por ahora permitir, pero loggear
        }
        
        return null; // null significa validación exitosa
    }
    
    /**
     * Agrega un nuevo jugador al lobby.
     */
    public PlayerLobbyData addPlayer(String playerName, String ipAddress) {
        String playerId = "player_" + playerIdCounter.getAndIncrement();
        PlayerLobbyData player = createPlayer(playerId, playerName, ipAddress);
        logger.info("Jugador {} ({}) se unió al lobby {}", playerName, playerId, lobbyId);
        return player;
    }
    
    /**
     * Remueve un jugador del lobby.
     */
    public PlayerLobbyData removePlayer(String playerId) {
        PlayerLobbyData player = players.remove(playerId);
        if (player != null) {
            // Liberar el color si lo tenía
            if (player.getSelectedColor() != null) {
                usedColors.remove(player.getSelectedColor());
            }
            logger.info("Jugador {} salió del lobby {}", player.getPlayerName(), lobbyId);
        }
        return player;
    }
    
    /**
     * Actualiza el estado ready de un jugador.
     */
    public boolean updateReadyStatus(String playerId, boolean ready) {
        PlayerLobbyData player = players.get(playerId);
        if (player == null) {
            return false;
        }
        
        ConnectionStatus newStatus = ready ? ConnectionStatus.READY : ConnectionStatus.CONNECTED;
        player.setConnectionStatus(newStatus);
        
        logger.info("Jugador {} cambió ready a: {}", player.getPlayerName(), ready);
        return true;
    }
    
    /**
     * Actualiza la clase seleccionada de un jugador.
     */
    public boolean updateClass(String playerId, String classId) {
        PlayerLobbyData player = players.get(playerId);
        if (player == null) {
            return false;
        }
        
        // Validar que la clase sea válida
        if (!isValidClass(classId)) {
            logger.warn("Clase inválida solicitada: {}", classId);
            return false;
        }
        
        player.setSelectedClass(classId);
        logger.info("Jugador {} seleccionó clase: {}", player.getPlayerName(), classId);
        return true;
    }
    
    /**
     * Actualiza el color seleccionado de un jugador.
     */
    public boolean updateColor(String playerId, String color) {
        PlayerLobbyData player = players.get(playerId);
        if (player == null) {
            return false;
        }
        
        // Validar que el color no esté en uso
        if (usedColors.contains(color) && !color.equals(player.getSelectedColor())) {
            logger.warn("Color {} ya en uso", color);
            return false;
        }
        
        // Liberar el color anterior
        if (player.getSelectedColor() != null) {
            usedColors.remove(player.getSelectedColor());
        }
        
        // Asignar el nuevo color
        player.setSelectedColor(color);
        usedColors.add(color);
        
        logger.info("Jugador {} seleccionó color: {}", player.getPlayerName(), color);
        return true;
    }
    
    /**
     * Actualiza el nombre de un jugador.
     */
    public boolean updatePlayerName(String playerId, String newName) {
        PlayerLobbyData player = players.get(playerId);
        if (player == null) {
            logger.warn("Intento de cambiar nombre de jugador inexistente: {}", playerId);
            return false;
        }
        
        if (player.getConnectionStatus() == ConnectionStatus.DISCONNECTED) {
            logger.warn("Intento de cambiar nombre de jugador desconectado: {}", playerId);
            return false;
        }
        
        // Validar que no exista otro jugador con ese nombre
        boolean nameDuplicated = players.values().stream()
            .filter(p -> !p.getPlayerId().equals(playerId)) // Excluir al mismo jugador
            .anyMatch(p -> newName.equalsIgnoreCase(p.getPlayerName()));
        
        if (nameDuplicated) {
            logger.warn("Intento de cambiar a nombre duplicado: {}", newName);
            return false;
        }
        
        String oldName = player.getPlayerName();
        player.setPlayerName(newName);
        logger.info("Jugador {} cambió nombre de '{}' a '{}'", playerId, oldName, newName);
        return true;
    }
    
    /**
     * Marca un jugador como desconectado.
     */
    public void markDisconnected(String playerId) {
        PlayerLobbyData player = players.get(playerId);
        if (player != null) {
            player.setConnectionStatus(ConnectionStatus.DISCONNECTED);
            logger.warn("Jugador {} desconectado", player.getPlayerName());
        }
    }
    
    /**
     * Actualiza la configuración del lobby (solo el host).
     */
    public boolean updateSettings(String requesterId, LobbyConfig newSettings) {
        if (!hostId.equals(requesterId)) {
            logger.warn("Jugador no-host {} intentó cambiar configuración", requesterId);
            return false;
        }
        
        if (status != LobbyStatus.WAITING) {
            logger.warn("Intento de cambiar configuración con lobby no en WAITING");
            return false;
        }
        
        lobbySettings.update(newSettings);
        logger.info("Configuración del lobby actualizada por el host");
        return true;
    }
    
    /**
     * Valida si se puede iniciar la partida.
     */
    public String validateStartMatch(String requesterId) {
        if (!hostId.equals(requesterId)) {
            return "Solo el host puede iniciar la partida";
        }
        
        if (status != LobbyStatus.WAITING) {
            return "El lobby ya ha iniciado";
        }
        
        if (players.isEmpty()) {
            return "No hay jugadores en el lobby";
        }
        
        // Verificar que todos estén ready (INCLUYENDO el host)
        boolean allReady = players.values().stream()
            .allMatch(p -> p.getConnectionStatus() == ConnectionStatus.READY);
        
        if (!allReady) {
            return "No todos los jugadores están listos";
        }
        
        return null; // null = validación exitosa
    }
    
    /**
     * Inicia la transición a la partida.
     */
    public void startMatch() {
        status = LobbyStatus.STARTING;
        logger.info("Lobby {} iniciando partida con {} jugadores", lobbyId, players.size());
    }
    
    /**
     * Completa la transición a partida en curso.
     */
    public void transitionToInGame() {
        status = LobbyStatus.IN_GAME;
        logger.info("Lobby {} transicionó a IN_GAME", lobbyId);
    }
    
    /**
     * Genera el snapshot actual del lobby para enviar a los clientes.
     */
    public LobbySnapshotDTO createSnapshot() {
        LobbySnapshotDTO snapshot = new LobbySnapshotDTO();
        snapshot.setLobbyId(lobbyId);
        snapshot.setHostId(hostId);
        snapshot.setMaxPlayers(maxPlayers);
        snapshot.setCreatedTimestamp(createdTimestamp);
        snapshot.setLobbyStatus(status);
        snapshot.setLobbySettings(lobbySettings.toDTO());
        
        List<PlayerLobbyDataDTO> playerDTOs = players.values().stream()
            .map(PlayerLobbyData::toDTO)
            .collect(Collectors.toList());
        snapshot.setPlayers(playerDTOs);
        
        return snapshot;
    }
    
    /**
     * Genera la configuración final para el inicio de partida.
     */
    public StartMatchDTO createStartMatchMessage() {
        long seed = lobbySettings.isRandomSeed() 
            ? System.currentTimeMillis() 
            : lobbySettings.getCustomSeed();
        
        List<PlayerLobbyDataDTO> playerList = players.values().stream()
            .map(PlayerLobbyData::toDTO)
            .collect(Collectors.toList());
        
        return new StartMatchDTO(seed, lobbySettings.toDTO(), playerList);
    }
    
    private boolean isValidClass(String classId) {
        if (classId == null || classId.trim().isEmpty()) {
            return false;
        }
        
        // Lista de clases válidas
        Set<String> validClasses = Set.of(
            "warrior", "guerrero",
            "mage", "mago",
            "ranger", "explorador",
            "rogue", "pícaro",
            "engineer", "ingeniero",
            "healer", "sanador"
        );
        
        return validClasses.contains(classId.toLowerCase());
    }
    
    // Getters
    
    public String getLobbyId() {
        return lobbyId;
    }
    
    public String getHostId() {
        return hostId;
    }
    
    public LobbyStatus getStatus() {
        return status;
    }
    
    public Collection<PlayerLobbyData> getPlayers() {
        return players.values();
    }
    
    public PlayerLobbyData getPlayer(String playerId) {
        return players.get(playerId);
    }
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public LobbyConfig getSettings() {
        return lobbySettings;
    }
    
    /**
     * Verifica si el lobby está lleno.
     */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
}
