// Smart Scheduler Router - Client-side routing system
class SmartSchedulerRouter {
    constructor() {
        this.routes = new Map();
        this.currentRoute = null;
        this.authToken = localStorage.getItem('authToken');
        this.user = JSON.parse(localStorage.getItem('user') || 'null');
        
        // Initialize router
        this.init();
    }

    init() {
        // Define all routes
        this.defineRoutes();
        
        // Handle browser back/forward
        window.addEventListener('popstate', () => {
            this.navigateToCurrentPath();
        });

        // Handle page load
        this.navigateToCurrentPath();
        
        // Handle navigation links
        this.attachNavigationListeners();
    }

    defineRoutes() {
        // Public routes
        this.routes.set('/', {
            title: 'Home - Smart Scheduler',
            requiresAuth: false,
            handler: () => this.loadPage('index.html')
        });

        this.routes.set('/login', {
            title: 'Login - Smart Scheduler',
            requiresAuth: false,
            handler: () => this.loadPage('login.html')
        });

        this.routes.set('/signup', {
            title: 'Sign Up - Smart Scheduler',
            requiresAuth: false,
            handler: () => this.loadPage('signup.html')
        });

        // Protected routes
        this.routes.set('/dashboard', {
            title: 'Dashboard - Smart Scheduler',
            requiresAuth: true,
            handler: () => this.loadPage('dashboard.html')
        });

        this.routes.set('/users', {
            title: 'Users - Smart Scheduler',
            requiresAuth: true,
            roles: ['admin'],
            handler: () => this.loadPage('users.html')
        });

        this.routes.set('/rooms', {
            title: 'Rooms - Smart Scheduler',
            requiresAuth: true,
            handler: () => this.loadPage('rooms.html')
        });

        this.routes.set('/courses', {
            title: 'Courses - Smart Scheduler',
            requiresAuth: true,
            handler: () => this.loadPage('courses.html')
        });

        this.routes.set('/timetable', {
            title: 'Timetable - Smart Scheduler',
            requiresAuth: true,
            handler: () => this.loadPage('timetable.html')
        });

        this.routes.set('/bookings', {
            title: 'Bookings - Smart Scheduler',
            requiresAuth: true,
            handler: () => this.loadPage('bookings.html')
        });

        this.routes.set('/profile-settings', {
            title: 'Profile Settings - Smart Scheduler',
            requiresAuth: true,
            handler: () => this.loadPage('profile-settings.html')
        });
    }

    attachNavigationListeners() {
        // Handle all navigation links
        document.addEventListener('click', (e) => {
            const link = e.target.closest('a[href]');
            if (link) {
                const href = link.getAttribute('href');
                
                // Check if it's an internal link
                if (href.startsWith('/') || (!href.includes('://') && !href.startsWith('#'))) {
                    e.preventDefault();
                    this.navigate(href);
                }
            }

            // Handle buttons with onclick navigation
            const button = e.target.closest('button[onclick]');
            if (button) {
                const onclick = button.getAttribute('onclick');
                const locationMatch = onclick.match(/location\.href\s*=\s*['"]([^'"]+)['"]/);
                if (locationMatch) {
                    e.preventDefault();
                    this.navigate(locationMatch[1]);
                }
            }
        });
    }

    async navigate(path) {
        const route = this.routes.get(path);
        
        if (!route) {
            console.warn(`Route not found: ${path}`);
            this.navigate('/');
            return;
        }

        // Check authentication
        if (route.requiresAuth && !this.isAuthenticated()) {
            this.navigate('/login');
            return;
        }

        // Check role permissions
        if (route.roles && !this.hasRole(route.roles)) {
            this.showError('Access denied. Insufficient permissions.');
            this.navigate('/dashboard');
            return;
        }

        // Update browser history
        if (window.location.pathname !== path) {
            window.history.pushState(null, route.title, path);
        }

        // Update page title
        document.title = route.title;

        // Update current route
        this.currentRoute = path;

        // Execute route handler
        try {
            await route.handler();
            this.updateActiveNavigation(path);
        } catch (error) {
            console.error(`Error loading route ${path}:`, error);
            this.showError('Failed to load page. Please try again.');
        }
    }

    async loadPage(filename) {
        try {
            const response = await fetch(`/${filename}`);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const html = await response.text();
            
            // Parse the HTML to extract just the body content
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const bodyContent = doc.body.innerHTML;
            
            // Replace current page content
            document.body.innerHTML = bodyContent;
            
            // Re-attach event listeners
            this.attachNavigationListeners();
            
            // Initialize page-specific functionality
            this.initializePageContent();
            
        } catch (error) {
            console.error(`Failed to load page ${filename}:`, error);
            this.showError(`Failed to load page: ${error.message}`);
        }
    }

    navigateToCurrentPath() {
        const path = window.location.pathname === '' ? '/' : window.location.pathname;
        this.navigate(path);
    }

    updateActiveNavigation(currentPath) {
        // Update navigation active states
        const navLinks = document.querySelectorAll('nav a, .nav a');
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href === currentPath || (currentPath === '/' && href === 'index.html')) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });
    }

    initializePageContent() {
        // Initialize based on current route
        switch (this.currentRoute) {
            case '/dashboard':
                this.initializeDashboard();
                break;
            case '/users':
                this.initializeUsers();
                break;
            case '/rooms':
                this.initializeRooms();
                break;
            case '/courses':
                this.initializeCourses();
                break;
            case '/timetable':
                this.initializeTimetable();
                break;
            case '/bookings':
                this.initializeBookings();
                break;
            case '/login':
                this.initializeLogin();
                break;
            case '/signup':
                this.initializeSignup();
                break;
        }

        // Initialize common functionality
        this.initializeCommonFeatures();
    }

    async initializeDashboard() {
        console.log('Initializing dashboard...');
        
        // Load dashboard stats
        try {
            const response = await fetch('/api/dashboard/stats', {
                headers: { 'Authorization': `Bearer ${this.authToken}` }
            });
            const data = await response.json();
            
            if (data.success) {
                this.updateDashboardStats(data.stats);
            }
        } catch (error) {
            console.error('Failed to load dashboard stats:', error);
        }

        // Initialize dashboard-specific features
        this.initializeDashboardCharts();
    }

    initializeUsers() {
        console.log('Initializing users page...');
        this.loadDataTable('/api/users', 'usersTable', 'users');
        this.setupUserManagement();
    }

    initializeRooms() {
        console.log('Initializing rooms page...');
        this.loadDataTable('/api/rooms', 'roomsTable', 'rooms');
        this.setupRoomManagement();
    }

    initializeCourses() {
        console.log('Initializing courses page...');
        this.loadDataTable('/api/courses', 'coursesTable', 'courses');
        this.setupCourseManagement();
    }

    initializeTimetable() {
        console.log('Initializing timetable page...');
        this.loadTimetableData();
        this.setupTimetableManagement();
    }

    initializeBookings() {
        console.log('Initializing bookings page...');
        this.loadDataTable('/api/bookings', 'bookingsTable', 'bookings');
        this.setupBookingManagement();
    }

    initializeLogin() {
        console.log('Initializing login page...');
        this.setupLoginForm();
    }

    initializeSignup() {
        console.log('Initializing signup page...');
        this.setupSignupForm();
    }

    initializeCommonFeatures() {
        // Profile menu
        this.setupProfileMenu();
        
        // Notifications
        this.setupNotifications();
        
        // Form validation
        this.setupFormValidation();
        
        // Loading states
        this.setupLoadingStates();
    }

    // Authentication helpers
    isAuthenticated() {
        return this.authToken && this.user;
    }

    hasRole(roles) {
        if (!this.user || !this.user.role) return false;
        return roles.includes(this.user.role);
    }

    login(token, userData) {
        this.authToken = token;
        this.user = userData;
        localStorage.setItem('authToken', token);
        localStorage.setItem('user', JSON.stringify(userData));
    }

    logout() {
        this.authToken = null;
        this.user = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        this.navigate('/login');
    }

    // Utility methods
    showError(message) {
        this.showNotification(message, 'error');
    }

    showSuccess(message) {
        this.showNotification(message, 'success');
    }

    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;
        
        // Add styles
        Object.assign(notification.style, {
            position: 'fixed',
            top: '20px',
            right: '20px',
            padding: '12px 24px',
            borderRadius: '8px',
            zIndex: '10000',
            color: 'white',
            fontWeight: '500',
            backgroundColor: type === 'error' ? '#ef4444' : type === 'success' ? '#10b981' : '#3b82f6'
        });
        
        document.body.appendChild(notification);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    // Data loading helpers
    async loadDataTable(apiEndpoint, tableId, dataKey) {
        try {
            const response = await fetch(apiEndpoint, {
                headers: this.authToken ? { 'Authorization': `Bearer ${this.authToken}` } : {}
            });
            const data = await response.json();

            const tableContainer = document.getElementById(tableId);
            if (!tableContainer) return;

            if (!data.success) {
                tableContainer.innerHTML = `<p class="error">❌ Error: ${data.error || 'Failed to load data'}</p>`;
                return;
            }

            const items = data[dataKey] || [];
            if (items.length === 0) {
                tableContainer.innerHTML = '<p class="empty-state">No data available</p>';
                return;
            }

            // Generate table HTML
            const headers = Object.keys(items[0]);
            const tableHTML = `
                <div class="table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                ${headers.map(header => `<th>${this.formatHeader(header)}</th>`).join('')}
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${items.map(item => `
                                <tr>
                                    ${headers.map(header => `<td>${this.formatCellValue(item[header])}</td>`).join('')}
                                    <td>
                                        <button class="btn-small btn-primary" onclick="router.editItem('${dataKey}', ${item.id})">Edit</button>
                                        <button class="btn-small btn-danger" onclick="router.deleteItem('${dataKey}', ${item.id})">Delete</button>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;

            tableContainer.innerHTML = tableHTML;

        } catch (error) {
            console.error(`Failed to load ${apiEndpoint}:`, error);
            const tableContainer = document.getElementById(tableId);
            if (tableContainer) {
                tableContainer.innerHTML = '<p class="error">⚠️ Failed to connect to API</p>';
            }
        }
    }

    formatHeader(header) {
        return header.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }

    formatCellValue(value) {
        if (value === null || value === undefined) return '-';
        if (typeof value === 'string' && value.includes('T')) {
            // Try to format as date
            try {
                return new Date(value).toLocaleDateString();
            } catch {
                return value;
            }
        }
        return value;
    }

    // Page-specific setup methods (to be implemented)
    setupUserManagement() {
        // User management functionality
        console.log('Setting up user management...');
    }

    setupRoomManagement() {
        // Room management functionality
        console.log('Setting up room management...');
    }

    setupCourseManagement() {
        // Course management functionality
        console.log('Setting up course management...');
    }

    setupTimetableManagement() {
        // Timetable management functionality
        console.log('Setting up timetable management...');
    }

    setupBookingManagement() {
        // Booking management functionality
        console.log('Setting up booking management...');
    }

    setupLoginForm() {
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.handleLogin(e);
            });
        }
    }

    setupSignupForm() {
        const signupForm = document.getElementById('signupForm');
        if (signupForm) {
            signupForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.handleSignup(e);
            });
        }
    }

    async handleLogin(e) {
        const formData = new FormData(e.target);
        const username = formData.get('username');
        const password = formData.get('password');

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (data.success) {
                this.login(data.token, data.user);
                this.showSuccess('Login successful!');
                this.navigate('/dashboard');
            } else {
                this.showError(data.error || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
            this.showError('Login failed. Please check your connection.');
        }
    }

    async handleSignup(e) {
        const formData = new FormData(e.target);
        const username = formData.get('username');
        const email = formData.get('email');
        const password = formData.get('password');
        const confirmPassword = formData.get('confirmPassword');

        if (password !== confirmPassword) {
            this.showError('Passwords do not match');
            return;
        }

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, email, password })
            });

            const data = await response.json();

            if (data.success) {
                this.login(data.token, data.user);
                this.showSuccess('Registration successful!');
                this.navigate('/dashboard');
            } else {
                this.showError(data.error || 'Registration failed');
            }
        } catch (error) {
            console.error('Signup error:', error);
            this.showError('Registration failed. Please check your connection.');
        }
    }

    setupProfileMenu() {
        const profileMenu = document.getElementById('profileMenu');
        if (profileMenu && this.user) {
            profileMenu.innerHTML = `
                <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(this.user.username)}&background=4f46e5&color=fff" 
                     alt="Profile" class="avatar-img">
                <div class="profile-dropdown">
                    <a href="/profile-settings">Profile Settings</a>
                    <a href="#" onclick="router.logout()">Logout</a>
                </div>
            `;
        }
    }

    setupNotifications() {
        // Notification system setup
        console.log('Setting up notifications...');
    }

    setupFormValidation() {
        // Form validation setup
        console.log('Setting up form validation...');
    }

    setupLoadingStates() {
        // Loading states setup
        console.log('Setting up loading states...');
    }

    updateDashboardStats(stats) {
        // Update dashboard statistics
        const statsElements = {
            'total-users': stats.users,
            'total-courses': stats.courses,
            'total-rooms': stats.rooms,
            'pending-bookings': stats.pending_bookings
        };

        Object.entries(statsElements).forEach(([id, value]) => {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = value;
            }
        });
    }

    initializeDashboardCharts() {
        // Initialize charts and graphs for dashboard
        console.log('Initializing dashboard charts...');
    }

    loadTimetableData() {
        // Load and display timetable data
        console.log('Loading timetable data...');
        this.loadDataTable('/api/timetable', 'timetableTable', 'timetable');
    }

    // CRUD operations
    async editItem(type, id) {
        console.log(`Editing ${type} with ID ${id}`);
        // Implement edit functionality
    }

    async deleteItem(type, id) {
        if (confirm(`Are you sure you want to delete this ${type}?`)) {
            console.log(`Deleting ${type} with ID ${id}`);
            // Implement delete functionality
        }
    }
}

// Initialize router when DOM is loaded
let router;
document.addEventListener('DOMContentLoaded', () => {
    router = new SmartSchedulerRouter();

});

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SmartSchedulerRouter;
}