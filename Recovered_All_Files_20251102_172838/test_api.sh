#!/bin/bash

# Smart Scheduler API Test Script

BASE_URL="http://localhost:8080/api"

echo "=== Smart Scheduler Backend API Tests ==="

# Test 1: Login with admin credentials
echo "1. Testing login..."
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  "${BASE_URL}/auth/login")

echo "Login Response: $LOGIN_RESPONSE"

# Extract token from response (assuming JSON format)
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | sed 's/"token":"\([^"]*\)"/\1/')
echo "Extracted Token: $TOKEN"

if [ -z "$TOKEN" ]; then
    echo "ÔØî Login failed - no token received"
    exit 1
else
    echo "Ô£à Login successful"
fi

# Test 2: Get dashboard stats
echo -e "\n2. Testing dashboard stats..."
STATS_RESPONSE=$(curl -s -X GET \
  -H "Authorization: $TOKEN" \
  "${BASE_URL}/dashboard/stats")

echo "Stats Response: $STATS_RESPONSE"

# Test 3: Get users list
echo -e "\n3. Testing users list..."
USERS_RESPONSE=$(curl -s -X GET \
  -H "Authorization: $TOKEN" \
  "${BASE_URL}/users")

echo "Users Response: $USERS_RESPONSE"

# Test 4: Get courses list
echo -e "\n4. Testing courses list..."
COURSES_RESPONSE=$(curl -s -X GET \
  -H "Authorization: $TOKEN" \
  "${BASE_URL}/courses")

echo "Courses Response: $COURSES_RESPONSE"

# Test 5: Get rooms list
echo -e "\n5. Testing rooms list..."
ROOMS_RESPONSE=$(curl -s -X GET \
  -H "Authorization: $TOKEN" \
  "${BASE_URL}/rooms")

echo "Rooms Response: $ROOMS_RESPONSE"

# Test 6: Add a new course
echo -e "\n6. Testing add course..."
ADD_COURSE_RESPONSE=$(curl -s -X POST \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"code": "CS401", "name": "Advanced Algorithms", "faculty": "admin", "maxStudents": 20}' \
  "${BASE_URL}/courses")

echo "Add Course Response: $ADD_COURSE_RESPONSE"

# Test 7: Add a new room
echo -e "\n7. Testing add room..."
ADD_ROOM_RESPONSE=$(curl -s -X POST \
  -H "Authorization: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "Conference Room", "capacity": 15}' \
  "${BASE_URL}/rooms")

echo "Add Room Response: $ADD_ROOM_RESPONSE"

# Test 8: Get timetable
echo -e "\n8. Testing timetable..."
TIMETABLE_RESPONSE=$(curl -s -X GET \
  -H "Authorization: $TOKEN" \
  "${BASE_URL}/timetable")

echo "Timetable Response: $TIMETABLE_RESPONSE"

echo -e "\n=== Tests Completed ==="
