@echo off
echo ========================================
echo Weather App - Quick Build Script
echo ========================================
echo.

echo [1/3] Cleaning old files...
if exist bin rmdir /s /q bin
mkdir bin
echo Done!
echo.

echo [2/3] Compiling all Java files...
javac -d bin -sourcepath src src/server/*.java src/client/*.java src/shared/*.java
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!
echo.

echo [3/3] Build complete!
echo.
echo ========================================
echo You can now run:
echo   - Server: run_server.bat
echo   - Client: run_client.bat
echo ========================================
echo.
pause
