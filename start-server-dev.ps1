Set-Location "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"
Write-Host "Starting Smart Scheduler Server..." -ForegroundColor Cyan
Write-Host "Location: $PWD" -ForegroundColor Yellow
mvn spring-boot:run -Dspring-boot.run.profiles=dev
