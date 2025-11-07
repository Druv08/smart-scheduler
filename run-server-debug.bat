@echo off
echo Starting Smart Scheduler Server with Error Monitoring...
cd /d "c:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"

echo Current Directory: %CD%
echo Checking JAR file...
if not exist "target\smart-scheduler-1.0-SNAPSHOT.jar" (
    echo ERROR: JAR file not found!
    pause
    exit /b 1
)

echo JAR file found. Starting application...
echo.
java -Dserver.port=8080 -Dspring.profiles.active=default -jar target/smart-scheduler-1.0-SNAPSHOT.jar

echo.
echo Server process ended. Exit code: %ERRORLEVEL%
if %ERRORLEVEL% neq 0 (
    echo ERROR: Server exited with error code %ERRORLEVEL%
) else (
    echo Server shutdown normally
)
pause