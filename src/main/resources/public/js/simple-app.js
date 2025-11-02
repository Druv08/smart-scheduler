// Simple App Controller - Basic functionality without complex routing
document.addEventListener('DOMContentLoaded', function() {
    // Basic authentication check
    const token = localStorage.getItem('authToken');
    const user = JSON.parse(localStorage.getItem('user') || 'null');
    
    // Initialize based on current page
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    
    // Initialize page-specific functionality
    initializePage(currentPage);
    
    // Set up profile menu if user is logged in
    if (user && token) {
        setupProfileMenu(user);
    }
    
    // Set up navigation highlighting
    highlightCurrentNav();
});

function initializePage(page) {
    switch(page) {
        case 'index.html':
        case '':
            break;
            
        case 'login.html':
            setupLoginForm();
            break;
            
        case 'signup.html':
            setupSignupForm();
            break;
            
        case 'dashboard.html':
            loadDashboardData();
            break;
            
        case 'users.html':
            loadUsersData();
            break;
            
        case 'rooms.html':
            loadRoomsData();
            break;
            
        case 'courses.html':
            // Courses page now has its own complete implementation
            break;
            
        case 'timetable.html':
            loadTimetableData();
            break;
            
        case 'bookings.html':
            loadBookingsData();
            break;
    }
}

function setupLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;
            const messageDiv = document.getElementById('loginMessage');
            
            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    localStorage.setItem('authToken', data.token);
                    localStorage.setItem('user', JSON.stringify(data.user));
                    showMessage(messageDiv, 'Login successful! Redirecting...', 'success');
                    
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1000);
                } else {
                    showMessage(messageDiv, data.error || 'Login failed', 'error');
                }
            } catch (error) {
                console.error('Login error:', error);
                showMessage(messageDiv, 'Network error. Please try again.', 'error');
            }
        });
    }
}

function setupSignupForm() {
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const role = document.getElementById('role').value;
            const messageDiv = document.getElementById('signupMessage');
            
            if (password !== confirmPassword) {
                showMessage(messageDiv, 'Passwords do not match', 'error');
                return;
            }
            
            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password, role })
                });
                
                const data = await response.json();
                
                if (data.success) {
                    localStorage.setItem('authToken', data.token);
                    localStorage.setItem('user', JSON.stringify(data.user));
                    showMessage(messageDiv, 'Registration successful! Redirecting...', 'success');
                    
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1000);
                } else {
                    showMessage(messageDiv, data.error || 'Registration failed', 'error');
                }
            } catch (error) {
                console.error('Signup error:', error);
                showMessage(messageDiv, 'Network error. Please try again.', 'error');
            }
        });
    }
}

async function loadDashboardData() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const response = await fetch('/api/dashboard/stats', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        
        if (data.success) {
            updateDashboardStats(data.stats);
        }
    } catch (error) {
        console.error('Failed to load dashboard data:', error);
    }
}

function updateDashboardStats(stats) {
    const elements = {
        'total-users': stats.users || 0,
        'total-courses': stats.courses || 0,
        'total-rooms': stats.rooms || 0,
        'pending-bookings': stats.pending_bookings || 0
    };
    
    Object.entries(elements).forEach(([id, value]) => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    });
}

async function loadUsersData() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const response = await fetch('/api/users', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        
        if (data.success) {
            renderUsersTable(data.users);
        }
    } catch (error) {
        console.error('Failed to load users:', error);
    }
}

function renderUsersTable(users) {
    const tbody = document.getElementById('usersTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = users.map(user => `
        <tr>
            <td>${user.username}</td>
            <td>${user.email}</td>
            <td>${user.role}</td>
            <td><span class="badge badge-success">Active</span></td>
            <td>${new Date(user.created_at).toLocaleDateString()}</td>
            <td class="actions">
                <button class="icon-btn edit" onclick="editUser(${user.id})" title="Edit user">✏️</button>
                <button class="icon-btn delete" onclick="deleteUser(${user.id})" title="Delete user">🗑️</button>
            </td>
        </tr>
    `).join('');
}

async function loadRoomsData() {
    try {
        const response = await fetch('/api/rooms');
        const data = await response.json();
        
        if (data.success) {
            renderRoomsTable(data.rooms);
        }
    } catch (error) {
        console.error('Failed to load rooms:', error);
    }
}

function renderRoomsTable(rooms) {
    const tbody = document.getElementById('roomsTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = rooms.map(room => `
        <tr>
            <td>${room.room_number}</td>
            <td>${room.building}</td>
            <td>${room.capacity}</td>
            <td>${room.room_type}</td>
            <td class="actions">
                <button class="icon-btn edit" onclick="editRoom(${room.id})" title="Edit room">✏️</button>
                <button class="icon-btn delete" onclick="deleteRoom(${room.id})" title="Delete room">🗑️</button>
            </td>
        </tr>
    `).join('');
}

async function loadCoursesData() {
    try {
        const response = await fetch('/api/courses');
        const data = await response.json();
        
        if (data.success) {
            renderCoursesTable(data.courses);
        }
    } catch (error) {
        console.error('Failed to load courses:', error);
    }
}

function renderCoursesTable(courses) {
    const tbody = document.getElementById('coursesTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = courses.map(course => `
        <tr>
            <td>${course.course_code}</td>
            <td>${course.course_name}</td>
            <td>${course.credits}</td>
            <td>${course.faculty_name || 'Not assigned'}</td>
            <td class="actions">
                <button class="icon-btn edit" onclick="editCourse(${course.id})" title="Edit course">✏️</button>
                <button class="icon-btn delete" onclick="deleteCourse(${course.id})" title="Delete course">🗑️</button>
            </td>
        </tr>
    `).join('');
}

async function loadTimetableData() {
    try {
        const response = await fetch('/api/timetable');
        const data = await response.json();
        
        if (data.success) {
            renderTimetable(data.timetable);
        }
    } catch (error) {
        console.error('Failed to load timetable:', error);
    }
}

function renderTimetable(timetable) {
    const container = document.getElementById('timetableContainer');
    if (!container) return;
    
    // Simple table view for now
    const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
    const timeSlots = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00'];
    
    let html = `
        <table class="timetable-table">
            <thead>
                <tr>
                    <th>Time</th>
                    ${days.map(day => `<th>${day}</th>`).join('')}
                </tr>
            </thead>
            <tbody>
    `;
    
    timeSlots.forEach(time => {
        html += `<tr><td class="time-slot">${time}</td>`;
        days.forEach(day => {
            const entry = timetable.find(t => t.day_of_week === day && t.start_time === time + ':00');
            html += `<td class="timetable-cell ${entry ? 'occupied' : 'empty'}">`;
            if (entry) {
                html += `<div class="course-entry">
                    <strong>${entry.course_code}</strong><br>
                    ${entry.room_number}
                </div>`;
            }
            html += '</td>';
        });
        html += '</tr>';
    });
    
    html += '</tbody></table>';
    container.innerHTML = html;
}

async function loadBookingsData() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const response = await fetch('/api/bookings', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const data = await response.json();
        
        if (data.success) {
            renderBookingsTable(data.bookings);
        }
    } catch (error) {
        console.error('Failed to load bookings:', error);
    }
}

function renderBookingsTable(bookings) {
    const tbody = document.getElementById('bookingsTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = bookings.map(booking => `
        <tr>
            <td>${booking.room_number} - ${booking.building}</td>
            <td>${new Date(booking.booking_date).toLocaleDateString()}</td>
            <td>${booking.start_time} - ${booking.end_time}</td>
            <td>${booking.purpose || 'Not specified'}</td>
            <td><span class="status-badge status-${booking.status}">${booking.status}</span></td>
            <td class="actions">
                <button class="icon-btn edit" onclick="editBooking(${booking.id})" title="Edit booking">✏️</button>
                <button class="icon-btn cancel" onclick="cancelBooking(${booking.id})" title="Cancel booking">❌</button>
            </td>
        </tr>
    `).join('');
}

function setupProfileMenu(user) {
    const profileMenu = document.getElementById('profileMenu');
    if (profileMenu) {
        profileMenu.innerHTML = `
            <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(user.username)}&background=4f46e5&color=fff" 
                 alt="Profile" class="avatar-img" onclick="toggleProfileDropdown()">
            <div class="profile-dropdown" id="profileDropdown">
                <a href="profile-settings.html">Profile Settings</a>
                <a href="#" onclick="logout()">Logout</a>
            </div>
        `;
    }
}

function toggleProfileDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    if (dropdown) {
        dropdown.classList.toggle('active');
    }
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

function highlightCurrentNav() {
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    const navLinks = document.querySelectorAll('nav a, .nav a');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPage) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

function showMessage(container, message, type) {
    if (container) {
        container.textContent = message;
        container.className = `message ${type} show`;
        container.style.display = 'block';
        
        setTimeout(() => {
            container.classList.remove('show');
            container.style.display = 'none';
        }, 5000);
    }
}

// Global functions for button actions
function editUser(id) {
    // Implement edit functionality
}

function deleteUser(id) {
    if (confirm('Are you sure you want to delete this user?')) {
        // Implement delete functionality
    }
}

function editRoom(id) {
    // Implement edit functionality
}

function deleteRoom(id) {
    if (confirm('Are you sure you want to delete this room?')) {
        console.log('Delete room:', id);
        // Implement delete functionality
    }
}

function editCourse(id) {
    // Implement edit functionality
}

function deleteCourse(id) {
    if (confirm('Are you sure you want to delete this course?')) {
        // Implement delete functionality
    }
}

function editBooking(id) {
    // Implement edit functionality
}

function cancelBooking(id) {
    if (confirm('Are you sure you want to cancel this booking?')) {
        // Implement cancel functionality
    }
}