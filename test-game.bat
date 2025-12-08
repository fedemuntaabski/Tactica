@echo off
REM =====================================================
REM Script de Testing para Juegito
REM Inicia 1 servidor y 2 clientes para pruebas
REM =====================================================

echo.
echo ================================================
echo  JUEGITO - Test de Servidor Multiplayer
echo ================================================
echo.
echo Este script iniciara:
echo   - 1 Servidor en puerto 8080
echo   - 2 Clientes conectandose a localhost:8080
echo.
echo Presione Ctrl+C en cualquier ventana para cerrarla
echo.
pause

REM Verificar que los JARs existen
if not exist "target\game-server-1.0.0.jar" (
    echo ERROR: No se encuentra el servidor compilado.
    echo Por favor ejecute primero: mvn clean package
    pause
    exit /b 1
)

if not exist "client-target\game-client-1.0.0.jar" (
    echo ERROR: No se encuentra el cliente compilado.
    echo Por favor ejecute primero: mvn -f client-pom.xml clean package
    pause
    exit /b 1
)

echo.
echo [1/3] Iniciando servidor en puerto 8080...
echo.
start "Juegito Server" cmd /k "java -jar target\game-server-1.0.0.jar 8080 2 4"

REM Esperar 3 segundos para que el servidor inicie
timeout /t 3 /nobreak >nul

echo [2/3] Iniciando Cliente 1...
echo.
start "Juegito Client 1" cmd /k "java -jar client-target\game-client-1.0.0.jar localhost 8080"

REM Esperar 2 segundos
timeout /t 2 /nobreak >nul

echo [3/3] Iniciando Cliente 2...
echo.
start "Juegito Client 2" cmd /k "java -jar client-target\game-client-1.0.0.jar localhost 8080"

echo.
echo ================================================
echo  Entorno de pruebas iniciado correctamente
echo ================================================
echo.
echo Instrucciones:
echo   1. Ambos clientes deben conectarse automaticamente
echo   2. El juego iniciara cuando ambos esten listos
echo   3. Haz click en los hexagonos para mover tu jugador
echo   4. Usa scroll del mouse para hacer zoom
echo   5. Cierra las ventanas cuando termines
echo.
echo Presione cualquier tecla para salir de este script...
pause >nul
