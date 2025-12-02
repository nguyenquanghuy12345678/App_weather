@echo off
echo ========================================
echo Testing Database Synchronization
echo ========================================
echo.

REM Check if weather.db exists
if exist weather.db (
    echo [OK] weather.db found
    echo.
    
    REM Use sqlite3 to check tables
    echo Checking database schema...
    sqlite3 weather.db ".schema"
    echo.
    
    echo Checking favorites table...
    sqlite3 weather.db "SELECT COUNT(*) as total_favorites FROM favorites;"
    sqlite3 weather.db "SELECT * FROM favorites LIMIT 5;"
    echo.
    
    echo Checking community_reports table...
    sqlite3 weather.db "SELECT COUNT(*) as total_reports FROM community_reports;"
    sqlite3 weather.db "SELECT location, accuracy, username, timestamp FROM community_reports ORDER BY timestamp DESC LIMIT 10;"
    echo.
    
    echo Checking search_history table...
    sqlite3 weather.db "SELECT COUNT(*) as total_history FROM search_history;"
    echo.
) else (
    echo [WARNING] weather.db not found
    echo Database will be created on first server/client run
)

echo.
echo ========================================
echo Test Complete
echo ========================================
pause
