@echo off
echo.
echo ================================================
echo   Starting Smart Scheduler...
echo ================================================
echo.

cd /d "%~dp0"

REM Start Maven in a new window without waiting
start "Smart Scheduler Server" cmd /c "mvn spring-boot:run"

echo   Server is starting in background window...
echo.
timeout /t 30 /nobreak >nul

echo   Checking if server is ready...
echo.

REM Wait for server to be ready
:wait_loop
timeout /t 3 /nobreak >nul
curl -s http://localhost:8080 >nul 2>&1
if %errorlevel% equ 0 goto server_ready
echo   Still starting...
goto wait_loop

:server_ready
echo.
echo ================================================
echo   SMART SCHEDULER IS RUNNING!
echo ================================================
echo.
echo   URL: http://localhost:8080/dashboard.html
echo.
echo   Press Ctrl+Shift+R to see full-width layout!
echo.
echo   Server window: "Smart Scheduler Server"
echo   (Don't close that window)
echo.
echo ================================================
echo.

REM Open browser
start http://localhost:8080/dashboard.html

pause
