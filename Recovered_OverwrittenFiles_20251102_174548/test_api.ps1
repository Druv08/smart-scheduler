# Smart Scheduler API Test Script (PowerShell)

$BASE_URL = "http://localhost:8080/api"

Write-Host "=== Smart Scheduler Backend API Tests ==="

# Test 1: Login with admin credentials
Write-Host "1. Testing login..."
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "Ô£à Login successful"
    Write-Host "Login Response: $($loginResponse | ConvertTo-Json)"
    
    $token = $loginResponse.token
    if (-not $token) {
        Write-Host "ÔØî No token received"
        exit 1
    }
    
    $headers = @{ "Authorization" = $token }
    
} catch {
    Write-Host "ÔØî Login failed: $($_.Exception.Message)"
    exit 1
}

# Test 2: Get dashboard stats
Write-Host "`n2. Testing dashboard stats..."
try {
    $statsResponse = Invoke-RestMethod -Uri "$BASE_URL/dashboard/stats" -Method GET -Headers $headers
    Write-Host "Ô£à Dashboard stats retrieved"
    Write-Host "Stats: $($statsResponse | ConvertTo-Json)"
} catch {
    Write-Host "ÔØî Dashboard stats failed: $($_.Exception.Message)"
}

# Test 3: Get users list
Write-Host "`n3. Testing users list..."
try {
    $usersResponse = Invoke-RestMethod -Uri "$BASE_URL/users" -Method GET -Headers $headers
    Write-Host "Ô£à Users list retrieved (Count: $($usersResponse.Count))"
} catch {
    Write-Host "ÔØî Users list failed: $($_.Exception.Message)"
}

# Test 4: Get courses list
Write-Host "`n4. Testing courses list..."
try {
    $coursesResponse = Invoke-RestMethod -Uri "$BASE_URL/courses" -Method GET -Headers $headers
    Write-Host "Ô£à Courses list retrieved (Count: $($coursesResponse.Count))"
} catch {
    Write-Host "ÔØî Courses list failed: $($_.Exception.Message)"
}

# Test 5: Get rooms list
Write-Host "`n5. Testing rooms list..."
try {
    $roomsResponse = Invoke-RestMethod -Uri "$BASE_URL/rooms" -Method GET -Headers $headers
    Write-Host "Ô£à Rooms list retrieved (Count: $($roomsResponse.Count))"
} catch {
    Write-Host "ÔØî Rooms list failed: $($_.Exception.Message)"
}

# Test 6: Add a new course
Write-Host "`n6. Testing add course..."
$courseBody = @{
    code = "CS401"
    name = "Advanced Algorithms"
    faculty = "admin"
    maxStudents = 20
} | ConvertTo-Json

try {
    $addCourseResponse = Invoke-RestMethod -Uri "$BASE_URL/courses" -Method POST -Body $courseBody -Headers $headers -ContentType "application/json"
    Write-Host "Ô£à Course added successfully"
    Write-Host "Response: $($addCourseResponse | ConvertTo-Json)"
} catch {
    Write-Host "ÔØî Add course failed: $($_.Exception.Message)"
}

# Test 7: Add a new room
Write-Host "`n7. Testing add room..."
$roomBody = @{
    name = "Conference Room"
    capacity = 15
} | ConvertTo-Json

try {
    $addRoomResponse = Invoke-RestMethod -Uri "$BASE_URL/rooms" -Method POST -Body $roomBody -Headers $headers -ContentType "application/json"
    Write-Host "Ô£à Room added successfully"
    Write-Host "Response: $($addRoomResponse | ConvertTo-Json)"
} catch {
    Write-Host "ÔØî Add room failed: $($_.Exception.Message)"
}

# Test 8: Get timetable
Write-Host "`n8. Testing timetable..."
try {
    $timetableResponse = Invoke-RestMethod -Uri "$BASE_URL/timetable" -Method GET -Headers $headers
    Write-Host "Ô£à Timetable retrieved (Count: $($timetableResponse.Count))"
} catch {
    Write-Host "ÔØî Timetable failed: $($_.Exception.Message)"
}

Write-Host "`n=== Tests Completed ==="
