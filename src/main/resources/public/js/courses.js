// Courses Management - Full CRUD Functionality
let currentEditId = null;
let allCourses = [];
let currentPage = 1;
const coursesPerPage = 10;

// Load all courses from API
async function loadCourses() {
    try {
        const response = await fetch('/api/courses');
        const courses = await response.json();
        
        allCourses = Array.isArray(courses) ? courses : [];
        renderCourses();
        updatePagination();
    } catch (error) {
        console.error('Error loading courses:', error);
        showNotification('Failed to load courses', 'error');
        document.getElementById('courseTableBody').innerHTML = 
            '<tr><td colspan="6" style="text-align: center; padding: 2rem; color: var(--text-muted);">Error loading courses</td></tr>';
    }
}

// Render courses in table
function renderCourses() {
    const tbody = document.getElementById('courseTableBody');
    const filtered = getFilteredCourses();
    const start = (currentPage - 1) * coursesPerPage;
    const end = start + coursesPerPage;
    const paginated = filtered.slice(start, end);
    
    if (paginated.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 2rem; color: var(--text-muted);">No courses found</td></tr>';
        return;
    }
    
    tbody.innerHTML = paginated.map(course => `
        <tr>
            <td><span class="course-code-badge">${course.courseCode}</span></td>
            <td><span class="course-name-text">${course.courseName}</span></td>
            <td><span class="hours-badge">${course.maxStudents || 4}h</span></td>
            <td><span class="teacher-name">${course.facultyUsername || 'TBA'}</span></td>
            <td><span class="batch-badge">CSE-A</span></td>
            <td>
                <button 
                    id="enroll-btn-${course.id}" 
                    onclick="toggleEnroll(${course.id})" 
                    class="btn-enroll ${course.enrolled ? 'enrolled' : ''}" 
                    title="${course.enrolled ? 'Unenroll from course' : 'Enroll in course'}">
                    ${course.enrolled ? 'Enrolled ✅' : 'Enroll'}
                </button>
            </td>
            <td>
                <div class="action-buttons">
                    <button onclick="editCourse(${course.id})" class="btn-action btn-edit" title="Edit Course">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                    </button>
                    <button onclick="deleteCourse(${course.id}, '${course.courseCode}')" class="btn-action btn-delete" title="Delete Course">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Get filtered courses based on dropdowns
function getFilteredCourses() {
    const deptFilter = document.getElementById('departmentFilter')?.value || '';
    const creditsFilter = document.getElementById('creditsFilter')?.value || '';
    
    return allCourses.filter(course => {
        // For now, we don't have department/credits fields in Course model
        // Will add basic filtering when those fields are available
        return true;
    });
}

// Update pagination controls
function updatePagination() {
    const filtered = getFilteredCourses();
    const totalPages = Math.ceil(filtered.length / coursesPerPage);
    
    document.getElementById('pageInfo').textContent = 
        `Page ${currentPage} of ${totalPages} (${filtered.length} courses)`;
    
    document.getElementById('prevPage').disabled = currentPage === 1;
    document.getElementById('nextPage').disabled = currentPage >= totalPages;
}

// Open modal for adding new course
function openCourseModal() {
    currentEditId = null;
    document.getElementById('modalTitle').textContent = 'Add New Course';
    document.getElementById('submitBtn').innerHTML = `
        <svg style="width: 1rem; height: 1rem; margin-right: 0.5rem;" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
        </svg>
        Add Course
    `;
    
    // Reset form
    document.getElementById('courseForm').reset();
    document.getElementById('courseModal').style.display = 'flex';
}

// Close modal
function closeCourseModal() {
    document.getElementById('courseModal').style.display = 'none';
    document.getElementById('courseForm').reset();
    currentEditId = null;
}

// Edit course - populate modal with existing data
async function editCourse(id) {
    try {
        const response = await fetch(`/api/courses/${id}`);
        const data = await response.json();
        
        if (!data.success || !data.code) {
            showNotification('Course not found', 'error');
            return;
        }
        
        currentEditId = id;
        document.getElementById('modalTitle').textContent = 'Edit Course';
        document.getElementById('submitBtn').innerHTML = `
            <svg style="width: 1rem; height: 1rem; margin-right: 0.5rem;" viewBox="0 0 24 24" fill="currentColor">
                <path d="M21 7v12q0 .825-.588 1.413Q19.825 21 19 21H5q-.825 0-1.413-.587Q3 19.825 3 19V5q0-.825.588-1.413Q4.175 3 5 3h12l4 4zm-9 11q1.25 0 2.125-.875T15 15q0-1.25-.875-2.125T12 12q-1.25 0-2.125.875T9 15q0 1.25.875 2.125T12 18zm-6-8h9V6H6v4z"/>
            </svg>
            Save Changes
        `;
        
        // Populate form fields
        document.getElementById('courseCode').value = data.code || '';
        document.getElementById('courseName').value = data.name || '';
        document.getElementById('hoursPerWeek').value = data.maxStudents || 4;
        document.getElementById('teacher').value = data.faculty || '';
        document.getElementById('batch').value = 'CSE-A'; // Default since we don't have batch field yet
        
        document.getElementById('courseModal').style.display = 'flex';
    } catch (error) {
        console.error('Error loading course details:', error);
        showNotification('Failed to load course details', 'error');
    }
}

// Delete course with confirmation
async function deleteCourse(id, courseCode) {
    if (!confirm(`Are you sure you want to delete course "${courseCode}"?\n\nThis action cannot be undone.`)) {
        return;
    }
    
    try {
        const response = await fetch(`/api/courses/${id}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification(`Course "${courseCode}" deleted successfully`, 'success');
            await loadCourses();
        } else {
            showNotification(data.error || 'Failed to delete course', 'error');
        }
    } catch (error) {
        console.error('Error deleting course:', error);
        showNotification('Failed to delete course', 'error');
    }
}

// Toggle enrollment status
async function toggleEnroll(id) {
    const button = document.getElementById(`enroll-btn-${id}`);
    const isCurrentlyEnrolled = button.classList.contains('enrolled');
    const newEnrolledState = !isCurrentlyEnrolled;
    
    try {
        const response = await fetch(`/api/courses/${id}/enroll`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ enrolled: newEnrolledState })
        });
        
        const data = await response.json();
        
        if (data.success) {
            // Update button state
            button.classList.toggle('enrolled', newEnrolledState);
            button.textContent = newEnrolledState ? 'Enrolled ✅' : 'Enroll';
            button.title = newEnrolledState ? 'Unenroll from course' : 'Enroll in course';
            
            // Update the course in our local array
            const course = allCourses.find(c => c.id === id);
            if (course) {
                course.enrolled = newEnrolledState;
            }
            
            showNotification(data.message, 'success');
        } else {
            showNotification(data.error || 'Failed to update enrollment', 'error');
        }
    } catch (error) {
        console.error('Error toggling enrollment:', error);
        showNotification('Failed to update enrollment', 'error');
    }
}

// Handle form submission (Add or Edit)
async function handleCourseSubmit(event) {
    event.preventDefault();
    
    const formData = {
        code: document.getElementById('courseCode').value.trim(),
        name: document.getElementById('courseName').value.trim(),
        faculty: document.getElementById('teacher').value.trim(),
        maxStudents: parseInt(document.getElementById('hoursPerWeek').value) || 4
    };
    
    // Validation
    if (!formData.code || !formData.name || !formData.faculty) {
        showNotification('Please fill in all required fields', 'error');
        return;
    }
    
    try {
        const url = currentEditId ? `/api/courses/${currentEditId}` : '/api/courses';
        const method = currentEditId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const data = await response.json();
        
        if (data.success) {
            showNotification(
                currentEditId ? 'Course updated successfully' : 'Course added successfully',
                'success'
            );
            closeCourseModal();
            await loadCourses();
        } else {
            showNotification(data.error || 'Operation failed', 'error');
        }
    } catch (error) {
        console.error('Error saving course:', error);
        showNotification('Failed to save course', 'error');
    }
}

// Show notification toast
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 2rem;
        right: 2rem;
        background: ${type === 'success' ? 'var(--success)' : type === 'error' ? 'var(--danger)' : 'var(--accent-primary)'};
        color: white;
        padding: 1rem 1.5rem;
        border-radius: 0.5rem;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 10000;
        animation: slideInRight 0.3s ease-out;
        max-width: 400px;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Initialize when DOM loads
document.addEventListener('DOMContentLoaded', () => {
    // Load courses
    loadCourses();
    
    // Setup form submit handler
    document.getElementById('courseForm')?.addEventListener('submit', handleCourseSubmit);
    
    // Setup filter change handlers
    document.getElementById('departmentFilter')?.addEventListener('change', () => {
        currentPage = 1;
        renderCourses();
        updatePagination();
    });
    
    document.getElementById('creditsFilter')?.addEventListener('change', () => {
        currentPage = 1;
        renderCourses();
        updatePagination();
    });
    
    // Setup pagination
    document.getElementById('prevPage')?.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            renderCourses();
            updatePagination();
        }
    });
    
    document.getElementById('nextPage')?.addEventListener('click', () => {
        const totalPages = Math.ceil(getFilteredCourses().length / coursesPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            renderCourses();
            updatePagination();
        }
    });
    
    // Close modal when clicking outside
    document.getElementById('courseModal')?.addEventListener('click', (e) => {
        if (e.target.id === 'courseModal') {
            closeCourseModal();
        }
    });
});

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .course-code-badge {
        font-family: 'Courier New', monospace;
        font-weight: 600;
        color: var(--accent-primary);
    }
    
    .course-name-text {
        font-weight: 500;
        color: var(--text-primary);
    }
    
    .hours-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        background: var(--accent-light);
        color: var(--accent-primary);
        border-radius: 12px;
        font-size: 0.875rem;
        font-weight: 600;
    }
    
    .teacher-name {
        color: var(--text-secondary);
    }
    
    .batch-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        background: var(--bg-tertiary);
        color: var(--text-primary);
        border-radius: 12px;
        font-size: 0.875rem;
        font-weight: 500;
    }
    
    .action-buttons {
        display: flex;
        gap: 0.5rem;
        justify-content: center;
    }
    
    .btn-action {
        padding: 0.5rem;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .btn-edit {
        background: var(--accent-light);
        color: var(--accent-primary);
    }
    
    .btn-edit:hover {
        background: var(--accent-primary);
        color: white;
        transform: translateY(-2px);
    }
    
    .btn-delete {
        background: rgba(248, 81, 73, 0.1);
        color: var(--danger);
    }
    
    .btn-delete:hover {
        background: var(--danger);
        color: white;
        transform: translateY(-2px);
    }
`;
document.head.appendChild(style);
