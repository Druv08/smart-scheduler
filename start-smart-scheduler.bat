@echo off
echo Starting Smart Scheduler Server...
cd /d "c:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"
echo Current directory: %CD%
echo Starting Java application...
java -jar target/smart-scheduler-1.0-SNAPSHOT.jar
pause