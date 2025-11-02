#!/bin/bash
echo "=== Smart Scheduler Server Startup ==="
echo "Druv's SRM University Timetable System"
echo "======================================"

# Navigate to project directory
cd "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"

echo "­ƒöº Setting up Java environment..."
export JAVA_HOME="C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\jdk21\jdk-21.0.5+11"
export PATH="$JAVA_HOME\bin:$PATH"

echo "­ƒôª Building project..."
mvn clean compile -q

echo "­ƒÜÇ Starting Smart Scheduler Server..."
echo "Server will be available at: http://localhost:8080"
echo "­ƒôÜ Your SRM CSE Timetable Data is Ready!"
echo ""
echo "Features available:"
echo "  Ô£à Dashboard - Personalized for Druv"
echo "  Ô£à Timetable - Real SRM schedule"
echo "  Ô£à Courses - 7 SRM subjects with real professors"
echo "  Ô£à Room Management"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Get the dependency classpath and run the application
java -cp "target/classes:$HOME/.m2/repository/org/springframework/boot/spring-boot-starter-web/3.5.0/*:$HOME/.m2/repository/org/springframework/boot/spring-boot-starter/3.5.0/*:$HOME/.m2/repository/org/springframework/boot/spring-boot/3.5.0/*:$HOME/.m2/repository/org/springframework/boot/spring-boot-autoconfigure/3.5.0/*:$HOME/.m2/repository/org/springframework/spring-core/6.2.0/*:$HOME/.m2/repository/org/springframework/spring-context/6.2.0/*:$HOME/.m2/repository/org/springframework/spring-web/6.2.0/*:$HOME/.m2/repository/org/springframework/spring-webmvc/6.2.0/*:$HOME/.m2/repository/org/xerial/sqlite-jdbc/3.42.0.0/*" com.druv.scheduler.SmartSchedulerApplication
