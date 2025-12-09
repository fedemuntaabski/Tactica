package com.juegito.client.network;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gestiona la conexión de red con el servidor.
 * Maneja socket TCP, envío y recepción de datos.
 */
public class NetworkClient {
    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);
    
    private final String host;
    private final int port;
    private final Gson gson;
    
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private volatile boolean connected;
    
    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new Gson();
        this.connected = false;
    }
    
    /**
     * Establece la conexión con el servidor.
     */
    public void connect() throws IOException {
        if (connected) {
            logger.warn("Already connected to server");
            return;
        }
        
        logger.info("Connecting to server {}:{}", host, port);
        
        socket = new Socket(host, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;
        
        logger.info("Connected successfully");
    }
    
    /**
     * Envía un mensaje al servidor.
     */
    public void sendMessage(Object message) {
        if (!connected) {
            logger.error("Cannot send message: not connected");
            return;
        }
        
        try {
            String json = gson.toJson(message);
            output.println(json);
            logger.debug("Sent message: {}", message.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage());
        }
    }
    
    /**
     * Recibe un mensaje del servidor (bloqueante).
     */
    public String receiveMessage() throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }
        
        String message = input.readLine();
        if (message == null) {
            logger.warn("Server closed connection");
            disconnect();
        }
        return message;
    }
    
    /**
     * Desconecta del servidor.
     */
    public void disconnect() {
        if (!connected) {
            return;
        }
        
        logger.info("Disconnecting from server");
        connected = false;
        
        closeResource(input, "input stream");
        closeResource(output, "output stream");
        closeResource(socket, "socket");
    }
    
    /**
     * Cierra un recurso de forma segura.
     * DRY: Centraliza manejo de errores al cerrar recursos.
     */
    private void closeResource(AutoCloseable resource, String name) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                logger.error("Error closing {}: {}", name, e.getMessage());
            }
        }
    }
    
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
}
