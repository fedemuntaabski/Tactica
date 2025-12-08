package com.juegito.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Representa un jugador conectado al servidor.
 * Gestiona la comunicación individual con cada cliente.
 */
public class Player {
    private final String playerId;
    private String playerName;
    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;
    private boolean ready;
    private boolean connected;
    
    public Player(String playerId, String playerName, Socket socket) throws IOException {
        this.playerId = playerId;
        this.playerName = playerName;
        this.socket = socket;
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.ready = false;
        this.connected = true;
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
    
    public boolean isReady() {
        return ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public void sendMessage(String message) {
        output.println(message);
    }
    
    public String receiveMessage() throws IOException {
        return input.readLine();
    }
    
    public void disconnect() {
        try {
            connected = false;
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            // Log error
        }
    }
}
