// Profile Settings JavaScript
document.addEventListener("DOMContentLoaded", async () => {
    // Elements
    const personalInfoForm = document.getElementById("personalInfoForm");
    const passwordForm = document.getElementById("passwordForm");
    const fullNameInput = document.getElementById("fullName");
    const emailInput = document.getElementById("email");
    const phoneInput = document.getElementById("phone");
    const departmentInput = document.getElementById("department");
    const roleInput = document.getElementById("role");
    const currentPasswordInput = document.getElementById("currentPassword");
    const newPasswordInput = document.getElementById("newPassword");
    const confirmPasswordInput = document.getElementById("confirmPassword");

    // Authentication token
    const getAuthToken = () => localStorage.getItem("authToken") || localStorage.getItem("token");

    // Show loading state
    function showLoading(input) {
        input.classList.add("loading");
    }

    // Hide loading state
    function hideLoading(input) {
        input.classList.remove("loading");
    }

    // Show message
    function showMessage(message, type = "success") {
        // Remove existing messages
        document.querySelectorAll('.message').forEach(msg => msg.remove());
        
        const messageEl = document.createElement("div");
        messageEl.className = `message ${type}-message`;
        messageEl.textContent = message;
        messageEl.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 20px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 1000;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            background-color: ${type === 'success' ? '#22c55e' : '#ef4444'};
        `;
        
        document.body.appendChild(messageEl);
        
        setTimeout(() => {
            messageEl.remove();
        }, 4000);
    }

    // Password strength checker
    function checkPasswordStrength(password) {
        const strengthBars = document.querySelectorAll('.strength-bar');
        if (!strengthBars.length) return;

        let strength = 0;
        if (password.length >= 8) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        strengthBars.forEach((bar, index) => {
            bar.className = 'strength-bar';
            if (index < Math.ceil(strength / 2)) {
                if (strength <= 2) bar.classList.add('weak');
                else if (strength <= 3) bar.classList.add('medium');
                else bar.classList.add('strong');
            }
        });
    }

    // Add password strength indicator
    function addPasswordStrengthIndicator() {
        const passwordGroup = newPasswordInput.closest('.form-group');
        if (!passwordGroup.querySelector('.password-strength')) {
            const strengthHTML = `
                <div class="password-strength">
                    <div class="strength-bar"></div>
                    <div class="strength-bar"></div>
                    <div class="strength-bar"></div>
                </div>
            `;
            passwordGroup.insertAdjacentHTML('beforeend', strengthHTML);
        }
    }

    // Load user information
    async function loadUserInfo() {
        const token = getAuthToken();
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        try {
            showLoading(fullNameInput);
            
            const response = await fetch('/api/profile/current', {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 401) {
                localStorage.removeItem("authToken");
                localStorage.removeItem("token");
                window.location.href = '/login.html';
                return;
            }

            if (!response.ok) {
                throw new Error('Failed to load user information');
            }

            const user = await response.json();
            
            // Populate form fields
            fullNameInput.value = user.name || user.username || '';
            emailInput.value = user.email || '';
            phoneInput.value = user.phone || '';
            departmentInput.value = user.department || '';
            roleInput.value = user.role || '';
            
        } catch (error) {
            console.error('Error loading user info:', error);
            showMessage('Failed to load user information', 'error');
        } finally {
            hideLoading(fullNameInput);
        }
    }

    // Update personal information
    async function updatePersonalInfo(event) {
        event.preventDefault();
        
        const token = getAuthToken();
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        const submitBtn = personalInfoForm.querySelector('.btn-primary');
        const originalText = submitBtn.textContent;
        
        try {
            submitBtn.textContent = 'Saving...';
            submitBtn.disabled = true;

            const updatedUser = {
                name: fullNameInput.value.trim(),
                phone: phoneInput.value.trim()
            };

            const response = await fetch('/api/profile/update', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedUser)
            });

            const result = await response.json();
            
            if (result.success) {
                showMessage('Profile updated successfully', 'success');
            } else {
                showMessage(result.error || 'Failed to update profile', 'error');
            }
            
        } catch (error) {
            console.error('Error updating profile:', error);
            showMessage('Failed to update profile', 'error');
        } finally {
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    }

    // Change password
    async function changePassword(event) {
        event.preventDefault();
        
        const token = getAuthToken();
        if (!token) {
            window.location.href = '/login.html';
            return;
        }

        // Validate password match
        if (newPasswordInput.value !== confirmPasswordInput.value) {
            showMessage('New passwords do not match', 'error');
            confirmPasswordInput.focus();
            return;
        }

        // Validate password length
        if (newPasswordInput.value.length < 8) {
            showMessage('New password must be at least 8 characters long', 'error');
            newPasswordInput.focus();
            return;
        }

        const submitBtn = passwordForm.querySelector('.btn-primary');
        const originalText = submitBtn.textContent;
        
        try {
            submitBtn.textContent = 'Updating...';
            submitBtn.disabled = true;

            const passwordData = {
                currentPassword: currentPasswordInput.value,
                newPassword: newPasswordInput.value
            };

            const response = await fetch('/api/profile/password', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(passwordData)
            });

            const result = await response.json();
            
            if (result.success) {
                showMessage('Password changed successfully', 'success');
                passwordForm.reset();
            } else {
                showMessage(result.error || 'Failed to change password', 'error');
            }
            
        } catch (error) {
            console.error('Error changing password:', error);
            showMessage('Failed to change password', 'error');
        } finally {
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    }

    // Event listeners
    personalInfoForm.addEventListener('submit', updatePersonalInfo);
    passwordForm.addEventListener('submit', changePassword);
    
    // Password strength checking
    newPasswordInput.addEventListener('input', () => {
        addPasswordStrengthIndicator();
        checkPasswordStrength(newPasswordInput.value);
    });

    // Password confirmation validation
    confirmPasswordInput.addEventListener('input', () => {
        const isValid = newPasswordInput.value === confirmPasswordInput.value;
        const group = confirmPasswordInput.closest('.form-group');
        
        group.classList.toggle('error', !isValid && confirmPasswordInput.value !== '');
        group.classList.toggle('success', isValid && confirmPasswordInput.value !== '');
    });

    // Form reset handlers
    personalInfoForm.addEventListener('reset', () => {
        setTimeout(() => loadUserInfo(), 100); // Reload original data
    });

    passwordForm.addEventListener('reset', () => {
        // Clear any validation states
        document.querySelectorAll('.form-group').forEach(group => {
            group.classList.remove('success', 'error');
        });
    });

    // Initialize
    await loadUserInfo();
    addPasswordStrengthIndicator();
});
