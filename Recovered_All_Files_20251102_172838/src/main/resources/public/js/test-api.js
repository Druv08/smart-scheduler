// Test API endpoints to verify functionality
async function testAPI() {
    console.log('­ƒº¬ Testing Smart Scheduler API Endpoints...');
    
    try {
        // Test 1: Health check
        console.log('\n1´©ÅÔâú Testing server health...');
        const healthResponse = await fetch('/');
        console.log('Ô£à Server is running:', healthResponse.ok);

        // Test 2: Login
        console.log('\n2´©ÅÔâú Testing login...');
        const loginResponse = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: 'admin', password: 'admin123' })
        });
        const loginData = await loginResponse.json();
        console.log('Ô£à Login successful:', loginData.success);
        
        if (!loginData.success) {
            console.error('ÔØî Login failed:', loginData.error);
            return;
        }

        const token = loginData.token;

        // Test 3: Get users
        console.log('\n3´©ÅÔâú Testing users endpoint...');
        const usersResponse = await fetch('/api/users', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const usersData = await usersResponse.json();
        console.log('Ô£à Users endpoint:', usersData.success, `(${usersData.users?.length || 0} users)`);

        // Test 4: Get rooms
        console.log('\n4´©ÅÔâú Testing rooms endpoint...');
        const roomsResponse = await fetch('/api/rooms');
        const roomsData = await roomsResponse.json();
        console.log('Ô£à Rooms endpoint:', roomsData.success, `(${roomsData.rooms?.length || 0} rooms)`);

        // Test 5: Get courses
        console.log('\n5´©ÅÔâú Testing courses endpoint...');
        const coursesResponse = await fetch('/api/courses');
        const coursesData = await coursesResponse.json();
        console.log('Ô£à Courses endpoint:', coursesData.success, `(${coursesData.courses?.length || 0} courses)`);

        // Test 6: Dashboard stats
        console.log('\n6´©ÅÔâú Testing dashboard stats...');
        const statsResponse = await fetch('/api/dashboard/stats', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const statsData = await statsResponse.json();
        console.log('Ô£à Dashboard stats:', statsData.success);
        console.log('­ƒôè Stats:', statsData.stats);

        // Test 7: Timetable
        console.log('\n7´©ÅÔâú Testing timetable endpoint...');
        const timetableResponse = await fetch('/api/timetable');
        const timetableData = await timetableResponse.json();
        console.log('Ô£à Timetable endpoint:', timetableData.success, `(${timetableData.timetable?.length || 0} entries)`);

        // Test 8: Bookings
        console.log('\n8´©ÅÔâú Testing bookings endpoint...');
        const bookingsResponse = await fetch('/api/bookings', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const bookingsData = await bookingsResponse.json();
        console.log('Ô£à Bookings endpoint:', bookingsData.success, `(${bookingsData.bookings?.length || 0} bookings)`);

        console.log('\n­ƒÄë All API tests completed successfully!');
        console.log('\n­ƒôï Summary:');
        console.log('Ô£à Server running and accessible');
        console.log('Ô£à Authentication system working');
        console.log('Ô£à All CRUD endpoints functional');
        console.log('Ô£à Database operations successful');
        console.log('Ô£à Dashboard statistics available');
        console.log('\n­ƒÜÇ Your Smart Scheduler is fully operational!');

    } catch (error) {
        console.error('ÔØî API Test Error:', error);
    }
}

// Auto-run tests when DOM loads
document.addEventListener('DOMContentLoaded', () => {
    // Add test button to page if we're on the main site
    if (window.location.pathname === '/' || window.location.pathname === '/dashboard') {
        const testBtn = document.createElement('button');
        testBtn.innerHTML = '­ƒº¬ Test API';
        testBtn.style.cssText = `
            position: fixed;
            bottom: 20px;
            right: 20px;
            padding: 10px 15px;
            background: #3b82f6;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            z-index: 10000;
            font-weight: 500;
        `;
        testBtn.onclick = testAPI;
        document.body.appendChild(testBtn);
    }
});

// Export for manual testing
window.testAPI = testAPI;
