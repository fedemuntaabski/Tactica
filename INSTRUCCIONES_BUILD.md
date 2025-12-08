# Instrucciones de Compilación y Ejecución

## Estructura de Archivos

```
Juegito/
├── SERVIDOR/
│   ├── pom.xml
│   └── src/main/java/com/juegito/server/...
│
├── CLIENTE/
│   ├── client-pom.xml  (renombrar a pom.xml si se compila separado)
│   └── client-src/     (renombrar a src/ si se compila separado)
│
└── DOCUMENTACION/
    ├── DOCUMENTACION.md         (Servidor)
    ├── DOCUMENTACION_CLIENTE.md (Cliente)
    ├── README.md                (Servidor)
    └── CLIENT_README.md         (Cliente)
```

---

## Opción 1: Compilar en Directorios Separados (Recomendado)

### Paso 1: Organizar Servidor

```powershell
# Crear directorio del servidor
New-Item -ItemType Directory -Path "e:\Juegito\server"

# Mover archivos del servidor
Move-Item "e:\Juegito\pom.xml" "e:\Juegito\server\"
Move-Item "e:\Juegito\src" "e:\Juegito\server\"

# Compilar servidor
cd e:\Juegito\server
mvn clean package
```

### Paso 2: Organizar Cliente

```powershell
# Crear directorio del cliente
New-Item -ItemType Directory -Path "e:\Juegito\client"

# Mover y renombrar archivos del cliente
Move-Item "e:\Juegito\client-pom.xml" "e:\Juegito\client\pom.xml"
Move-Item "e:\Juegito\client-src" "e:\Juegito\client\src"

# Compilar cliente
cd e:\Juegito\client
mvn clean package
```

### Paso 3: Ejecutar

**Terminal 1 - Servidor:**
```powershell
cd e:\Juegito\server
java -jar target\game-server-1.0.0.jar
```

**Terminal 2 - Cliente 1:**
```powershell
cd e:\Juegito\client
java -jar target\game-client-1.0.0.jar localhost 8080
```

**Terminal 3 - Cliente 2:**
```powershell
cd e:\Juegito\client
java -jar target\game-client-1.0.0.jar localhost 8080
```

---

## Opción 2: Proyecto Multi-Módulo Maven

### Crear pom.xml padre

Crear `e:\Juegito\pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.juegito</groupId>
    <artifactId>juegito-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>server</module>
        <module>client</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
</project>
```

### Compilar todo

```powershell
cd e:\Juegito
mvn clean package
```

---

## Opción 3: Compilación Manual (Sin Maven)

### Compilar Servidor

```powershell
cd e:\Juegito
mkdir -p build\server\classes
javac -d build\server\classes -cp "lib\gson-2.10.1.jar;lib\slf4j-api-2.0.9.jar;lib\logback-classic-1.4.11.jar" src\main\java\com\juegito\server\*.java src\main\java\com\juegito\game\*.java src\main\java\com\juegito\model\*.java src\main\java\com\juegito\protocol\*.java src\main\java\com\juegito\protocol\dto\*.java

# Crear JAR
cd build\server\classes
jar cvfe ..\..\..\game-server.jar com.juegito.server.GameServer com\juegito\*.class
```

### Compilar Cliente

```powershell
cd e:\Juegito
mkdir -p build\client\classes
javac -d build\client\classes -cp "lib\gson-2.10.1.jar;lib\slf4j-api-2.0.9.jar;lib\logback-classic-1.4.11.jar" client-src\main\java\com\juegito\client\*.java client-src\main\java\com\juegito\client\network\*.java client-src\main\java\com\juegito\client\state\*.java client-src\main\java\com\juegito\client\game\*.java client-src\main\java\com\juegito\client\ui\*.java client-src\main\java\com\juegito\client\protocol\*.java client-src\main\java\com\juegito\client\protocol\dto\*.java

# Crear JAR
cd build\client\classes
jar cvfe ..\..\..\game-client.jar com.juegito.client.GameClient com\juegito\*.class
```

---

## Prueba Rápida

### 1. Iniciar Servidor

```powershell
java -jar game-server.jar
```

Deberías ver:
```
INFO  c.j.s.GameServer - Game server started on port 8080
INFO  c.j.s.GameServer - Waiting for players... (min: 2, max: 4)
```

### 2. Conectar Cliente 1

En otra terminal:
```powershell
java -jar game-client.jar
```

Deberías ver:
```
╔════════════════════════════════════════╗
║         JUEGITO - CLIENTE              ║
╚════════════════════════════════════════╝

✓ Conectado al servidor

╔════════════════════════════════════════╗
║           LOBBY - JUEGITO              ║
╚════════════════════════════════════════╝
```

### 3. Marcar como Listo

Escribe `r` y presiona Enter.

### 4. Conectar Cliente 2

En otra terminal:
```powershell
java -jar game-client.jar
```

### 5. Marcar Cliente 2 como Listo

Cuando ambos estén listos, el juego iniciará automáticamente.

---

## Solución de Problemas

### Error: "No se encuentra mvn"

Instala Maven:
```powershell
# Con Chocolatey
choco install maven

# O descarga desde https://maven.apache.org/
```

### Error: "Java version mismatch"

Verifica tu versión de Java:
```powershell
java -version
```

Debe ser 17 o superior. Si no, descarga desde https://adoptium.net/

### Error: "Connection refused"

1. Verifica que el servidor esté corriendo
2. Verifica el puerto (default: 8080)
3. Verifica el firewall

### Error: "Cannot find main class"

Verifica el MANIFEST.MF en el JAR:
```powershell
jar tf game-server.jar | findstr MANIFEST
jar xf game-server.jar META-INF/MANIFEST.MF
type META-INF\MANIFEST.MF
```

---

## Configuración Avanzada

### Cambiar Puerto del Servidor

```powershell
java -jar game-server.jar 9000 2 4
# puerto=9000, min=2, max=4 jugadores
```

### Conectar a Servidor Remoto

```powershell
java -jar game-client.jar 192.168.1.100 9000
```

### Habilitar Debug Logging

Editar `src/main/resources/logback.xml`:
```xml
<logger name="com.juegito" level="TRACE" />
```

---

## Archivos Generados

Después de la compilación:

```
Juegito/
├── server/
│   └── target/
│       └── game-server-1.0.0.jar
│
├── client/
│   └── target/
│       └── game-client-1.0.0.jar
│
└── logs/
    ├── server.log
    └── client.log
```

---

## Próximos Pasos

1. ✅ Servidor y cliente compilados
2. ✅ Conexión establecida
3. ✅ Lobby funcional
4. 🎮 Implementar lógica específica del juego
5. 🎨 Mejorar UI (opcional: JavaFX/Swing)
6. 🧪 Agregar tests unitarios
7. 📦 Empaquetar para distribución

---

**¡Listo para jugar!**
