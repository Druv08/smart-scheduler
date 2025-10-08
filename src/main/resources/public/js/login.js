// Smart Scheduler - Login Handler
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const togglePassword = document.querySelector('.toggle-password');
    const passwordInput = document.getElementById('password');

    // Toggle password visibility
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            
            // Toggle the eye icon
            const eyeIcon = this.querySelector('.eye-icon path');
            if (type === 'text') {
                eyeIcon.setAttribute('d', 'M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z');
            }
        });
    }

    // Handle form submission
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            
            // Basic validation
            if (!email || !password) {
                alert('Please fill in all fields');
                return;
            }
            
            // For now, simulate a successful login and redirect to dashboard
            // In a real application, this would make an API call to authenticate
            const loginBtn = document.querySelector('.btn-login');
            const originalText = loginBtn.innerHTML;
            
            // Show loading state
            loginBtn.innerHTML = '<span>Logging in...</span>';
            loginBtn.disabled = true;
            
            // Simulate API call delay
            setTimeout(() => {
                // For demonstration, any email/password combination works
                // In production, this would validate against your backend
                if (email && password) {
                    // Store login state (in production, use proper session management)
                    localStorage.setItem('isLoggedIn', 'true');
                    localStorage.setItem('userEmail', email);
                    
                    // Redirect to dashboard
                    window.location.href = 'dashboard.html';
                } else {
                    // Reset button state
                    loginBtn.innerHTML = originalText;
                    loginBtn.disabled = false;
                    alert('Invalid credentials. Please try again.');
                }
            }, 1000);
        });
    }
});

// Check if user is already logged in when visiting login page
document.addEventListener('DOMContentLoaded', function() {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    if (isLoggedIn === 'true' && window.location.pathname.includes('login.html')) {
        window.location.href = 'dashboard.html';
    }
});