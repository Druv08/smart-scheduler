// Smart Scheduler - Visual Timetable Grid
class TimetableManager {
    constructor() {
        this.currentWeekOffset = 0;
        this.sessionInfo = null;
        this.timetableData = null;
        this.timeSlots = [
            '08:00', '09:00', '10:00', '11:00', '12:00',
            '13:00', '14:00', '15:00', '16:00', '17:00'
        ];
        this.days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
        this.init();
    }

    async init() {
        try {
            // Validate session
            this.sessionInfo = await this.validateSession();
            if (!this.sessionInfo) {
                return; // Redirect will happen in validateSession
            }

            // Load timetable data
            await this.loadTimetableData();
            
            // Setup event listeners
            this.setupEventListeners();
            
            // Render the timetable grid
            this.renderTimetableGrid();

            console.log('TimetableManager initialized successfully');
        } catch (error) {
            console.error('Failed to initialize TimetableManager:', error);
        }
    }

    // Session Management (simplified)
    async validateSession() {
        try {
            const response = await fetch('/api/session', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                console.warn('Session validation failed, redirecting to login');
                window.location.replace('/login.html');
                return null;
            }

            const data = await response.json();
            if (!data.authenticated) {
                console.warn('Not authenticated, redirecting to login');
                window.location.replace('/login.html');
                return null;
            }

            return data;
        } catch (error) {
            console.error('Session validation failed:', error);
            window.location.replace('/login.html');
            return null;
        }
    }

    // Data Loading
    async loadTimetableData() {
        try {
            const response = await fetch('/api/reports/timetable-grid', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                this.timetableData = await response.json();
            } else {
                console.warn('Failed to load timetable data');
                this.timetableData = { schedule: {}, timeSlots: this.timeSlots, days: this.days };
            }
        } catch (error) {
            console.error('Failed to load timetable data:', error);
            this.timetableData = { schedule: {}, timeSlots: this.timeSlots, days: this.days };
        }
    }

    // Event Listeners
    setupEventListeners() {
        // Week navigation
        const prevWeekBtn = document.getElementById('prevWeek');
        const nextWeekBtn = document.getElementById('nextWeek');
        
        if (prevWeekBtn) {
            prevWeekBtn.addEventListener('click', () => this.navigateWeek(-1));
        }
        
        if (nextWeekBtn) {
            nextWeekBtn.addEventListener('click', () => this.navigateWeek(1));
        }

        // Add schedule button
        const addScheduleBtn = document.getElementById('addSchedule');
        if (addScheduleBtn) {
            addScheduleBtn.addEventListener('click', () => {
                window.location.href = '/bookings.html';
            });
        }
    }

    // Timetable Grid Rendering
    renderTimetableGrid() {
        const tbody = document.querySelector('#weeklyTimetable tbody');
        if (!tbody) {
            console.error('Timetable tbody not found');
            return;
        }

        // Clear existing content
        tbody.innerHTML = '';

        // Create time slot rows
        this.timeSlots.forEach(timeSlot => {
            const row = document.createElement('tr');
            
            // Time column
            const timeCell = document.createElement('td');
            timeCell.className = 'time-cell';
            timeCell.textContent = this.formatTime(timeSlot);
            row.appendChild(timeCell);

            // Day columns
            this.days.forEach(day => {
                const dayCell = document.createElement('td');
                dayCell.className = 'schedule-cell';
                dayCell.setAttribute('data-day', day);
                dayCell.setAttribute('data-time', timeSlot);

                // Find entries for this day and time
                const entries = this.getEntriesForSlot(day, timeSlot);
                if (entries.length > 0) {
                    entries.forEach(entry => {
                        const entryElement = this.createEntryElement(entry);
                        dayCell.appendChild(entryElement);
                    });
                    dayCell.classList.add('occupied');
                } else {
                    dayCell.classList.add('available');
                    const emptySlot = document.createElement('div');
                    emptySlot.className = 'empty-slot';
                    emptySlot.textContent = '-';
                    dayCell.appendChild(emptySlot);
                }

                row.appendChild(dayCell);
            });

            tbody.appendChild(row);
        });

        // Update week display
        this.updateWeekDisplay();
    }

    // Get entries for specific day and time slot
    getEntriesForSlot(day, timeSlot) {
        if (!this.timetableData || !this.timetableData.schedule) {
            return [];
        }

        const daySchedule = this.timetableData.schedule[day];
        if (!daySchedule || !daySchedule[timeSlot]) {
            return [];
        }

        return daySchedule[timeSlot];
    }

    // Create entry element for display in grid
    createEntryElement(entry) {
        const entryDiv = document.createElement('div');
        entryDiv.className = 'schedule-entry';
        entryDiv.setAttribute('data-entry-id', entry.id);

        // Determine entry type for styling
        const entryType = this.getEntryType(entry.courseCode || 'lecture');
        entryDiv.classList.add(`entry-${entryType}`);

        // Entry content
        entryDiv.innerHTML = `
            <div class="entry-header">
                <span class="course-code">${entry.courseCode || 'N/A'}</span>
                <span class="time-range">${entry.startTime || ''}-${entry.endTime || ''}</span>
            </div>
            <div class="entry-details">
                <div class="course-name">${entry.courseName || ''}</div>
                <div class="entry-location">${entry.roomName || ''}</div>
                <div class="instructor-name">${entry.instructorName || 'TBD'}</div>
            </div>
        `;

        return entryDiv;
    }

    // Determine entry type for styling
    getEntryType(courseCode) {
        if (courseCode && courseCode.toLowerCase().includes('lab')) return 'lab';
        if (courseCode && courseCode.toLowerCase().includes('proj')) return 'project';
        return 'lecture'; // default
    }

    // Format time for display
    formatTime(time) {
        const [hour, minute] = time.split(':');
        const hourInt = parseInt(hour);
        const ampm = hourInt >= 12 ? 'PM' : 'AM';
        const displayHour = hourInt > 12 ? hourInt - 12 : hourInt === 0 ? 12 : hourInt;
        return `${displayHour}:${minute} ${ampm}`;
    }

    // Week Navigation
    navigateWeek(direction) {
        this.currentWeekOffset += direction;
        this.updateWeekDisplay();
    }

    updateWeekDisplay() {
        const currentWeekElement = document.getElementById('currentWeek');
        if (currentWeekElement) {
            const baseDate = new Date();
            const weekStart = new Date(baseDate);
            weekStart.setDate(baseDate.getDate() + (this.currentWeekOffset * 7));
            
            const weekEnd = new Date(weekStart);
            weekEnd.setDate(weekStart.getDate() + 4); // Friday

            const formatOptions = { month: 'long', day: 'numeric' };
            const startStr = weekStart.toLocaleDateString('en-US', formatOptions);
            const endStr = weekEnd.toLocaleDateString('en-US', formatOptions);
            const year = weekStart.getFullYear();

            currentWeekElement.textContent = `${startStr} - ${endStr}, ${year}`;
        }
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.timetableManager = new TimetableManager();
});