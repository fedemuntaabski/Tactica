# Juegito Game Client

Cliente de juego multiplayer desarrollado en Java.

## Estructura del Proyecto

```
client-src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── juegito/
│   │           └── client/
│   │               ├── GameClient.java      # Clase principal
│   │               ├── network/             # Conexión y comunicación
│   │               ├── state/               # Estado local del juego
│   │               ├── game/                # Lógica de turnos y acciones
│   │               ├── ui/                  # Interfaz de usuario
│   │               └── protocol/            # Protocolo de mensajes
│   └── resources/
│       ├── logback.xml
│       └── client.properties
└── pom.xml (client-pom.xml)
```

## Requisitos

- Java 17 o superior
- Maven 3.6+
- Servidor ejecutándose

## Compilación

```bash
mvn -f client-pom.xml clean package
```

## Ejecución

```bash
java -jar target/game-client-1.0.0.jar [host] [puerto]
```

Ejemplo:
```bash
java -jar target/game-client-1.0.0.jar localhost 8080
```

## Características

- ✅ Conexión al servidor vía TCP
- ✅ Protocolo de mensajes JSON
- ✅ Estado local sincronizado
- ✅ Manejo de reconexión automática
- ✅ Pantalla de lobby interactiva
- ✅ Sistema de turnos
- ✅ Ejecución de acciones
- ✅ Renderizado de mapa base
- ✅ Preparado para combate y actualizaciones

## Controles

### En el Lobby
- `R` - Marcar como listo
- `U` - Cancelar listo
- `Q` - Salir

### En el Juego
- `M` - Mover
- `A` - Atacar
- `D` - Defender
- `S` - Saltar turno
- `Q` - Salir

## Documentación

Ver [DOCUMENTACION_CLIENTE.md](DOCUMENTACION_CLIENTE.md) para detalles técnicos.
