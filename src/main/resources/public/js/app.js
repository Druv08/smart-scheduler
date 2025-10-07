// Wait for DOM to load
document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ app.js loaded successfully!");
    console.log("✅ Smart Scheduler Frontend Loaded");
    
    // Get users container
    const usersContainer = document.getElementById('users');

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
    document.getElementById('addCourseModal').classList.remove('hidden');
}

function hideAddCourseModal() {
    document.getElementById('addCourseModal').classList.add('hidden');
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
            toast('Course added successfully', 'success');
        } else {
            toast(data.error, 'error');
        }
    } catch (error) {
        toast('Failed to add course', 'error');
    }
});

// Load courses function
async function loadCourses() {
    const container = document.getElementById('coursesTable');
    if (!container) return;

    try {
        const response = await fetch('/api/courses');
        const data = await response.json();

        if (!data.success) {
            container.innerHTML = `
                <p class="text-red-500 text-center">
                    Failed to load courses: ${data.error}
                </p>`;
            return;
        }

        const courses = data.courses;
        if (courses.length === 0) {
            container.innerHTML = `
                <p class="text-gray-400 text-center">
                    No courses available. Add your first course!
                </p>`;
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
                        <th class="px-4 py-3 text-right">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${courses.map(course => `
                        <tr class="border-b border-gray-700 hover:bg-gray-700/50">
                            <td class="px-4 py-3">${course.courseCode}</td>
                            <td class="px-4 py-3">${course.courseName}</td>
                            <td class="px-4 py-3">${course.facultyUsername}</td>
                            <td class="px-4 py-3 text-center">${course.maxStudents}</td>
                            <td class="px-4 py-3 text-right">
                                <button onclick="deleteCourse(${course.id})"
                                        class="text-red-400 hover:text-red-300">
                                    Delete
                                </button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    } catch (error) {
        container.innerHTML = `
            <p class="text-red-500 text-center">
                Failed to connect to server
            </p>`;
    }
}

// Initialize if on courses page
if (window.location.pathname.includes('courses')) {
    loadCourses();
}