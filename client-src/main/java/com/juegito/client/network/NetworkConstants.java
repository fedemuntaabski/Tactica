package com.juegito.client.network;

/**
 * Constantes de configuración de red.
 * Sigue principio DRY: evita magic numbers duplicados.
 */
public final class NetworkConstants {
    
    // Prevenir instanciación
    private NetworkConstants() {}
    
    // === HEARTBEAT ===
    public static final int HEARTBEAT_INTERVAL_MS = 5000;
    
    // === TIMEOUTS ===
    public static final int CONNECTION_TIMEOUT_MS = 10000;
    public static final int READ_TIMEOUT_MS = 5000;
    
    // === RETRY ===
    public static final int MAX_RECONNECT_ATTEMPTS = 3;
    public static final int RECONNECT_DELAY_MS = 2000;
}
