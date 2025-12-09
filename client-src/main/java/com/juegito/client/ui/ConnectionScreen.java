package com.juegito.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pantalla inicial de conexión usando Swing.
 * Permite ingresar IP, puerto y nombre del jugador antes de conectar.
 * Implementa KISS: Interfaz simple para conexión inicial.
 */
public class ConnectionScreen {
    private final JFrame frame;
    private JTextField hostField;
    private JTextField portField;
    private JTextField nameField;
    private JButton connectButton;
    private JLabel statusLabel;
    
    private ConnectionCallback callback;
    private final AtomicBoolean connected;
    
    public ConnectionScreen() {
        this.connected = new AtomicBoolean(false);
        this.frame = new JFrame("Juegito - Conectar al Servidor");
        setupUI();
    }
    
    /**
     * Configura la interfaz de usuario.
     */
    private void setupUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titleLabel = new JLabel("CONECTAR AL SERVIDOR");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Campo de host
        mainPanel.add(createFieldPanel("Host:", hostField = new JTextField("localhost", 20)));
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Campo de puerto
        mainPanel.add(createFieldPanel("Puerto:", portField = new JTextField("8080", 20)));
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Campo de nombre
        mainPanel.add(createFieldPanel("Nombre:", nameField = new JTextField("Jugador", 20)));
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Botón de conectar
        connectButton = new JButton("Conectar");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.setFont(new Font("Arial", Font.BOLD, 14));
        connectButton.addActionListener(e -> handleConnect());
        mainPanel.add(connectButton);
        
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Label de estado
        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(Color.RED);
        mainPanel.add(statusLabel);
        
        frame.add(mainPanel);
        
        // Listener para cerrar
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });
    }
    
    /**
     * Crea un panel con label y campo de texto.
     */
    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(80, 25));
        panel.add(jLabel);
        panel.add(field);
        return panel;
    }
    
    /**
     * Maneja el evento de conectar.
     */
    private void handleConnect() {
        String host = hostField.getText().trim();
        String portText = portField.getText().trim();
        String name = nameField.getText().trim();
        
        // Validaciones
        if (host.isEmpty()) {
            showError("El host no puede estar vacío");
            return;
        }
        
        if (name.isEmpty()) {
            showError("El nombre no puede estar vacío");
            return;
        }
        
        int port;
        try {
            port = Integer.parseInt(portText);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showError("Puerto inválido (debe ser entre 1 y 65535)");
            return;
        }
        
        // Deshabilitar UI mientras se conecta
        setUIEnabled(false);
        statusLabel.setText("Conectando...");
        statusLabel.setForeground(Color.BLUE);
        
        // Ejecutar callback en otro thread
        new Thread(() -> {
            if (callback != null) {
                boolean success = callback.onConnect(host, port, name);
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        connected.set(true);
                        statusLabel.setText("¡Conectado!");
                        statusLabel.setForeground(Color.GREEN);
                        
                        // Cerrar ventana después de 1 segundo
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                                SwingUtilities.invokeLater(() -> frame.dispose());
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    } else {
                        showError("Error al conectar al servidor");
                        setUIEnabled(true);
                    }
                });
            }
        }).start();
    }
    
    /**
     * Muestra un mensaje de error.
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }
    
    /**
     * Habilita/deshabilita la UI.
     */
    private void setUIEnabled(boolean enabled) {
        hostField.setEnabled(enabled);
        portField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        connectButton.setEnabled(enabled);
    }
    
    /**
     * Muestra la pantalla.
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
    }
    
    /**
     * Cierra la pantalla.
     */
    public void close() {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
        });
    }
    
    /**
     * Establece el callback de conexión.
     */
    public void setCallback(ConnectionCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Espera hasta que se conecte o se cancele.
     */
    public boolean waitForConnection() {
        while (frame.isVisible() && !connected.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return connected.get();
    }
    
    /**
     * Interface para callback de conexión.
     */
    public interface ConnectionCallback {
        /**
         * Llamado cuando se intenta conectar.
         * 
         * @return true si la conexión fue exitosa, false en caso contrario
         */
        boolean onConnect(String host, int port, String playerName);
        
        /**
         * Llamado cuando se cancela la conexión.
         */
        void onCancel();
    }
}
