<<<<<<< Updated upstream
// Wait for DOM to load
document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ app.js loaded successfully!");
    console.log("✅ Smart Scheduler Frontend Loaded");
    
    // Get users container
    const usersContainer = document.getElementById('users');
=======
// Smart Scheduler Main Application
class SmartSchedulerApp {
    constructor() {
        this.api = new SmartSchedulerAPI();
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
>>>>>>> Stashed changes

    // Function to fetch and display users
    const fetchUsers = async () => {
        try {
            const response = await fetch('/api/users');
            const data = await response.json();
            
            if (data.success) {
                const usersList = data.users.map(user => `
                    <div class="p-4 mb-2 bg-gray-50 rounded-lg">
                        <span class="font-medium">${user.username}</span>
                        <span class="ml-2 px-2 py-1 text-sm rounded-full ${getRoleBadgeColor(user.role)}">
                            ${user.role}
                        </span>
                    </div>
                `).join('');
                
                usersContainer.innerHTML = usersList;
            } else {
                usersContainer.innerHTML = '<p class="text-red-500">Error loading users</p>';
            }
        } catch (error) {
            console.error('Error:', error);
            usersContainer.innerHTML = '<p class="text-red-500">Failed to load users</p>';
        }
    };

    // Helper function for role badge colors
    const getRoleBadgeColor = (role) => {
        switch(role) {
            case 'admin':
                return 'bg-red-100 text-red-800';
            case 'faculty':
                return 'bg-blue-100 text-blue-800';
            case 'student':
                return 'bg-green-100 text-green-800';
            default:
                return 'bg-gray-100 text-gray-800';
        }
    };

    // Initial load
    fetchUsers();

    async function loadData(apiPath, targetId, dataKey) {
        try {
            const response = await fetch(`/api/${apiPath}`);
            const data = await response.json();

            if (!data.success) {
                document.getElementById(targetId).innerHTML =
                    `<p class="text-red-500">❌ Error: ${data.error || 'Failed to load'}</p>`;
                return;
            }

            const items = data[dataKey];
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

    // Example: Load Users and Rooms when those pages are opened
    if (window.location.pathname.includes("users")) {
        loadData("users", "usersTable", "users");
    } else if (window.location.pathname.includes("rooms")) {
        loadData("rooms", "roomsTable", "rooms");
    } else if (window.location.pathname.includes("courses")) {
        loadData("courses", "coursesTable", "courses");
    }
<<<<<<< Updated upstream
=======

    renderRooms(rooms, container) {
        const roomsList = rooms.map(room => `
            <div class="room-card">
                <div class="room-info">
                    <h3>${room.room_number}</h3>
                    <p>${room.building}</p>
                    <p>Capacity: ${room.capacity}</p>
                    <p>Type: ${room.room_type}</p>
                </div>
                <div class="room-actions">
                    <button onclick="app.editRoom(${room.id})" class="btn-edit">Edit</button>
                    <button onclick="app.deleteRoom(${room.id})" class="btn-delete">Delete</button>
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
            const courses = await this.api.getCourses();
            this.renderCourses(courses, coursesContainer);
        } catch (error) {
            console.error('Failed to load courses:', error);
            coursesContainer.innerHTML = '<p class="error">Failed to load courses</p>';
        }
    }

    renderCourses(courses, container) {
        const coursesList = courses.map(course => `
            <div class="course-card">
                <div class="course-info">
                    <h3>${course.course_code}</h3>
                    <p>${course.course_name}</p>
                    <p>Credits: ${course.credits}</p>
                    ${course.faculty_name ? `<p>Faculty: ${course.faculty_name}</p>` : ''}
                </div>
                <div class="course-actions">
                    <button onclick="app.editCourse(${course.id})" class="btn-edit">Edit</button>
                    <button onclick="app.deleteCourse(${course.id})" class="btn-delete">Delete</button>
                </div>
            </div>
        `).join('');
        
        container.innerHTML = coursesList;
    }

    // Timetable Module
    async initializeTimetable() {
        const timetableContainer = document.getElementById('timetable');
        if (!timetableContainer) return;

        try {
            const timetable = await this.api.getTimetable();
            this.renderTimetable(timetable, timetableContainer);
        } catch (error) {
            console.error('Failed to load timetable:', error);
            timetableContainer.innerHTML = '<p class="error">Failed to load timetable</p>';
        }
    }

    renderTimetable(timetable, container) {
        // Group by day and time
        const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
        const timeSlots = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00'];

        let timetableHTML = `
            <div class="timetable-grid">
                <div class="timetable-header">
                    <div class="time-header">Time</div>
                    ${days.map(day => `<div class="day-header">${day}</div>`).join('')}
                </div>
        `;

        timeSlots.forEach(time => {
            timetableHTML += `<div class="time-slot">${time}</div>`;
            days.forEach(day => {
                const entry = timetable.find(t => t.day_of_week === day && t.start_time === time + ':00');
                timetableHTML += `
                    <div class="timetable-cell ${entry ? 'occupied' : 'empty'}">
                        ${entry ? `
                            <div class="course-info">
                                <strong>${entry.course_code}</strong>
                                <br>${entry.room_number}
                            </div>
                        ` : ''}
                    </div>
                `;
            });
        });

        timetableHTML += '</div>';
        container.innerHTML = timetableHTML;
    }

    // Bookings Module
    async initializeBookings() {
        const bookingsContainer = document.getElementById('bookings');
        if (!bookingsContainer) return;

        try {
            const bookings = await this.api.getBookings();
            this.renderBookings(bookings, bookingsContainer);
        } catch (error) {
            console.error('Failed to load bookings:', error);
            bookingsContainer.innerHTML = '<p class="error">Failed to load bookings</p>';
        }
    }

    renderBookings(bookings, container) {
        const bookingsList = bookings.map(booking => `
            <div class="booking-card">
                <div class="booking-info">
                    <h3>${booking.room_number} - ${booking.building}</h3>
                    <p>Date: ${new Date(booking.booking_date).toLocaleDateString()}</p>
                    <p>Time: ${booking.start_time} - ${booking.end_time}</p>
                    <p>Purpose: ${booking.purpose || 'Not specified'}</p>
                    <span class="status-badge status-${booking.status}">${booking.status}</span>
                </div>
                <div class="booking-actions">
                    <button onclick="app.editBooking(${booking.id})" class="btn-edit">Edit</button>
                    <button onclick="app.cancelBooking(${booking.id})" class="btn-cancel">Cancel</button>
                </div>
            </div>
        `).join('');
        
        container.innerHTML = bookingsList;
    }

    // Dashboard Module
    async initializeDashboard() {
        const dashboardContainer = document.getElementById('dashboard-stats');
        if (!dashboardContainer) return;

        try {
            const stats = await this.api.getDashboardStats();
            this.renderDashboardStats(stats);
        } catch (error) {
            console.error('Failed to load dashboard stats:', error);
        }
    }

    renderDashboardStats(stats) {
        const statsElements = {
            'total-users': stats.users,
            'total-courses': stats.courses,
            'total-rooms': stats.rooms,
            'pending-bookings': stats.pending_bookings
        };

        Object.entries(statsElements).forEach(([id, value]) => {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = value || 0;
            }
        });
    }

    // Utility methods
    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <span>${message}</span>
            <button onclick="this.parentElement.remove()" class="notification-close">&times;</button>
        `;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    closeModal(modal) {
        modal.remove();
    }

    toggleDropdown(toggle) {
        const dropdown = toggle.nextElementSibling;
        if (dropdown) {
            dropdown.classList.toggle('active');
        }
    }

    executeCallback(callback) {
        if (typeof window[callback] === 'function') {
            window[callback]();
        }
    }

    // CRUD Operations
    async editUser(id) {
        // Implement edit user functionality
    }

    async deleteUser(id) {
        if (confirm('Are you sure you want to delete this user?')) {
            try {
                await this.api.deleteUser(id);
                this.showNotification('User deleted successfully', 'success');
                this.initializeUsers();
            } catch (error) {
                this.showNotification('Failed to delete user', 'error');
            }
        }
    }

    async editRoom(id) {

        // Implement edit room functionality
    }

    async deleteRoom(id) {
        if (confirm('Are you sure you want to delete this room?')) {
            try {
                await this.api.deleteRoom(id);
                this.showNotification('Room deleted successfully', 'success');
                this.initializeRooms();
            } catch (error) {
                this.showNotification('Failed to delete room', 'error');
            }
        }
    }

    async editCourse(id) {

        // Implement edit course functionality
    }

    async deleteCourse(id) {
        if (confirm('Are you sure you want to delete this course?')) {
            try {
                await this.api.deleteCourse(id);
                this.showNotification('Course deleted successfully', 'success');
                this.initializeCourses();
            } catch (error) {
                this.showNotification('Failed to delete course', 'error');
            }
        }
    }

    async editBooking(id) {

        // Implement edit booking functionality
    }

    async cancelBooking(id) {
        if (confirm('Are you sure you want to cancel this booking?')) {
            try {
                await this.api.cancelBooking(id);
                this.showNotification('Booking cancelled successfully', 'success');
                this.initializeBookings();
            } catch (error) {
                this.showNotification('Failed to cancel booking', 'error');
            }
        }
    }
}

// Initialize app when DOM is loaded
let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new SmartSchedulerApp();
>>>>>>> Stashed changes
});

async function api(url, method = 'GET', body) {
  try {
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: body ? JSON.stringify(body) : undefined
    });
    const json = await res.json().catch(() => ({}));
    if (!res.ok) return { success:false, ...json };
    return typeof json === 'object' ? json : { success:true, data:json };
  } catch (e) {
    return { success:false, error: e.message };
  }
}

function toast(msg, type='info') {
  console[type === 'error' ? 'error' : 'log'](msg);
}

// Course management functions
function showAddCourseModal() {
    const modal = document.getElementById('addCourseModal');
    modal.classList.remove('hidden');
    modal.classList.add('flex');
}

function hideAddCourseModal() {
    const modal = document.getElementById('addCourseModal');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    document.getElementById('addCourseForm').reset();
}

// Add course form handler
document.getElementById('addCourseForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    
    try {
        const response = await fetch('/api/courses', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
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

document.addEventListener('DOMContentLoaded', () => {
    // Initialize based on current page
    if (window.location.pathname.includes('courses')) {
        loadCourses();
    }
});

async function loadCourses() {
    const container = document.getElementById('coursesTable');
    try {
        const response = await fetch('/api/courses');
        const data = await response.json();

        if (!data.success) {
            container.innerHTML = `<p class="text-red-500 text-center">Error: ${data.error}</p>`;
            return;
        }

        if (!data.courses || data.courses.length === 0) {
            container.innerHTML = `
                <p class="text-gray-400 text-center">No courses available yet.</p>
            `;
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
                            <td class="px-4 py-3">${course.courseCode}</td>
                            <td class="px-4 py-3">${course.courseName}</td>
                            <td class="px-4 py-3">${course.facultyUsername}</td>
                            <td class="px-4 py-3 text-center">${course.maxStudents}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        container.innerHTML = `
            <p class="text-red-500 text-center">Failed to load courses. Please try again.</p>
        `;
    }
}

// Auth handling
async function login(username, password) {
    try {
        const resp = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const payload = await resp.json();
        if (!payload.success) {
            showError(payload.error || 'Login failed');
            return null;
        }
        return payload.data;
    } catch (e) {
        showError('Network error');
        return null;
    }
}

// Users
async function loadUsers() {
    try {
        const resp = await fetch('/api/users', {
            headers: { 'Authorization': getAuthToken() }
        });
        const payload = await resp.json();
        if (!payload.success) {
            showError(payload.error || 'Failed to load users');
            return;
        }
        renderUsers(payload.data || []);
    } catch (e) {
        showError('Network error');
    }
}

async function createUser(userData) {
    try {
        const resp = await fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': getAuthToken()
            },
            body: JSON.stringify(userData)
        });
        const payload = await resp.json();
        if (!payload.success) {
            showFieldError('#username', payload.error || 'Could not create user');
            return null;
        }
        return payload.data;
    } catch (e) {
        showError('Network error');
        return null;
    }
}

// Rooms
async function loadRooms() {
    try {
        const resp = await fetch('/api/rooms', {
            headers: { 'Authorization': getAuthToken() }
        });
        const payload = await resp.json();
        if (!payload.success) {
            showError(payload.error || 'Failed to load rooms');
            return;
        }
        renderRooms(payload.data || []);
    } catch (e) {
        showError('Network error');
    }
}

async function createRoom(roomData) {
    try {
        const resp = await fetch('/api/rooms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': getAuthToken()
            },
            body: JSON.stringify(roomData)
        });
        const payload = await resp.json();
        if (!payload.success) {
            showFieldError('#roomName', payload.error || 'Could not create room');
            return null;
        }
        return payload.data;
    } catch (e) {
        showError('Network error');
        return null;
    }
}

// Courses
async function loadCourses() {
    try {
        const resp = await fetch('/api/courses', {
            headers: { 'Authorization': getAuthToken() }
        });
        const payload = await resp.json();
        if (!payload.success) {
            showError(payload.error || 'Failed to load courses');
            return;
        }
        renderCourses(payload.data || []);
    } catch (e) {
        showError('Network error');
    }
}

async function createCourse(courseData) {
    try {
        const resp = await fetch('/api/courses', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': getAuthToken()
            },
            body: JSON.stringify(courseData)
        });
        const payload = await resp.json();
        if (!payload.success) {
            showFieldError('#courseCode', payload.error || 'Could not create course');
            return null;
        }
        return payload.data;
    } catch (e) {
        showError('Network error');
        return null;
    }
}

// Bookings
async function loadBookings() {
    try {
        const resp = await fetch('/api/bookings', {
            headers: { 'Authorization': getAuthToken() }
        });
        const payload = await resp.json();
        if (!payload.success) {
            showError(payload.error || 'Failed to load bookings');
            return;
        }
        renderBookings(payload.data || []);
    } catch (e) {
        showError('Network error');
    }
}

async function createBooking(bookingData) {
    try {
        const resp = await fetch('/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': getAuthToken()
            },
            body: JSON.stringify(bookingData)
        });
        const payload = await resp.json();
        if (!payload.success) {
            showFieldError('#courseSelect', payload.error || 'Could not create booking');
            return null;
        }
        return payload.data;
    } catch (e) {
        showError('Network error');
        return null;
    }
}

// Helper functions
function getAuthToken() {
    return localStorage.getItem('authToken');
}

function showError(message) {
    const errorDiv = document.querySelector('#errorMessage');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    } else {
        console.error(message);
    }
}

function showFieldError(selector, message) {
    const field = document.querySelector(selector);
    if (field) {
        field.setCustomValidity(message);
        field.reportValidity();
    } else {
        showError(message);
    }
}

<div id="errorMessage" class="error-message" style="display:none;"></div>