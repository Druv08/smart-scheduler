@echo off
echo Starting Smart Scheduler Spring Boot Server...
echo.
echo Server will be available at: http://localhost:8080
echo Press Ctrl+C to stop the server
echo.

cd /d "%~dp0"
mvn compile exec:java -Dexec.mainClass="com.druv.scheduler.SmartSchedulerApplication"
pause
