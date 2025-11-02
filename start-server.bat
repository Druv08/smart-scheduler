@echo off
echo Starting Smart Scheduler...

REM Set JAVA_HOME to use JDK 21
set "JAVA_HOME=C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\jdk21\jdk-21.0.5+11"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Change to the project directory
cd /d "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"

REM Compile the project
echo Compiling project...
mvn compile

REM Run the application with all dependencies
echo Starting server on http://localhost:8080...
mvn exec:java -Dexec.mainClass="com.druv.scheduler.SmartSchedulerApplication"
