# Documentación del Servidor de Juego - Juegito

**Fecha:** 8 de diciembre de 2025  
**Versión:** 1.0.0  
**Autor:** GitHub Copilot

---

## 1. Descripción General

Este documento detalla la implementación de la infraestructura del servidor para el juego "Juegito", desarrollado en Java siguiendo las mejores prácticas de programación orientada a objetos.

### 1.1 Objetivos

- Crear un servidor robusto capaz de gestionar múltiples jugadores simultáneos
- Implementar un protocolo de comunicación eficiente y extensible
- Gestionar el ciclo completo del juego desde el lobby hasta la finalización
- Manejar desconexiones y errores de forma elegante

---

## 2. Arquitectura del Sistema

### 2.1 Principios de Diseño Aplicados

**KISS (Keep It Simple, Stupid):**
- Cada clase tiene una responsabilidad clara y bien definida
- Métodos concisos que realizan una única tarea
- Evitación de complejidad innecesaria

**DRY (Don't Repeat Yourself):**
- Reutilización de código mediante abstracciones apropiadas
- DTOs compartidos para transferencia de datos
- Validación centralizada en `ActionValidator`

**Bajo Acoplamiento:**
- Las clases se comunican mediante interfaces claras
- Uso de DTOs para desacoplar el protocolo de la lógica interna
- Inyección de dependencias donde es apropiado

**Alta Cohesión:**
- `GameServer`: Gestión de conexiones y orquestación
- `GameState`: Estado del mundo del juego
- `Lobby`: Gestión de jugadores pre-partida
- `ActionValidator`: Validación de acciones
- `ClientHandler`: Comunicación individual con clientes

### 2.2 Estructura de Paquetes

```
com.juegito
├── server           # Clases del servidor
│   ├── GameServer.java
│   └── ClientHandler.java
├── game             # Lógica del juego
│   ├── GameState.java
│   ├── Lobby.java
│   └── ActionValidator.java
├── model            # Modelos de dominio
│   └── Player.java
└── protocol         # Protocolo de comunicación
    ├── Message.java
    ├── MessageType.java
    └── dto          # Data Transfer Objects
        ├── PlayerConnectDTO.java
        ├── PlayerInfoDTO.java
        ├── LobbyStateDTO.java
        ├── GameStateDTO.java
        └── PlayerActionDTO.java
```

---

## 3. Componentes Principales

### 3.1 GameServer

**Responsabilidades:**
- Aceptar conexiones de clientes en el puerto configurado
- Gestionar el ciclo de vida del lobby y del juego
- Coordinar la comunicación entre jugadores
- Orquestar el flujo del juego

**Características clave:**
- Uso de `ExecutorService` para manejo concurrente de clientes
- Thread-safe mediante sincronización apropiada
- Configurable vía parámetros (puerto, min/max jugadores)

**Flujo de ejecución:**
1. Inicialización del servidor en puerto especificado
2. Aceptación de conexiones hasta que el lobby esté lleno
3. Espera de que todos los jugadores estén listos
4. Inicio automático del juego
5. Gestión del ciclo de turnos
6. Manejo de desconexiones y finalización

### 3.2 ClientHandler

**Responsabilidades:**
- Gestionar la comunicación bidireccional con un cliente
- Procesar mensajes entrantes
- Notificar al servidor de eventos relevantes

**Características clave:**
- Ejecuta en su propio hilo por cada jugador
- Deserialización automática de mensajes JSON
- Manejo robusto de errores de conexión

### 3.3 GameState

**Responsabilidades:**
- Mantener el estado completo del mundo del juego
- Gestionar el orden y ciclo de turnos
- Proveer acceso thread-safe al estado

**Características clave:**
- Uso de `ConcurrentHashMap` para thread-safety
- Estado encapsulado con acceso controlado
- Generación de DTOs para transmisión

**Datos gestionados:**
- Orden de jugadores (aleatorizado al inicio)
- Índice del turno actual
- Número de turno global
- Estado del mundo (mapa genérico extensible)
- Flag de juego activo

### 3.4 Lobby

**Responsabilidades:**
- Gestionar jugadores antes del inicio del juego
- Validar condiciones de inicio
- Controlar capacidad mínima y máxima

**Características clave:**
- Thread-safe mediante sincronización
- Validación de estado "ready" de jugadores
- Límites configurables

### 3.5 ActionValidator

**Responsabilidades:**
- Validar que las acciones sean válidas en el contexto actual
- Verificar turnos de jugadores
- Validar tipos de acción conocidos

**Características clave:**
- Separación de concerns: validación vs procesamiento
- Retorno de resultados estructurados
- Extensible para nuevos tipos de acción

**Tipos de acción soportados:**
- MOVE: Movimiento
- ATTACK: Ataque
- DEFEND: Defensa
- SKIP: Pasar turno

---

## 4. Protocolo de Comunicación

### 4.1 Formato de Mensajes

Todos los mensajes utilizan JSON con la siguiente estructura base:

```json
{
  "type": "MESSAGE_TYPE",
  "timestamp": 1234567890,
  "senderId": "player-uuid",
  "payload": { ... }
}
```

### 4.2 Tipos de Mensaje

**Conexión:**
- `PLAYER_CONNECT`: Nuevo jugador conectado
- `PLAYER_DISCONNECT`: Jugador desconectado

**Lobby:**
- `LOBBY_STATE`: Estado actual del lobby
- `START_GAME`: Inicio de partida

**Juego:**
- `GAME_STATE`: Estado completo del juego
- `PLAYER_ACTION`: Acción de jugador
- `TURN_START`: Inicio de turno
- `TURN_END`: Fin de turno

**Validación:**
- `ACTION_VALID`: Acción aceptada
- `ACTION_INVALID`: Acción rechazada

**Sistema:**
- `ERROR`: Mensaje de error
- `PING`/`PONG`: Keepalive

### 4.3 DTOs Definidos

**PlayerConnectDTO:**
- `playerName`: Nombre del jugador
- `playerId`: ID único

**LobbyStateDTO:**
- `players`: Lista de jugadores
- `maxPlayers`: Capacidad máxima
- `gameStarted`: Flag de inicio

**GameStateDTO:**
- `currentTurnPlayerId`: ID del jugador con turno
- `turnNumber`: Número de turno actual
- `worldState`: Mapa del estado del mundo

**PlayerActionDTO:**
- `actionType`: Tipo de acción
- `actionData`: Datos específicos de la acción

---

## 5. Gestión de Jugadores

### 5.1 Ciclo de Vida del Jugador

1. **Conexión:**
   - Cliente se conecta al socket del servidor
   - Se crea instancia de `Player` con ID único
   - Se asigna nombre automático (Player_N)
   - Se agrega al lobby si hay espacio

2. **Lobby:**
   - Jugador recibe estado del lobby
   - Puede marcarse como "ready"
   - Espera a que todos estén listos

3. **Juego:**
   - Orden de turnos aleatorizado
   - Recibe notificación cuando es su turno
   - Envía acciones al servidor
   - Recibe actualizaciones de estado

4. **Desconexión:**
   - Detección automática (socket cerrado o null)
   - Notificación a otros jugadores
   - Limpieza de recursos
   - Si es su turno, se avanza automáticamente
   - Si quedan menos del mínimo, juego finaliza

### 5.2 Manejo de Conexiones

- Cada jugador tiene su `ClientHandler` en hilo separado
- Pool de threads gestionado por `ExecutorService`
- Escritura/lectura mediante `PrintWriter` y `BufferedReader`
- Auto-flush habilitado para envío inmediato

---

## 6. Ciclo de Turnos

### 6.1 Inicialización

Al iniciar el juego:
1. Se baraja el orden de jugadores aleatoriamente
2. Se establece el índice de turno en 0
3. Se notifica al primer jugador

### 6.2 Flujo de Turno

1. Jugador actual recibe `TURN_START`
2. Envía `PLAYER_ACTION` al servidor
3. Servidor valida la acción
4. Si es válida:
   - Se procesa la acción
   - Se envía `TURN_END` a todos
   - Se actualiza el estado
   - Se avanza al siguiente jugador
5. Si es inválida:
   - Se notifica al jugador
   - El turno continúa

### 6.3 Avance de Turno

```
currentIndex = (currentIndex + 1) % playerCount
if (currentIndex == 0) turnNumber++
```

Esto garantiza rotación circular y cuenta de turnos globales.

---

## 7. Gestión de Estado

### 7.1 Estado del Mundo

El `GameState` mantiene un mapa flexible (`Map<String, Object>`) que permite almacenar cualquier dato del juego:

```java
worldState.put("entities", entityList);
worldState.put("score", scoreMap);
worldState.put("customData", customObject);
```

**Ventajas:**
- Extensible sin modificar la estructura
- Serializable directamente a JSON
- Thread-safe mediante `ConcurrentHashMap`

### 7.2 Sincronización

El estado se sincroniza mediante:
- Broadcasting después de cada acción válida
- Envío del estado completo en cada update
- Clientes reconstruyen su vista local

**Consideraciones:**
- Para juegos complejos, considerar delta updates
- Para estado grande, implementar compresión
- Para baja latencia, optimizar frecuencia de sync

---

## 8. Validación de Acciones

### 8.1 Niveles de Validación

1. **Validación de formato:**
   - Mensaje JSON bien formado
   - Tipo de mensaje válido
   - Campos requeridos presentes

2. **Validación de contexto:**
   - Juego está activo
   - Es el turno del jugador
   - Acción permitida en estado actual

3. **Validación de lógica:**
   - Tipo de acción conocido
   - Datos de acción válidos (extensible)

### 8.2 Respuestas de Validación

**Acción válida:**
```json
{
  "type": "ACTION_VALID",
  "payload": { ...actionData }
}
```

**Acción inválida:**
```json
{
  "type": "ACTION_INVALID",
  "payload": {
    "reason": "Not player's turn"
  }
}
```

---

## 9. Manejo de Errores y Desconexiones

### 9.1 Detección de Desconexión

- `readLine()` retorna `null` cuando socket se cierra
- `IOException` capturada al leer/escribir
- Timeout configurable (futuro enhancement)

### 9.2 Acciones al Desconectar

1. Marcar jugador como desconectado
2. Cerrar recursos (streams, socket)
3. Remover del lobby
4. Notificar a otros jugadores
5. Si es su turno, avanzar automáticamente
6. Si quedan menos jugadores del mínimo, terminar juego

### 9.3 Manejo de Errores

- Logging extensivo con SLF4J/Logback
- Errores no fatales loggeados y continuación
- Errores fatales resultan en shutdown graceful
- Validación de entrada para prevenir crashes

---

## 10. Configuración y Deployment

### 10.1 Configuración del Servidor

**Parámetros de línea de comandos:**
```bash
java -jar game-server.jar [port] [minPlayers] [maxPlayers]
```

**Valores por defecto:**
- Puerto: 8080
- Jugadores mínimos: 2
- Jugadores máximos: 4

**Archivo de propiedades:**
- `server.properties` en resources
- Timeouts configurables
- Extensible para más parámetros

### 10.2 Dependencias

**Gson (2.10.1):**
- Serialización/deserialización JSON
- Automática de objetos Java

**SLF4J (2.0.9) + Logback (1.4.11):**
- Logging estructurado
- Niveles configurables
- Salida a consola y archivo

### 10.3 Compilación

```bash
mvn clean package
```

Genera: `target/game-server-1.0.0.jar`

### 10.4 Ejecución

```bash
java -jar target/game-server-1.0.0.jar
```

---

## 11. Extensibilidad

### 11.1 Agregar Nuevos Tipos de Mensaje

1. Agregar enum en `MessageType`
2. Crear DTO si necesita payload específico
3. Agregar case en `ClientHandler.processMessage()`
4. Implementar lógica en `GameServer`

### 11.2 Agregar Nuevas Validaciones

1. Extender `ActionValidator.validateActionType()`
2. Agregar tipo de acción al switch
3. Implementar lógica de validación específica

### 11.3 Extender Estado del Mundo

Simplemente agregar nuevas entradas al mapa:
```java
gameState.updateWorldState("newFeature", data);
```

### 11.4 Personalizar Lógica de Juego

Actualmente el servidor es agnóstico a la lógica específica del juego. Para implementar reglas:

1. Crear clases en paquete `com.juegito.game.rules`
2. Inyectar en `GameServer`
3. Invocar durante `processValidAction()`

---

## 12. Consideraciones de Seguridad

### 12.1 Implementadas

- Validación de entrada antes de procesar
- Límite de jugadores para prevenir DoS
- Cierre de conexiones rechazadas
- Thread-safety en estructuras compartidas

### 12.2 Recomendaciones Futuras

- Autenticación de jugadores
- Encriptación de comunicación (TLS/SSL)
- Rate limiting de mensajes
- Validación de tamaño de payload
- Sanitización de nombres de jugador

---

## 13. Logging

### 13.1 Niveles Utilizados

- **DEBUG:** Detalles de flujo (turnos, mensajes)
- **INFO:** Eventos importantes (conexiones, inicio juego)
- **WARN:** Situaciones anómalas (lobby lleno, acción inválida)
- **ERROR:** Errores que requieren atención

### 13.2 Formato

```
HH:mm:ss.SSS [thread] LEVEL logger - message
```

### 13.3 Salidas

- Consola: Para desarrollo
- Archivo (`logs/server.log`): Para producción

---

## 14. Testing

### 14.1 Recomendaciones

**Unit Tests:**
- `ActionValidator`: Validación de reglas
- `GameState`: Gestión de estado
- `Lobby`: Lógica de lobby

**Integration Tests:**
- Flujo completo de conexión
- Ciclo de turnos
- Manejo de desconexiones

**Herramientas sugeridas:**
- JUnit 5
- Mockito para mocks
- Cliente de prueba automatizado

---

## 15. Mejoras Futuras

### 15.1 Performance

- Implementar pool de objetos para reducir GC
- Delta updates en lugar de estado completo
- Compresión de mensajes grandes
- Métricas de performance

### 15.2 Funcionalidad

- Persistencia de partidas (base de datos)
- Reconexión de jugadores
- Espectadores
- Chat entre jugadores
- Matchmaking automático
- Rankings y estadísticas

### 15.3 Robustez

- Health checks
- Heartbeat automático
- Timeout de turnos
- Recovery de crashes
- Backup de estado

---

## 16. Decisiones de Diseño

### 16.1 ¿Por qué Sockets en lugar de WebSockets?

- Simplicidad para implementación inicial
- Menor overhead para juego en tiempo real
- Facilita testing con herramientas estándar
- Migración a WebSockets posible sin cambio de lógica

### 16.2 ¿Por qué JSON?

- Legible y debuggeable
- Amplio soporte en todas las plataformas
- Serialización automática con Gson
- Para mayor performance, considerar Protocol Buffers

### 16.3 ¿Por qué Thread por Cliente?

- Modelo simple y directo
- Apropiado para número limitado de jugadores
- Para alta concurrencia, considerar NIO o frameworks reactivos

### 16.4 ¿Por qué Estado Centralizado?

- Simplifica sincronización
- Previene inconsistencias
- Autoridad del servidor (anti-cheat)
- Fuente única de verdad

---

## 17. Glosario

- **DTO:** Data Transfer Object - Objeto para transferir datos
- **Lobby:** Sala de espera pre-juego
- **Turn-based:** Sistema por turnos
- **Thread-safe:** Seguro para concurrencia
- **Broadcasting:** Envío a todos los clientes
- **Graceful shutdown:** Cierre ordenado

---

## 18. Referencias

- Java Socket Programming: Oracle Docs
- Gson Documentation: https://github.com/google/gson
- SLF4J Documentation: http://www.slf4j.org/
- Clean Code Principles: Robert C. Martin

---

## 19. Conclusión

Esta implementación proporciona una base sólida y extensible para un servidor de juego multiplayer. Siguiendo los principios SOLID, KISS y DRY, el código es mantenible y fácilmente extensible para agregar funcionalidades específicas del juego.

La arquitectura desacoplada permite que la lógica del juego se implemente independientemente de la infraestructura de red, facilitando futuras iteraciones y mejoras.

---

## 21. Actualización: Cliente Implementado

### 21.1 Infraestructura del Cliente

Se ha completado la implementación del cliente del juego, que incluye:

**Componentes de Red:**
- `NetworkClient`: Gestión de socket TCP
- `MessageHandler`: Serialización/deserialización JSON
- `ConnectionManager`: Conexión, desconexión, reconexión automática

**Gestión de Estado:**
- `ClientGameState`: Estado local sincronizado
- `ServerUpdateProcessor`: Procesamiento de actualizaciones del servidor

**Lógica de Juego:**
- `TurnManager`: Control de turnos del jugador
- `ActionExecutor`: Envío y tracking de acciones

**Interfaz de Usuario:**
- `UIController`: Coordinación de pantallas
- `LobbyScreen`: Pantalla de lobby con jugadores
- `MapRenderer`: Renderizado del mundo y estado del juego

### 21.2 Comunicación Cliente-Servidor

El cliente y servidor se comunican usando el mismo protocolo:

```
Cliente                          Servidor
  |------ connect() ------------->|
  |<----- PLAYER_CONNECT ---------|
  |<----- LOBBY_STATE ------------|
  |<----- START_GAME -------------|
  |<----- GAME_STATE -------------|
  |<----- TURN_START -------------|
  |------ PLAYER_ACTION --------->|
  |<----- ACTION_VALID/INVALID ---|
  |<----- TURN_END ---------------|
  |<----- GAME_STATE -------------|
```

### 21.3 Archivos del Cliente

**Código fuente:** `client-src/main/java/com/juegito/client/`
**Configuración:** `client-pom.xml`, `client-src/main/resources/`
**Documentación:** `DOCUMENTACION_CLIENTE.md`
**README:** `CLIENT_README.md`

### 21.4 Características Implementadas

✅ Conexión TCP al servidor  
✅ Protocolo de mensajes JSON compatible  
✅ Sincronización de estado local  
✅ Reconexión automática (3 intentos)  
✅ Heartbeat (ping/pong) cada 5 segundos  
✅ Pantalla de lobby interactiva  
✅ Sistema de turnos por jugador  
✅ Validación de acciones  
✅ Renderizado de estado del juego  
✅ Preparado para mostrar combates  
✅ Preparado para cambios en el mapa  
✅ Manejo de desconexiones  

### 21.5 Uso del Cliente

**Compilar:**
```bash
mvn -f client-pom.xml clean package
```

**Ejecutar:**
```bash
java -jar target/game-client-1.0.0.jar [host] [port]
```

**Controles en Lobby:**
- R: Marcar como listo
- U: Cancelar listo
- Q: Salir

**Controles en Juego:**
- M: Mover
- A: Atacar
- D: Defender
- S: Saltar turno
- Q: Salir

### 21.6 Extensiones Futuras

El cliente está preparado para:
- Migración a UI gráfica (JavaFX/Swing)
- Implementación de lógica específica del juego
- Visualización avanzada del mapa
- Animaciones de combate
- Chat entre jugadores
- Estadísticas y rankings

Ver `DOCUMENTACION_CLIENTE.md` para detalles completos de arquitectura y diseño.

---

**Fin del documento**
