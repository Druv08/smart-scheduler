#!/usr/bin/env pwsh
Write-Host "🚀 Starting Smart Scheduler with Fresh Build..." -ForegroundColor Green

# Kill any existing Java processes
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Start-Sleep 2

# Start the server in background
$job = Start-Job -ScriptBlock {
    Set-Location "C:\Users\druvk\OneDrive\Desktop\CODE\Smart_Scheduler\smart-scheduler"
    mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
}

Write-Host "⏳ Waiting for server to start..." -ForegroundColor Yellow
Start-Sleep 15

# Test URLs
Write-Host "🧪 Testing URLs..." -ForegroundColor Cyan
$urls = @(
    @{url="http://localhost:8080/"; name="Homepage"},
    @{url="http://localhost:8080/login.html"; name="Login"},
    @{url="http://localhost:8080/dashboard.html"; name="Dashboard"},
    @{url="http://localhost:8080/profile-settings.html"; name="Profile"}
)

foreach ($test in $urls) {
    try {
        $response = Invoke-WebRequest -Uri $test.url -UseBasicParsing -TimeoutSec 5
        Write-Host "✅ $($test.name): Status $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Host "❌ $($test.name): Error" -ForegroundColor Red
    }
}

Write-Host "🎯 Server is running! Access at: http://localhost:8080" -ForegroundColor Green
Write-Host "🔄 To stop: Get-Job | Remove-Job -Force" -ForegroundColor Yellow

# Keep job running
Write-Host "📊 Job Status:" -ForegroundColor Cyan
Get-Job