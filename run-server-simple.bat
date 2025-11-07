@echo off
cd /d "%~dp0"
echo Starting Smart Scheduler...
echo Current directory: %CD%
mvn exec:java
pause