// Smart Scheduler Main Application
console.log("✅ app.js loaded successfully!");

// Smart Scheduler App Class
class SmartSchedulerApp {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.initializeModules();
    }

    setupEventListeners() {
        // Global event listeners
        document.addEventListener('click', this.handleGlobalClick.bind(this));
        document.addEventListener('submit', this.handleGlobalSubmit.bind(this));
    }

    handleGlobalClick(e) {
        // Handle modal closes
        if (e.target.classList.contains('modal-overlay')) {
            this.closeModal(e.target);
        }

        // Handle dropdown toggles  
        if (e.target.closest('.dropdown-toggle')) {
            this.toggleDropdown(e.target.closest('.dropdown-toggle'));
        }
    }

    handleGlobalSubmit(e) {
        // Prevent default form submission and handle via API
        const form = e.target;
        if (form.classList.contains('api-form')) {
            e.preventDefault();
            this.handleFormSubmission(form);
        }
    }

    async handleFormSubmission(form) {
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        const endpoint = form.dataset.endpoint;
        const method = form.dataset.method || 'POST';
        
        try {
            const response = await this.api(endpoint, method, data);
            if (response.success) {
                this.showNotification('Operation completed successfully', 'success');
                this.initializeModules();
            } else {
                this.showNotification(response.error || 'Operation failed', 'error');
            }
        } catch (error) {
            this.showNotification('Network error occurred', 'error');
        }
    }

    initializeModules() {
        // Initialize different modules based on current page
        if (window.location.pathname.includes("users")) {
            this.loadData("users", "usersTable", "users");
        } else if (window.location.pathname.includes("rooms")) {
            this.loadData("rooms", "roomsTable", "rooms");
        } else if (window.location.pathname.includes("courses")) {
            this.loadData("courses", "coursesTable", "courses");
        }
        
        this.initializeUsers();
        this.initializeRooms();
        this.initializeCourses();
    }

    // Users Module
    async initializeUsers() {
        const usersContainer = document.getElementById('users');
        if (!usersContainer) return;

        try {
            const response = await this.api('/api/users');
            if (response.success && response.users) {
                this.renderUsers(response.users, usersContainer);
            } else {
                usersContainer.innerHTML = '<p class="text-red-500">Error loading users</p>';
            }
        } catch (error) {
            console.error('Failed to load users:', error);
            usersContainer.innerHTML = '<p class="text-red-500">Failed to load users</p>';
        }
    }

    renderUsers(users, container) {
        const usersList = users.map(user => `
            <div class="p-4 mb-2 bg-gray-50 rounded-lg">
                <span class="font-medium">${user.username}</span>
                <span class="ml-2 px-2 py-1 text-sm rounded-full ${this.getRoleBadgeColor(user.role)}">
                    ${user.role}
                </span>
            </div>
        `).join('');
        
        container.innerHTML = usersList;
    }

    getRoleBadgeColor(role) {
        switch(role) {
            case 'ADMIN':
            case 'admin':
                return 'bg-red-100 text-red-800';
            case 'FACULTY':
            case 'faculty':
                return 'bg-blue-100 text-blue-800';
            case 'STUDENT':
            case 'student':
                return 'bg-green-100 text-green-800';
            default:
                return 'bg-gray-100 text-gray-800';
        }
    }

    // Rooms Module
    async initializeRooms() {
        const roomsContainer = document.getElementById('rooms');
        if (!roomsContainer) return;

        try {
            const response = await this.api('/api/rooms');
            if (response.success && response.rooms) {
                this.renderRooms(response.rooms, roomsContainer);
            }
        } catch (error) {
            console.error('Failed to load rooms:', error);
        }
    }

    renderRooms(rooms, container) {
        const roomsList = rooms.map(room => `
            <div class="room-card">
                <div class="room-info">
                    <h3>${room.room_name || room.room_number}</h3>
                    <p>Capacity: ${room.capacity}</p>
                </div>
            </div>
        `).join('');
        
        container.innerHTML = roomsList;
    }

    // Courses Module
    async initializeCourses() {
        const coursesContainer = document.getElementById('courses');
        if (!coursesContainer) return;

        try {
            const response = await this.api('/api/courses');
            if (response.success && response.courses) {
                this.renderCourses(response.courses, coursesContainer);
            }
        } catch (error) {
            console.error('Failed to load courses:', error);
        }
    }

    renderCourses(courses, container) {
        const coursesList = courses.map(course => `
            <div class="course-card">
                <div class="course-info">
                    <h3>${course.course_code || course.courseCode}</h3>
                    <p>${course.course_name || course.courseName}</p>
                </div>
            </div>
        `).join('');
        
        container.innerHTML = coursesList;
    }

    // Generic data loading method
    async loadData(apiPath, targetId, dataKey) {
        try {
            const response = await this.api(`/api/${apiPath}`);

            if (!response.success) {
                document.getElementById(targetId).innerHTML =
                    `<p class="text-red-500">❌ Error: ${response.error || 'Failed to load'}</p>`;
                return;
            }

            const items = response[dataKey];
            if (!items || items.length === 0) {
                document.getElementById(targetId).innerHTML =
                    `<p class="text-gray-400 text-center">No ${dataKey} available yet.</p>`;
                return;
            }

            let html = `
                <table class="min-w-full border border-gray-300 bg-white rounded-lg shadow-md">
                    <thead class="bg-gray-100 text-gray-700">
                        <tr>
                            ${Object.keys(items[0] || {}).map(key => `<th class="px-4 py-2">${key}</th>`).join('')}
                        </tr>
                    </thead>
                    <tbody>
                        ${items.map(row => `
                            <tr class="border-t hover:bg-gray-50">
                                ${Object.values(row).map(val => `<td class="px-4 py-2">${val}</td>`).join('')}
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;

            document.getElementById(targetId).innerHTML = html;
        } catch (error) {
            console.error(error);
            document.getElementById(targetId).innerHTML =
                `<p class="text-red-500">⚠️ Failed to connect to API</p>`;
        }
    }

    // Utility methods
    showNotification(message, type = 'info') {
        console.log(`${type.toUpperCase()}: ${message}`);
        // Create notification element if needed
    }

    closeModal(modal) {
        if (modal) modal.remove();
    }

    toggleDropdown(toggle) {
        const dropdown = toggle.nextElementSibling;
        if (dropdown) {
            dropdown.classList.toggle('active');
        }
    }

    // API wrapper method
    async api(url, method = 'GET', body) {
        try {
            const res = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: body ? JSON.stringify(body) : undefined
            });
            const json = await res.json().catch(() => ({}));
            if (!res.ok) return { success: false, ...json };
            return typeof json === 'object' ? json : { success: true, data: json };
        } catch (e) {
            return { success: false, error: e.message };
        }
    }
}

// Initialize app when DOM is loaded
let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new SmartSchedulerApp();
});

// Legacy API function for backward compatibility
async function api(url, method = 'GET', body) {
    try {
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: body ? JSON.stringify(body) : undefined
        });
        const json = await res.json().catch(() => ({}));
        if (!res.ok) return { success: false, ...json };
        return typeof json === 'object' ? json : { success: true, data: json };
    } catch (e) {
        return { success: false, error: e.message };
    }
}

function toast(msg, type = 'info') {
    console[type === 'error' ? 'error' : 'log'](msg);
}

// Course management functions
function showAddCourseModal() {
    const modal = document.getElementById('addCourseModal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }
}

function hideAddCourseModal() {
    const modal = document.getElementById('addCourseModal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        const form = document.getElementById('addCourseForm');
        if (form) form.reset();
    }
}

// Load courses function
async function loadCourses() {
    const container = document.getElementById('coursesTable');
    if (!container) return;
    
    try {
        const response = await fetch('/api/courses');
        const data = await response.json();

        if (!data.success) {
            container.innerHTML = `<p class="text-red-500 text-center">Error: ${data.error}</p>`;
            return;
        }

        if (!data.courses || data.courses.length === 0) {
            container.innerHTML = `<p class="text-gray-400 text-center">No courses available yet.</p>`;
            return;
        }

        container.innerHTML = `
            <table class="min-w-full">
                <thead class="border-b border-gray-700">
                    <tr>
                        <th class="px-4 py-3 text-left">Code</th>
                        <th class="px-4 py-3 text-left">Name</th>
                        <th class="px-4 py-3 text-left">Faculty</th>
                        <th class="px-4 py-3 text-center">Max Students</th>
                    </tr>
                </thead>
                <tbody>
                    ${data.courses.map(course => `
                        <tr class="border-b border-gray-700 hover:bg-gray-700/50">
                            <td class="px-4 py-3">${course.courseCode || course.course_code}</td>
                            <td class="px-4 py-3">${course.courseName || course.course_name}</td>
                            <td class="px-4 py-3">${course.facultyUsername || course.faculty_username}</td>
                            <td class="px-4 py-3 text-center">${course.maxStudents || course.max_students}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        container.innerHTML = `<p class="text-red-500 text-center">Failed to load courses. Please try again.</p>`;
    }
}

// Initialize form handlers when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    // Add course form handler
    const addCourseForm = document.getElementById('addCourseForm');
    if (addCourseForm) {
        addCourseForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            
            try {
                const response = await fetch('/api/courses', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        course_code: formData.get('courseCode'),
                        course_name: formData.get('courseName'),
                        faculty_username: formData.get('facultyUsername'),
                        max_students: parseInt(formData.get('maxStudents'))
                    })
                });

                const data = await response.json();
                if (data.success) {
                    hideAddCourseModal();
                    loadCourses();
                    alert('Course added successfully');
                } else {
                    alert('Error: ' + (data.error || 'Failed to add course'));
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Failed to add course. Please try again.');
            }
        });
    }

    // Initialize based on current page
    if (window.location.pathname.includes('courses')) {
        loadCourses();
    }
});

// Helper functions
function getAuthToken() {
    return localStorage.getItem('authToken');
}

function showError(message) {
    console.error(message);
}

// Auth handling
async function login(username, password) {
    try {
        const resp = await fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const payload = await resp.json();
        if (!payload.success) {
            showError(payload.error || 'Login failed');
            return null;
        }
        return payload;
    } catch (e) {
        showError('Network error');
        return null;
    }
}
