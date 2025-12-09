package com.juegito.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Representa la conexión de red de un jugador.
 * Maneja solo la comunicación individual con cada cliente.
 * El estado del jugador en el lobby está en PlayerLobbyData.
 */
public class Player {
    private final String playerId;
    private String playerName;
    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;
    
    public Player(String playerId, String playerName, Socket socket) throws IOException {
        this.playerId = playerId;
        this.playerName = playerName;
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void sendMessage(String message) {
        output.println(message);
    }
    
    public String receiveMessage() throws IOException {
        return input.readLine();
    }
    
    public void disconnect() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            // Log error
        }
    }
}
