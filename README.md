# Juegito Game Server

Servidor de juego multiplayer desarrollado en Java con arquitectura modular.

## Arquitectura del Proyecto

El proyecto utiliza **3 módulos Maven** para evitar duplicación de código (DRY) y sigue principios SOLID:

```
Juegito/
├── protocol-common/          # Módulo compartido
│   ├── pom.xml
│   └── src/main/java/com/juegito/protocol/
│       ├── Message.java
│       ├── MessageType.java
│       └── dto/              # 29+ DTOs compartidos
│           ├── lobby/         # DTOs del sistema de lobby
│           └── ...            # DTOs de juego
├── game-server/              # Servidor
│   ├── pom.xml
│   └── src/main/java/com/juegito/
│       ├── server/           # Servidor y manejo de clientes
│       ├── game/             # Lógica del juego
│       │   └── lobby/        # Sistema de lobby modular
│       ├── model/            # Modelos de dominio
│       └── protocol/
│           └── MapDTOConverter.java  # Server-only
└── game-client/              # Cliente con LibGDX
    ├── client-pom.xml
    └── client-src/main/java/com/juegito/client/
        ├── graphics/         # Renderizado LibGDX
        ├── network/          # Comunicación TCP
        ├── state/            # Estado del cliente
        └── game/             # Lógica de turnos
```

**Principios aplicados:**
- ✅ **DRY** (Don't Repeat Yourself): Módulo compartido evita duplicación
- ✅ **KISS** (Keep It Simple): Separación clara de responsabilidades
- ✅ **SRP** (Single Responsibility): Cada clase tiene una responsabilidad única
  - `Player`: Solo maneja conexión de red (Socket, I/O)
  - `PlayerLobbyData`: Solo maneja estado del lobby
  - `LobbyManager`: Coordina operaciones de lobby
  - `LobbyState`: Mantiene y valida estado
- ✅ **Low Coupling**: Módulos independientes con interfaces bien definidas
- ✅ **High Cohesion**: Componentes relacionados agrupados (game/lobby/)

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

**Fase 1 - Infraestructura del Servidor: 85% COMPLETADO** ✅  
**Fase 2 - Cliente Básico (Sin Gráficos): 100% COMPLETADO** ✅  
**Fase 3 - Gráficos con LibGDX: 100% COMPLETADO** ✅

Ver `FASE1_ESTADO_IMPLEMENTACION.md` para detalles completos de todas las fases.

### Estado Actual de la Interfaz

- ✅ Cliente funcional con **interfaz gráfica LibGDX**
- ✅ **LibGDX 1.12.1 integrado** - ventana de 1280x720
- ✅ Renderizado visual del mapa hexagonal con colores
- ✅ Sprites de jugadores (círculos placeholder)
- ✅ Barras de HP y nombres de jugadores
- ✅ Click en casillas para mover
- ✅ HUD con turno, lista de jugadores y log de acciones
- ✅ Cámara con zoom (scroll del mouse)

La Fase 3 (interfaz gráfica con LibGDX) está **completamente implementada**.

## Características Implementadas

### Infraestructura
- ✅ Servidor con sockets TCP
- ✅ Protocolo de mensajes basado en JSON
- ✅ Manejo concurrente de múltiples clientes
- ✅ Thread pool para escalabilidad

### Gestión de Juego
- ✅ Sistema de lobby avanzado con sincronización en tiempo real
- ✅ Control de permisos basado en roles (host vs jugadores)
- ✅ Selección de clase y color por jugador
- ✅ Ready check con validación completa
- ✅ Inicio automático de partida
- ✅ Ciclo de turnos rotatorio
- ✅ Validación de acciones
- ✅ Sincronización de estado del mundo

### Mapa y Movimiento
- ✅ Generación procedural de mapa hexagonal
- ✅ Sistema de spawn points multi-jugador
- ✅ Pathfinding A* para movimiento
- ✅ Validación de movimiento por tipo de tile

### Interfaz Gráfica
- ✅ Renderizado hexagonal con LibGDX
- ✅ Cámara 2D con zoom y pan
- ✅ Click en tiles para mover
- ✅ HUD con información de partida
- ✅ Sprites placeholder (círculos y hexágonos)

### Protocolo
- ✅ 31+ tipos de mensajes (incluye 16 del sistema de lobby)
- ✅ 29+ DTOs completos
- ✅ Serialización automática JSON
- ✅ Validaciones de permisos y seguridad

### Robustez
- ✅ Manejo graceful de desconexiones
- ✅ Logging completo (SLF4J + Logback)
- ✅ Thread-safety en estructuras compartidas
- ✅ Shutdown hook para limpieza

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

- **DOCUMENTACION.md** - Documentación técnica completa del servidor (incluye Sistema de Lobby Avanzado)
- **FASE1_ESTADO_IMPLEMENTACION.md** - Estado detallado de la Fase 1 ⭐
- **DOCUMENTACION_CLIENTE.md** - Arquitectura del cliente
- **DOCUMENTACION_MAPA.md** - Sistema de mapa hexagonal
- **INSTRUCCIONES_BUILD.md** - Instrucciones de compilación
- **CLIENT_README.md** - Guía de uso del clientecompilación
- **CLIENT_README.md** - Guía de uso del cliente

## Arquitectura

### Principios de Diseño
- **KISS** (Keep It Simple, Stupid)
- **DRY** (Don't Repeat Yourself)
- **Bajo acoplamiento**
- **Alta cohesión**
- **SOLID principles**

### Estructura de Paquetes
```
com.juegito
├── server/          # GameServer, ClientHandler
├── game/            # GameState, Lobby, ActionValidator, MapGenerator
├── model/           # Player, GameMap, Tile, HexCoordinate
└── protocol/        # Message, MessageType, DTOs
```

## Testing

Para ejecutar pruebas manuales:

1. Iniciar servidor:
```bash
java -jar target/game-server-1.0.0.jar 8080 2 4
```

2. Conectar clientes (en terminales separadas):
```bash
java -jar client-target/game-client-1.0.0.jar localhost 8080
```

3. Marcar como listos (presionar R en cada cliente)
4. El juego iniciará automáticamente

## Contribución

Ver `FASE1_ESTADO_IMPLEMENTACION.md` para el plan de desarrollo y próximos pasos.

## Licencia

Proyecto educativo - 2025

