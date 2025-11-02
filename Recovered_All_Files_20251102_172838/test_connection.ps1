# Test Smart Scheduler API Connectivity

Write-Host "=== Testing Smart Scheduler API Connection ==="

# Test 1: Basic connectivity test
Write-Host "1. Testing basic server connectivity..."
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080" -Method GET -TimeoutSec 10
    if ($response.StatusCode -eq 200) {
        Write-Host "Ô£à Server is running and accessible at localhost:8080"
    }
} catch {
    Write-Host "ÔØî Server connectivity failed: $($_.Exception.Message)"
    exit 1
}

# Test 2: Test API login endpoint
Write-Host "`n2. Testing API login endpoint..."
try {
    $loginBody = @{
        username = "admin"
        password = "admin123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -TimeoutSec 10
    
    if ($loginResponse.success) {
        Write-Host "Ô£à Login successful!"
        Write-Host "Token received: $($loginResponse.token.Substring(0,10))..."
        
        # Test 3: Test authenticated endpoint
        Write-Host "`n3. Testing authenticated endpoint (dashboard stats)..."
        $headers = @{ "Authorization" = $loginResponse.token }
        
        $statsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/dashboard/stats" -Method GET -Headers $headers -TimeoutSec 10
        
        Write-Host "Ô£à Dashboard stats retrieved successfully!"
        Write-Host "Total Users: $($statsResponse.totalUsers)"
        Write-Host "Total Courses: $($statsResponse.totalCourses)" 
        Write-Host "Total Rooms: $($statsResponse.totalRooms)"
        Write-Host "Upcoming Classes: $($statsResponse.upcomingClasses)"
        
    } else {
        Write-Host "ÔØî Login failed: $($loginResponse.message)"
    }
} catch {
    Write-Host "ÔØî API test failed: $($_.Exception.Message)"
}

Write-Host "`n=== Connection Test Complete ==="
