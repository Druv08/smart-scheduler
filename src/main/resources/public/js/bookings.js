// Smart Scheduler - Booking/Schedule Management
class BookingManager {
    constructor() {
        this.sessionInfo = null;
        this.formData = {
            courseId: null,
            roomId: null,
            day: null,
            startTime: null,
            endTime: null,
            instructorId: null
        };
        this.conflictCheckTimer = null;
        this.init();
    }

    async init() {
        try {
            // Validate session
            this.sessionInfo = await this.validateSession();
            if (!this.sessionInfo) {
                return; // Redirect will happen in validateSession
            }

            // Load form data
            await this.loadFormData();
            
            // Setup form validation and event listeners
            this.setupFormHandlers();

            console.log('BookingManager initialized successfully');
        } catch (error) {
            console.error('Failed to initialize BookingManager:', error);
        }
    }

    // Session validation (no aggressive polling)
    async validateSession() {
        try {
            const response = await fetch('/api/session', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                console.warn('Session invalid, redirecting to login');
                window.location.href = '/login.html';
                return null;
            }

            return await response.json();
        } catch (error) {
            console.error('Session validation error:', error);
            window.location.href = '/login.html';
            return null;
        }
    }

    // Load dropdown data
    async loadFormData() {
        try {
            // Load courses
            await this.loadCourses();
            
            // Load rooms
            await this.loadRooms();
            
            // Load instructors
            await this.loadInstructors();
            
        } catch (error) {
            console.error('Failed to load form data:', error);
        }
    }

    async loadCourses() {
        try {
            const response = await fetch('/api/courses', {
                credentials: 'include'
            });
            
            if (response.ok) {
                const courses = await response.json();
                this.populateSelect('courseId', courses, 'id', 'name');
            }
        } catch (error) {
            console.error('Failed to load courses:', error);
        }
    }

    async loadRooms() {
        try {
            const response = await fetch('/api/rooms', {
                credentials: 'include'
            });
            
            if (response.ok) {
                const rooms = await response.json();
                this.populateSelect('roomId', rooms, 'id', 'name');
            }
        } catch (error) {
            console.error('Failed to load rooms:', error);
        }
    }

    async loadInstructors() {
        try {
            const response = await fetch('/api/instructors', {
                credentials: 'include'
            });
            
            if (response.ok) {
                const instructors = await response.json();
                this.populateSelect('instructorId', instructors, 'id', 'name');
            }
        } catch (error) {
            console.error('Failed to load instructors:', error);
        }
    }

    populateSelect(selectId, items, valueField, textField) {
        const select = document.getElementById(selectId);
        if (!select) return;

        // Clear existing options except the first one
        const options = select.querySelectorAll('option');
        for (let i = 1; i < options.length; i++) {
            options[i].remove();
        }

        // Add new options
        items.forEach(item => {
            const option = document.createElement('option');
            option.value = item[valueField];
            option.textContent = item[textField];
            select.appendChild(option);
        });
    }

    // Form event handlers
    setupFormHandlers() {
        const form = document.getElementById('bookingForm');
        if (form) {
            form.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }

        // Real-time conflict checking (with debouncing)
        const formElements = ['courseId', 'roomId', 'day', 'startTime', 'endTime', 'instructorId'];
        formElements.forEach(elementId => {
            const element = document.getElementById(elementId);
            if (element) {
                element.addEventListener('change', () => this.scheduleConflictCheck());
            }
        });

        // Cancel button
        const cancelBtn = document.getElementById('cancelBooking');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => {
                window.location.href = '/timetable.html';
            });
        }
    }

    // Debounced conflict checking (prevent spam)
    scheduleConflictCheck() {
        clearTimeout(this.conflictCheckTimer);
        this.conflictCheckTimer = setTimeout(() => {
            this.checkForConflicts();
        }, 800); // Wait 800ms before checking
    }

    // Check for scheduling conflicts
    async checkForConflicts() {
        try {
            const formData = this.getFormData();
            
            // Validate required fields for conflict check
            if (!formData.roomId || !formData.day || !formData.startTime || !formData.endTime) {
                this.clearConflictWarnings();
                return;
            }

            const conflictData = {
                roomId: parseInt(formData.roomId),
                instructorId: formData.instructorId ? parseInt(formData.instructorId) : null,
                day: formData.day,
                startTime: formData.startTime,
                endTime: formData.endTime
            };

            const response = await fetch('/api/schedule/check-conflict', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(conflictData)
            });

            if (response.ok) {
                const result = await response.json();
                this.displayConflictResults(result);
            } else {
                console.warn('Conflict check failed');
                this.clearConflictWarnings();
            }

        } catch (error) {
            console.error('Conflict check error:', error);
            this.clearConflictWarnings();
        }
    }

    displayConflictResults(result) {
        const warningDiv = document.getElementById('conflictWarnings');
        if (!warningDiv) return;

        if (result.hasConflicts) {
            let warningHtml = '<div class="alert alert-danger"><strong>Conflicts Detected:</strong><ul>';
            
            if (result.roomConflicts && result.roomConflicts.length > 0) {
                warningHtml += '<li>Room is already booked during this time</li>';
            }
            
            if (result.instructorConflicts && result.instructorConflicts.length > 0) {
                warningHtml += '<li>Instructor has another class during this time</li>';
            }
            
            warningHtml += '</ul></div>';
            warningDiv.innerHTML = warningHtml;
        } else {
            warningDiv.innerHTML = '<div class="alert alert-success">No conflicts detected - time slot is available!</div>';
        }
    }

    clearConflictWarnings() {
        const warningDiv = document.getElementById('conflictWarnings');
        if (warningDiv) {
            warningDiv.innerHTML = '';
        }
    }

    // Get form data
    getFormData() {
        return {
            courseId: document.getElementById('courseId')?.value,
            roomId: document.getElementById('roomId')?.value,
            day: document.getElementById('day')?.value,
            startTime: document.getElementById('startTime')?.value,
            endTime: document.getElementById('endTime')?.value,
            instructorId: document.getElementById('instructorId')?.value
        };
    }

    // Form submission
    async handleFormSubmit(event) {
        event.preventDefault();
        
        try {
            const formData = this.getFormData();
            
            // Validate form data
            if (!this.validateFormData(formData)) {
                return;
            }

            const scheduleData = {
                courseId: parseInt(formData.courseId),
                roomId: parseInt(formData.roomId),
                day: formData.day,
                startTime: formData.startTime,
                endTime: formData.endTime,
                instructorId: formData.instructorId ? parseInt(formData.instructorId) : null
            };

            const response = await fetch('/api/schedule', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify(scheduleData)
            });

            if (response.ok) {
                const result = await response.json();
                this.showSuccessMessage('Schedule created successfully!');
                
                // Redirect after short delay
                setTimeout(() => {
                    window.location.href = '/timetable.html';
                }, 1500);
            } else {
                const error = await response.text();
                this.showErrorMessage(`Failed to create schedule: ${error}`);
            }

        } catch (error) {
            console.error('Form submission error:', error);
            this.showErrorMessage('An error occurred while creating the schedule.');
        }
    }

    validateFormData(formData) {
        const requiredFields = ['courseId', 'roomId', 'day', 'startTime', 'endTime'];
        
        for (const field of requiredFields) {
            if (!formData[field]) {
                this.showErrorMessage(`Please fill in the ${field.replace('Id', '').toUpperCase()} field.`);
                return false;
            }
        }

        // Validate time range
        if (formData.startTime >= formData.endTime) {
            this.showErrorMessage('End time must be after start time.');
            return false;
        }

        return true;
    }

    showSuccessMessage(message) {
        this.showMessage(message, 'success');
    }

    showErrorMessage(message) {
        this.showMessage(message, 'danger');
    }

    showMessage(message, type) {
        const messageDiv = document.getElementById('formMessages');
        if (messageDiv) {
            messageDiv.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
            
            // Auto-hide success messages after 3 seconds
            if (type === 'success') {
                setTimeout(() => {
                    messageDiv.innerHTML = '';
                }, 3000);
            }
        }
    }
}

// Initialize when DOM loads
document.addEventListener('DOMContentLoaded', () => {
    window.bookingManager = new BookingManager();
});