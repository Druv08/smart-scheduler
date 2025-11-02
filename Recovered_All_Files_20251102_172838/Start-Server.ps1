# Smart Scheduler Server Startup Script
Write-Host "Starting Smart Scheduler..." -ForegroundColor Green

# Navigate to project directory
Set-Location "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"

# Set Java environment
$env:JAVA_HOME = "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\jdk21\jdk-21.0.5+11"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Compiling project..." -ForegroundColor Yellow
& mvn compile

Write-Host "Starting server on http://localhost:8080..." -ForegroundColor Green
& java -cp "target/classes;$env:USERPROFILE\.m2\repository\org\springframework\boot\spring-boot-starter-web\3.5.0\*;$env:USERPROFILE\.m2\repository\org\springframework\boot\spring-boot-starter\3.5.0\*;$env:USERPROFILE\.m2\repository\org\springframework\boot\spring-boot\3.5.0\*;$env:USERPROFILE\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.5.0\*;$env:USERPROFILE\.m2\repository\org\springframework\spring-core\6.2.0\*;$env:USERPROFILE\.m2\repository\org\springframework\spring-context\6.2.0\*;$env:USERPROFILE\.m2\repository\org\springframework\spring-web\6.2.0\*;$env:USERPROFILE\.m2\repository\org\springframework\spring-webmvc\6.2.0\*;$env:USERPROFILE\.m2\repository\org\xerial\sqlite-jdbc\3.42.0.0\*" com.druv.scheduler.SmartSchedulerApplication
