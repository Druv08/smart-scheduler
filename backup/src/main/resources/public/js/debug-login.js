// Debug Login Test
console.log('Testing login flow...');

async function testLogin() {
    try {
        console.log('Making login request...');
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                username: 'admin',
                password: 'admin123456'
            })
        });

        console.log('Login response status:', response.status);
        const data = await response.json();
        console.log('Login response data:', data);

        if (data.success) {
            console.log('Login successful, testing session...');
            
            // Test session endpoint
            const sessionResponse = await fetch('/api/session', {
                method: 'GET',
                credentials: 'include'
            });
            
            console.log('Session response status:', sessionResponse.status);
            const sessionData = await sessionResponse.json();
            console.log('Session response data:', sessionData);
        }
    } catch (error) {
        console.error('Test failed:', error);
    }
}

// Run test after page loads
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', testLogin);
} else {
    testLogin();
}