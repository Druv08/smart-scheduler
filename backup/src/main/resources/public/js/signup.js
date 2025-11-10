// Sign-up page JavaScript functionality
document.addEventListener('DOMContentLoaded', () => {

    const signupForm = document.getElementById('signupForm');
    const togglePassword = document.querySelector('.toggle-password');
    const passwordInput = document.getElementById('password');

    // Toggle password visibility
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', () => {
            const type = passwordInput.type === 'password' ? 'text' : 'password';
            passwordInput.type = type;
            
            // Update icon
            const eyeIcon = togglePassword.querySelector('.eye-icon path');
            if (type === 'text') {
                eyeIcon.setAttribute('d', 'M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z');
            } else {
                eyeIcon.setAttribute('d', 'M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z');
            }
        });
    }

    // Handle form submission
    if (signupForm) {
        signupForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData(signupForm);
            const userData = {
                username: formData.get('username'),
                password: formData.get('password'),
                role: formData.get('role')
            };

            // Basic validation
            if (!userData.username || !userData.password || !userData.role) {
                showMessage('Please fill in all fields', 'error');
                return;
            }

            if (userData.password.length < 8) {
                showMessage('Password must be at least 8 characters long', 'error');
                return;
            }

            try {
                // Show loading state
                const submitBtn = signupForm.querySelector('button[type="submit"]');
                const originalText = submitBtn.querySelector('span').textContent;
                submitBtn.querySelector('span').textContent = 'Creating Account...';
                submitBtn.disabled = true;

                const response = await fetch('/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(userData)
                });

                const data = await response.json();

                if (data.success) {
                    showMessage('Account created successfully! Redirecting to login...', 'success');
                    setTimeout(() => {
                        window.location.href = 'login.html';
                    }, 2000);
                } else {
                    showMessage(data.error || 'Failed to create account', 'error');
                }
            } catch (error) {
                console.error('Sign-up error:', error);
                showMessage('Network error. Please check your connection and try again.', 'error');
            } finally {
                // Reset button state
                const submitBtn = signupForm.querySelector('button[type="submit"]');
                submitBtn.querySelector('span').textContent = originalText;
                submitBtn.disabled = false;
            }
        });
    }

    // Add some basic form styling enhancements
    const inputs = document.querySelectorAll('input, select');
    inputs.forEach(input => {
        input.addEventListener('focus', () => {
            input.parentElement.classList.add('focused');
        });
        
        input.addEventListener('blur', () => {
            if (!input.value) {
                input.parentElement.classList.remove('focused');
            }
        });
    });

    // Function to show messages
    function showMessage(message, type) {
        // Remove existing messages
        const existingMessage = document.querySelector('.signup-message');
        if (existingMessage) {
            existingMessage.remove();
        }
        
        const messageDiv = document.createElement('div');
        messageDiv.className = `signup-message ${type}`;
        messageDiv.textContent = message;
        
        // Add some basic styling
        messageDiv.style.padding = '12px';
        messageDiv.style.marginBottom = '16px';
        messageDiv.style.borderRadius = '6px';
        messageDiv.style.fontWeight = '500';
        
        if (type === 'success') {
            messageDiv.style.backgroundColor = '#d1fae5';
            messageDiv.style.color = '#065f46';
            messageDiv.style.border = '1px solid #34d399';
        } else if (type === 'error') {
            messageDiv.style.backgroundColor = '#fee2e2';
            messageDiv.style.color = '#991b1b';
            messageDiv.style.border = '1px solid #f87171';
        }
        
        const loginBox = document.querySelector('.login-box');
        loginBox.insertBefore(messageDiv, loginBox.firstChild);
        
        // Remove message after 6 seconds
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 6000);
    }
});