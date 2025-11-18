@echo off
echo ========================================
echo Starting Weather Client...
echo ========================================
echo.

if not exist bin (
    echo ERROR: bin folder not found!
    echo Please run build.bat first
    pause
    exit /b 1
)

java -cp bin client.WeatherClient

pause
