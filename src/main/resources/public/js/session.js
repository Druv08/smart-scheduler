// Smart Scheduler - Session Management
class SessionManager {
    constructor() {
        this.sessionInfo = null;
        this.init();
    }

    async init() {
        try {
            // Validate session on page load
            await this.validateSession();
        } catch (error) {
            console.error('Session initialization failed:', error);
        }
    }

    // Session validation
    async validateSession() {
        try {
            const response = await fetch('/api/session', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                this.sessionInfo = await response.json();
                if (this.sessionInfo.authenticated) {
                    console.log('Session valid:', this.sessionInfo);
                    return this.sessionInfo;
                } else {
                    throw new Error('Not authenticated');
                }
            } else {
                throw new Error('Session validation failed');
            }
        } catch (error) {
            console.warn('Session validation error:', error);
            this.redirectToLogin();
            return null;
        }
    }

    // Redirect to login
    redirectToLogin() {
        if (!window.location.pathname.includes('login.html') && 
            !window.location.pathname.includes('index.html') &&
            window.location.pathname !== '/') {
            console.log('Redirecting to login due to invalid session');
            window.location.href = '/login.html';
        }
    }

    // Logout function
    async logout() {
        try {
            const response = await fetch('/api/logout', {
                method: 'POST',
                credentials: 'include'
            });
            
            this.sessionInfo = null;
            window.location.href = '/login.html';
        } catch (error) {
            console.error('Logout error:', error);
            // Force redirect even if logout fails
            this.sessionInfo = null;
            window.location.href = '/login.html';
        }
    }

    // Get current user info
    getCurrentUser() {
        return this.sessionInfo;
    }

    // Check if user has specific role
    hasRole(role) {
        return this.sessionInfo && this.sessionInfo.role === role;
    }

    // Check if user is admin
    isAdmin() {
        return this.hasRole('admin');
    }

    // Check if user is faculty
    isFaculty() {
        return this.hasRole('faculty') || this.hasRole('professor');
    }

    // Check if user is student
    isStudent() {
        return this.hasRole('student');
    }
}

// Global session manager instance
window.sessionManager = new SessionManager();

// Auto-validate session for pages that require authentication
document.addEventListener('DOMContentLoaded', function() {
    // Check if page requires authentication
    const requiresAuth = document.body.hasAttribute('data-require-auth');
    
    if (requiresAuth) {
        console.log('Page requires authentication, validating session...');
        window.sessionManager.validateSession();
    }

    // Setup logout buttons
    const logoutButtons = document.querySelectorAll('[data-logout]');
    logoutButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            window.sessionManager.logout();
        });
    });
});

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SessionManager;
}