# Juegito Game Server

Servidor de juego multiplayer desarrollado en Java.

## Estructura del Proyecto

```
Juegito/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── juegito/
│   │   │           ├── server/        # Servidor y manejo de clientes
│   │   │           ├── game/          # Lógica del juego
│   │   │           ├── model/         # Modelos de dominio
│   │   │           └── protocol/      # Protocolo de comunicación
│   │   └── resources/
│   │       ├── logback.xml
│   │       └── server.properties
├── pom.xml
└── DOCUMENTACION.md
```

## Requisitos

- Java 17 o superior
- Maven 3.6+

## Compilación

```bash
mvn clean package
```

## Ejecución

```bash
java -jar target/game-server-1.0.0.jar [puerto] [minJugadores] [maxJugadores]
```

Ejemplo:
```bash
java -jar target/game-server-1.0.0.jar 8080 2 4
```

## Características

- ✅ Servidor con sockets TCP
- ✅ Protocolo de mensajes basado en JSON
- ✅ Sistema de lobby con sincronización
- ✅ Gestión de jugadores conectados
- ✅ Ciclo de turnos
- ✅ Validación de acciones
- ✅ Gestión de estado del mundo
- ✅ Manejo de desconexiones
- ✅ Logging detallado

## Documentación

Para más detalles sobre la arquitectura y diseño, ver [DOCUMENTACION.md](DOCUMENTACION.md)
