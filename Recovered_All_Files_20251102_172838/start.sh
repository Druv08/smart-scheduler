#!/bin/bash
echo "Starting Smart Scheduler Server..."
echo "======================================"

cd "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"

echo "Compiling project..."
mvn clean compile

echo "Starting server at http://localhost:8080..."
echo "Press Ctrl+C to stop the server"
echo ""

# Run the application directly with java using Maven's dependency classpath
mvn dependency:exec -Dexec.executable="java" -Dexec.args="-classpath %classpath com.druv.scheduler.SmartSchedulerApplication"
