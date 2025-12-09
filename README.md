# Juegito - Juego Multiplayer Hexagonal

Juego multiplayer por turnos desarrollado en Java con arquitectura modular y gráficos LibGDX.

## Arquitectura del Proyecto

El proyecto utiliza **3 módulos Maven** implementando principios SOLID, DRY y KISS:

```
Juegito/
├── protocol-common/          # Módulo compartido (DTOs y mensajes)
│   ├── pom.xml
│   └── src/main/java/com/juegito/protocol/
│       ├── Message.java
│       ├── MessageType.java (40+ tipos)
│       └── dto/              # 34 DTOs compartidos
│           ├── lobby/        # 16 DTOs del sistema de lobby
│           └── ...           # DTOs de juego (mapa, acciones, estado)
│
├── src/                      # Servidor (game-server)
│   ├── pom.xml
│   └── main/java/com/juegito/
│       ├── server/           # GameServer, ClientHandler, NetworkService
│       ├── game/             # Lógica del juego
│       │   ├── lobby/        # LobbyManager, LobbyState (sistema modular)
│       │   ├── ActionValidator.java
│       │   ├── GameState.java
│       │   ├── MapGenerator.java
│       │   └── MovementExecutor.java
│       ├── model/            # Player, GameMap, Tile, HexCoordinate
│       └── protocol/
│           └── MapDTOConverter.java
│
└── client-src/               # Cliente (game-client) con LibGDX
    ├── client-pom.xml
    └── main/java/com/juegito/client/
        ├── graphics/         # Renderizado LibGDX
        │   ├── GameScreen.java
        │   ├── LobbyScreen.java
        │   ├── HexMapRenderer.java
        │   ├── PlayerRenderer.java
        │   ├── HUD.java
        │   ├── CameraController.java
        │   ├── GameInputProcessor.java
        │   └── SimpleAssetManager.java
        ├── network/          # Comunicación TCP
        │   ├── ConnectionManager.java
        │   ├── NetworkClient.java
        │   └── MessageHandler.java
        ├── state/            # Estado del cliente
        │   ├── ClientGameState.java
        │   ├── LobbyClientState.java
        │   ├── PlayerLocalState.java
        │   └── ServerUpdateProcessor.java
        ├── events/           # Sistema de eventos pub/sub
        │   ├── EventBus.java
        │   ├── GameEvent.java
        │   └── GameEventType.java
        ├── controllers/
        │   └── ActionController.java
        ├── ui/
        │   ├── DebugConsole.java
        │   └── ConnectionScreen.java
        └── game/
            ├── ActionExecutor.java
            └── TurnManager.java
```

**Principios aplicados:**

✅ **DRY (Don't Repeat Yourself)**
- Módulo `protocol-common` compartido evita duplicación de DTOs
- Método `deserializePayload<T>()` genérico en `ClientHandler`
- Constantes centralizadas en `GraphicsConstants`
- Mapas de colores estáticos (`createBiomeColors()`, `createTileColors()`)
- Métodos de utilidad reutilizables (`createSystemMessage()`, `stopThreads()`)

✅ **KISS (Keep It Simple, Stupid)**
- Cada clase tiene una responsabilidad única y clara
- Renderers separados (mapa, jugadores, HUD)
- Métodos pequeños y enfocados
- Delegación clara de responsabilidades

✅ **SRP (Single Responsibility Principle)**
- `Player`: Solo conexión de red (Socket, I/O)
- `PlayerLobbyData`: Solo estado del lobby
- `LobbyManager`: Coordinación de operaciones
- `LobbyState`: Validación y mantenimiento de estado
- `HexMapRenderer`: Solo renderizado del mapa
- `PlayerRenderer`: Solo renderizado de jugadores
- `HUD`: Solo interfaz de usuario

✅ **Low Coupling / High Cohesion**
- Módulos independientes con interfaces bien definidas
- Componentes relacionados agrupados lógicamente
- `HexMapRenderer` solo conoce `GameMapDTO`, no la red
- `EventBus` desacopla UI de lógica de negocio

## Requisitos

- Java 17 o superior
- Maven 3.6+

## Compilación

**1. Compilar módulo compartido (protocol-common):**
```bash
cd protocol-common
mvn clean install
```

**2. Compilar servidor:**
```bash
cd ..
mvn clean package
```

**3. Compilar cliente:**
```bash
mvn -f client-pom.xml clean package
```

## Ejecución

**Servidor:**
```bash
java -jar target/game-server-1.0.0.jar [puerto] [minJugadores] [maxJugadores]
```

Ejemplo:
```bash
java -jar target/game-server-1.0.0.jar 8080 2 4
```

**Cliente:**
```bash
java -jar client-target/game-client-1.0.0.jar [host] [puerto]
```

Ejemplo:
```bash
java -jar client-target/game-client-1.0.0.jar localhost 8080
```

## Pruebas Rápidas

**Script de pruebas automáticas (Windows):**

El proyecto incluye `test-game.bat` para ejecutar automáticamente 1 servidor + 2 clientes:

```bash
test-game.bat
```

Este script:
- Verifica que los JARs estén compilados
- Inicia el servidor en el puerto 8080 (2-4 jugadores)
- Abre 2 ventanas de cliente conectándose a localhost:8080
- Permite probar las funcionalidades multijugador inmediatamente

**Requisitos previos:**
- Haber compilado el proyecto con `mvn package` (cliente y servidor)
- Los JARs deben existir en `target/` y `client-target/`

## Estado del Proyecto

**✅ Fase 1 - Infraestructura del Servidor: 100% COMPLETADO**
**✅ Fase 2 - Cliente Básico: 100% COMPLETADO**  
**✅ Fase 3 - Gráficos con LibGDX: 100% COMPLETADO**

Ver `FASE1_ESTADO_IMPLEMENTACION.md` para detalles completos.

### Características Actuales

**Sistema de Lobby Completo:**
- ✅ Lobby con sincronización en tiempo real
- ✅ Sistema de roles (host/jugador) con permisos
- ✅ Selección de clase, color y nombre
- ✅ Ready check con validación
- ✅ Inicio automático cuando todos están listos
- ✅ Kick de jugadores (solo host)
- ✅ Configuración de lobby (solo host)

**Interfaz Gráfica (LibGDX 1.12.1):**
- ✅ Pantalla de lobby visual con lista de jugadores
- ✅ Renderizado hexagonal del mapa (flat-top hexagons)
- ✅ Sprites de jugadores con círculos de colores
- ✅ Barras de HP y nombres de jugadores
- ✅ HUD con información de turno, jugadores y log de acciones
- ✅ Cámara 2D con zoom (scroll) y pan (WASD/flechas)
- ✅ Click en hexágonos para mover
- ✅ Sistema de eventos pub/sub (EventBus)

**Mecánicas de Juego:**
- ✅ Generación procedural de mapa hexagonal
- ✅ Sistema de biomas (bosque, montaña, llanura)
- ✅ Tiles especiales (spawn, recursos, estratégicos)
- ✅ Pathfinding A* para movimiento
- ✅ Validación de movimiento por tipo de tile
- ✅ Sistema de turnos rotatorio
- ✅ Estado local del jugador (HP, inventario, cooldowns)

## Características Implementadas

### Infraestructura y Red
- ✅ Servidor TCP multihilo con thread pool
- ✅ Protocolo JSON con 40+ tipos de mensajes
- ✅ Reconexión automática del cliente (3 intentos)
- ✅ Heartbeat/ping-pong para detectar desconexiones
- ✅ Timeout de inactividad (60 segundos)
- ✅ Manejo graceful de desconexiones
- ✅ Logging completo (SLF4J + Logback)
- ✅ Thread-safety en estructuras compartidas

### Sistema de Lobby
- ✅ Lobby avanzado con sincronización en tiempo real
- ✅ Sistema de roles: Host (primer jugador) vs Jugadores
- ✅ Permisos diferenciados:
  - Host: Kick, cambiar configuración, iniciar partida
  - Jugadores: Ready, cambiar clase/color/nombre
- ✅ Selección de clase (Warrior/Mage/Ranger/Rogue)
- ✅ Selección de color único por jugador
- ✅ Cambio de nombre en tiempo real
- ✅ Ready check con validación completa
- ✅ Inicio automático cuando todos ready
- ✅ Chat del lobby (opcional)
- ✅ Snapshot completo del lobby en cada cambio

### Generación y Renderizado del Mapa
- ✅ Generación procedural de mapa hexagonal
- ✅ Algoritmo de distribución de biomas:
  - Bosque (verde oscuro)
  - Montaña (gris)
  - Llanura (beige/amarillo claro)
- ✅ Tiles especiales:
  - Spawn (azul) - puntos de inicio
  - Resource (dorado) - recursos
  - Strategic (púrpura) - puntos clave
  - Blocked (rojo oscuro) - bloqueados
- ✅ Sistema de spawn points para múltiples jugadores
- ✅ Renderizado hexagonal flat-top con LibGDX
- ✅ Colores diferenciados por bioma y tipo
- ✅ Highlight amarillo en tile seleccionado
- ✅ Bordes oscuros en hexágonos

### Sistema de Movimiento y Turnos
- ✅ Pathfinding A* para movimiento inteligente
- ✅ Validación de movimiento:
  - Distancia máxima por turno
  - Tiles bloqueados
  - Costos variables por bioma
- ✅ Ciclo de turnos rotatorio
- ✅ Validación de acciones (solo en tu turno)
- ✅ Sincronización de estado del mundo
- ✅ Feedback visual de acciones

### Interfaz y Controles
- ✅ Pantalla de lobby gráfica:
  - Lista de jugadores con estado (Ready/Not Ready)
  - Indicador de host
  - Botones de ready/unready
  - Selección de clase y color
- ✅ Pantalla de juego:
  - Mapa hexagonal renderizado
  - Jugadores con círculos de colores únicos
  - Barras de HP sobre jugadores
  - Nombres de jugadores
  - HUD con información de turno
  - Lista de jugadores activos
  - Log de últimas 10 acciones
- ✅ Cámara 2D:
  - Zoom con scroll del mouse (0.5x - 3.0x)
  - Pan con WASD o flechas
  - Lerp suave para movimientos
- ✅ Input:
  - Click en hexágono para seleccionar
  - Click en destino para mover
  - Validación de turnos antes de acción

### Estado y Sincronización
- ✅ `ClientGameState`: Estado global del juego
- ✅ `LobbyClientState`: Estado del lobby
- ✅ `PlayerLocalState`: Estado local completo:
  - HP actual y máximo
  - Inventario (lista de items)
  - Cooldowns de habilidades
  - Efectos de estado activos
- ✅ `ServerUpdateProcessor`: Procesa mensajes del servidor
- ✅ `EventBus`: Sistema pub/sub para desacoplar componentes
- ✅ Sincronización en tiempo real de todos los cambios

## Controles

**En Lobby:**
- Botón "Ready"/"Unready" - Marcar listo para jugar
- Click en clase - Seleccionar clase (Warrior/Mage/Ranger/Rogue)
- Click en color - Seleccionar color único
- Editar nombre - Cambiar nombre del jugador
- Botón "Start Game" - Iniciar partida (solo host cuando todos listos)
- Botón "Quit" - Salir del lobby

**En Juego:**
- **Click izquierdo** en hexágono - Seleccionar tile
- **Click izquierdo** en tile destino - Mover jugador (solo en tu turno)
- **Scroll mouse** - Zoom in/out (0.5x - 3.0x)
- **WASD o flechas** - Mover cámara (pan)
- **ESC** - Menú (futuro)

## Características Pendientes (Fases Futuras)

### Fase 4 - Sistema de Combate
- ⏳ Ataques básicos y a distancia
- ⏳ Sistema de daño y defensa
- ⏳ Habilidades especiales por clase
- ⏳ Efectos de área (AoE)
- ⏳ Críticos y esquivas

### Fase 5 - Sistema de Progresión
- ⏳ Experiencia y niveles
- ⏳ Árbol de habilidades por clase
- ⏳ Sistema de loot
- ⏳ Inventario mejorado
- ⏳ Equipamiento (armas, armaduras)

### Fase 6 - Contenido y Mejoras
- ⏳ Enemigos NPC con IA
- ⏳ Misiones y objetivos
- ⏳ Efectos ambientales (clima)
- ⏳ Sonido y música
- ⏳ Animaciones mejoradas
- ⏳ Sprites personalizados (reemplazar placeholders)
- ⏳ Persistencia de partidas
- ⏳ Sistema de puntuación/ranking
  - HP actual y máximo
  - Inventario (lista de items)
  - Cooldowns de habilidades
  - Efectos de estado activos
- ✅ `ServerUpdateProcessor`: Procesa mensajes del servidor
- ✅ `EventBus`: Sistema pub/sub para desacoplar componentes
- ✅ Sincronización en tiempo real de todos los cambios

## Cliente

### Compilar Cliente
```bash
mvn -f client-pom.xml clean package
```

### Ejecutar Cliente
```bash
java -jar client-target/game-client-1.0.0.jar [host] [port]
```

Ejemplo:
```bash
java -jar client-target/game-client-1.0.0.jar localhost 8080
```

### Características del Cliente

- ✅ Proyecto separado del servidor
- ✅ Conexión a IP pública configurable
- ✅ Reconexión automática (3 intentos)
- ✅ Consola de debug interactiva
- ✅ Estado local sincronizado
- ✅ Cache del mundo (mapa, entidades, eventos)
- ✅ Comandos simples (M/A/D/S/R/Q)
- ✅ Feedback de acciones en tiempo real

### Controles

**En Lobby:**
- R: Ready (listo para jugar)
- U: Unready (cancelar listo)
- Q: Quit (salir)

**En Juego:**
- M: Move (mover)
- A: Attack (atacar)
- D: Defend (defender)
- S: Skip (saltar turno)
- Q: Quit (salir)

## Características Pendientes (Fases Futuras)

- ⏳ Interfaz gráfica (JavaFX/Swing)
- ⏳ Timeout de turnos automático
- ⏳ Sistema de combate con enemigos
- ⏳ Sistema de loot y objetos
- ⏳ Habilidades especiales
- ⏳ Efectos ambientales (clima)
- ⏳ Persistencia de partidas
- ⏳ Chat entre jugadores
## Documentación

- **README.md** - Este archivo: visión general y guía de inicio rápido
- **DOCUMENTACION.md** - Documentación técnica completa del servidor
- **DOCUMENTACION_CLIENTE.md** - Arquitectura detallada del cliente
- **DOCUMENTACION_MAPA.md** - Sistema de mapa hexagonal y pathfinding
- **FASE1_ESTADO_IMPLEMENTACION.md** - Estado detallado de todas las fases
- **INSTRUCCIONES_BUILD.md** - Compilación paso a paso
- **INSTRUCCIONES_EJECUCION.md** - Ejecución y controles del cliente
- **CLIENT_README.md** - Guía específica del cliente

## Arquitectura Técnica

### Servidor (game-server)

**Paquetes principales:**
- `server/` - Infraestructura de red
  - `GameServer.java` - Servidor TCP principal
  - `ClientHandler.java` - Handler por cliente (hilo dedicado)
  - `NetworkService.java` - Utilidades de red
  
- `game/` - Lógica del juego
  - `GameState.java` - Estado global de la partida
  - `ActionValidator.java` - Validación de acciones
  - `MapGenerator.java` - Generación procedural de mapas
  - `MovementExecutor.java` - Ejecución de movimientos
  - `MovementValidator.java` - Validación de movimientos
  - `lobby/` - Sistema de lobby modular
    - `LobbyManager.java` - Coordinador principal
    - `LobbyState.java` - Estado y validaciones
    - `LobbyConfig.java` - Configuración
    - `PlayerLobbyData.java` - Datos de jugador en lobby

- `model/` - Modelos de dominio
  - `Player.java` - Socket y comunicación
  - `GameMap.java` - Mapa completo
  - `Tile.java` - Tile individual
  - `HexCoordinate.java` - Coordenadas hexagonales (axial)
  - `BiomeType.java`, `TileType.java` - Enums

### Cliente (game-client)

**Paquetes principales:**
- `graphics/` - Renderizado LibGDX
  - `GameApplication.java` - Aplicación principal
  - `LobbyScreen.java` - Pantalla de lobby
  - `GameScreen.java` - Pantalla de juego
  - `HexMapRenderer.java` - Renderizado del mapa
  - `PlayerRenderer.java` - Renderizado de jugadores
  - `HUD.java` - Interfaz de usuario
  - `CameraController.java` - Control de cámara
  - `GameInputProcessor.java` - Procesamiento de input
  - `SimpleAssetManager.java` - Gestión de assets
  - `GraphicsConstants.java` - Constantes gráficas (DRY)

- `network/` - Comunicación
  - `ConnectionManager.java` - Gestión de conexión y reconexión
  - `NetworkClient.java` - Cliente TCP
  - `MessageHandler.java` - Serialización JSON

- `state/` - Estado del cliente
  - `ClientGameState.java` - Estado global
  - `LobbyClientState.java` - Estado del lobby
  - `PlayerLocalState.java` - Estado local del jugador
  - `ServerUpdateProcessor.java` - Procesador de actualizaciones

- `events/` - Sistema de eventos
  - `EventBus.java` - Bus pub/sub thread-safe
  - `GameEvent.java` - Evento base
  - `GameEventType.java` - Tipos de eventos
  - `GameEventListener.java` - Interface de listener

- `controllers/` - Controladores
  - `ActionController.java` - Controlador de acciones con validación

- `ui/` - UI Components
  - `DebugConsole.java` - Consola de debug
  - `ConnectionScreen.java` - Pantalla de conexión

- `game/` - Lógica de juego
  - `ActionExecutor.java` - Ejecución de acciones
  - `TurnManager.java` - Gestión de turnos

## Mejoras de Código Recientes (DRY/KISS)

**Refactorings aplicados:**

1. **ClientHandler** (servidor):
   - ✅ Método genérico `deserializePayload<T>()` elimina 80+ líneas duplicadas
   - ✅ Simplificación de handlers de lobby

2. **ConnectionManager** (cliente):
   - ✅ Método `createSystemMessage()` reutilizable
   - ✅ Método `stopThreads()` para limpieza

3. **NetworkClient** (cliente):
   - ✅ Método `closeResource()` genérico para cleanup

4. **HexMapRenderer** (cliente):
   - ✅ Constantes precalculadas (`HEX_3_2`, `HEX_SQRT3`)
   - ✅ Métodos estáticos para mapas de colores
   - ✅ Renderizado por capas separadas (tiles, bordes, selección)

5. **PlayerRenderer** (cliente):
   - ✅ Tres métodos separados para renderizado (círculos, HP, nombres)
   - ✅ Eliminación de loops duplicados

6. **GameScreen** (cliente):
   - ✅ Inicialización modular (`initializeAssets()`, `initializeCamera()`, etc.)
   - ✅ Separación de actualización y renderizado
   - ✅ Integración de `SimpleAssetManager`

7. **GraphicsConstants**:
   - ✅ Centralización de constantes gráficas
   - ✅ Evita "magic numbers" duplicados en todo el código
- **DOCUMENTACION_CLIENTE.md** - Arquitectura del cliente
- **DOCUMENTACION_MAPA.md** - Sistema de mapa hexagonal
- **INSTRUCCIONES_BUILD.md** - Instrucciones de compilación
- **CLIENT_README.md** - Guía de uso del cliente

## Testing

Para probar el juego multiplayer localmente, ejecuta:

```bash
test-game.bat
```

Este script automáticamente:
1. Verifica que los JARs estén compilados
2. Inicia 1 servidor en puerto 8080 (2-4 jugadores)
3. Abre 2 clientes conectados a localhost:8080

**Pasos para probar:**
1. Espera a que ambos clientes se conecten
2. Ambos jugadores marcan "Ready" en el lobby
3. El host (jugador 1) presiona "Start Game"
4. Juega haciendo click en hexágonos para mover

## Contribución

Proyecto educativo siguiendo principios SOLID, DRY y KISS.

## Licencia

Proyecto educativo - 2025

