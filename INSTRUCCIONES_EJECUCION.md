# Instrucciones de Compilación y Ejecución - Cliente con LibGDX

## Requisitos

- **Java 17** o superior
- **Maven 3.6+**
- **Servidor del juego ejecutándose** (por defecto en `localhost:8080`)

---

## Compilación

### Servidor

```bash
mvn clean package
```

El JAR se generará en: `target/game-server-1.0.0.jar`

### Cliente (con LibGDX)

```bash
mvn -f client-pom.xml clean package
```

El JAR se generará en: `client-target/game-client-1.0.0.jar`

---

## Ejecución

### 1. Iniciar el Servidor

```bash
java -jar target/game-server-1.0.0.jar [puerto] [minJugadores] [maxJugadores]
```

**Ejemplo:**
```bash
java -jar target/game-server-1.0.0.jar 8080 2 4
```

Esto inicia el servidor en el puerto 8080, requiriendo mínimo 2 jugadores y permitiendo máximo 4.

### 2. Iniciar el Cliente (Interfaz Gráfica)

```bash
java -jar client-target/game-client-1.0.0.jar [host] [puerto]
```

**Ejemplos:**

**Conectar a localhost:**
```bash
java -jar client-target/game-client-1.0.0.jar
```

**Conectar a servidor remoto:**
```bash
java -jar client-target/game-client-1.0.0.jar 192.168.1.100 8080
```

---

## Controles del Cliente

### En el Lobby
- Esperar a que se conecten suficientes jugadores
- El juego inicia automáticamente cuando todos los jugadores están listos

### Durante el Juego

**Mouse:**
- **Click izquierdo en tile:** Seleccionar casilla
- **Click izquierdo en tile diferente:** Mover a esa casilla (si es tu turno)
- **Scroll del mouse:** Zoom in/out

**Teclado:**
- *(Controles de teclado se pueden agregar en el futuro)*

---

## Interfaz Gráfica

### Elementos de la UI

**Panel Superior Central:**
- Número de turno actual
- Indicador "ES TU TURNO" (amarillo cuando es tu turno)

**Panel Izquierdo:**
- Lista de jugadores conectados
- Indicador del jugador activo (marcado con `>`)
- Tu jugador marcado con "(TÚ)"

**Panel Inferior Derecho:**
- Log de acciones recientes
- Últimas 10 acciones mostradas

**Mapa:**
- Hexágonos con colores por bioma:
  - **Verde oscuro:** Bosque
  - **Gris:** Montaña
  - **Verde claro:** Llanura
  - **Azul:** Spawn point
  - **Dorado:** Recurso
  - **Púrpura:** Estratégico
- **Círculos de colores:** Jugadores
- **Círculo dorado:** Tu jugador
- **Barras verdes:** HP de los jugadores
- **Nombres blancos:** ID de los jugadores

**Highlight:**
- Tile seleccionado aparece con borde amarillo semi-transparente

---

## Resolución de Problemas

### El cliente no se conecta

1. Verifica que el servidor esté ejecutándose
2. Verifica que el host y puerto sean correctos
3. Revisa los logs en consola para ver mensajes de error

### Ventana no aparece

1. Verifica que tengas Java 17 o superior
2. Asegúrate de que las dependencias de LibGDX se descargaron correctamente:
   ```bash
   mvn -f client-pom.xml dependency:resolve
   ```

### El juego va lento

1. Verifica que tu tarjeta gráfica soporte OpenGL
2. Reduce el tamaño de la ventana si es necesario
3. Cierra otras aplicaciones que usen la GPU

### Errores de compilación

Si hay errores al compilar, limpia y recompila:
```bash
mvn -f client-pom.xml clean
mvn -f client-pom.xml package
```

---

## Notas Técnicas

### Dependencias Principales

**Cliente:**
- LibGDX 1.12.1 (core, backend-lwjgl3, platform)
- LWJGL 3.3.3 (OpenGL, GLFW, OpenAL)
- Gson 2.10.1
- SLF4J 2.0.9 + Logback 1.4.11

**Servidor:**
- Gson 2.10.1
- SLF4J 2.0.9 + Logback 1.4.11

### Rendimiento

- **FPS:** 60 FPS (con VSync)
- **Resolución inicial:** 1280x720
- **Redimensionable:** Sí

---

## Próximas Mejoras

### Gráficos
- [ ] Sprites reales en lugar de círculos
- [ ] Texturas para hexágonos
- [ ] Animaciones de movimiento
- [ ] Efectos de partículas
- [ ] Fuentes custom

### Gameplay
- [ ] Sistema de combate visual
- [ ] Indicadores de alcance de movimiento
- [ ] Fog of war
- [ ] Animaciones de ataque
- [ ] Efectos de sonido

### UI
- [ ] Pantalla de lobby mejorada
- [ ] Chat entre jugadores
- [ ] Menú de opciones
- [ ] Estadísticas post-partida

---

**Compilado el:** 8 de diciembre de 2025  
**Versión:** 1.0.0  
**Fase 3 - Gráficos con LibGDX:** ✅ COMPLETADA
