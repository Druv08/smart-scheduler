// Smart Scheduler - Visual Timetable Grid
class TimetableManager {
    constructor() {
        this.currentWeekOffset = 0;
        this.sessionInfo = null;
        this.timetableData = [];
        // Use 24-hour format to match database
        this.timeSlots = [
            '08:00–08:50', '08:50–09:40', '09:45–10:35', '10:40–11:30',
            '11:35–12:25', '12:30–13:20', '13:25–14:15', '14:20–15:10',
            '15:10–16:00', '16:00–16:50', '16:50–17:30', '17:30–18:10'
        ];
        this.days = ['1', '2', '3', '4', '5']; // Day orders
        this.init();
    }

    async init() {
        try {
            // Load timetable data from API
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

    // Data Loading
    async loadTimetableData() {
        try {
            console.log('Fetching timetable data from /api/timetable...');
            const response = await fetch('/api/timetable', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                this.timetableData = data || [];
                console.log(`✅ Loaded ${this.timetableData.length} timetable entries`);
                console.log('Sample entry:', this.timetableData[0]);
            } else {
                console.warn('Failed to load timetable data, status:', response.status);
                this.timetableData = [];
            }
        } catch (error) {
            console.error('Failed to load timetable data:', error);
            this.timetableData = [];
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

        // Add schedule button - already handled in timetable.html
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
            timeCell.className = 'time-slot';
            timeCell.textContent = timeSlot;
            row.appendChild(timeCell);

            // Day columns (DAY ORDER-1 through DAY ORDER-5)
            this.days.forEach(dayOrder => {
                const dayCell = document.createElement('td');
                dayCell.setAttribute('data-day', dayOrder);
                dayCell.setAttribute('data-time', timeSlot);

                // Find entries for this day and time
                const entries = this.getEntriesForSlot(dayOrder, timeSlot);
                
                if (entries.length > 0) {
                    // Display entries
                    entries.forEach(entry => {
                        const entryElement = this.createEntryElement(entry);
                        dayCell.appendChild(entryElement);
                    });
                } else {
                    // Empty slot
                    dayCell.style.color = 'var(--gray-400)';
                    dayCell.style.textAlign = 'center';
                    dayCell.textContent = '-';
                }

                row.appendChild(dayCell);
            });

            tbody.appendChild(row);
        });

        // Update week display
        this.updateWeekDisplay();
    }

    // Get entries for specific day and time slot
    getEntriesForSlot(dayOrder, timeSlot) {
        if (!this.timetableData || this.timetableData.length === 0) {
            return [];
        }

        // Parse the time slot (e.g., "08:00–08:50") - handle both – and -
        const [startTime, endTime] = timeSlot.split(/[–-]/).map(t => t.trim());

        // Find matching entries
        const matches = this.timetableData.filter(entry => {
            const matchesDay = entry.dayOfWeek === dayOrder;
            const matchesTime = entry.startTime === startTime && entry.endTime === endTime;
            return matchesDay && matchesTime;
        });
        
        return matches;
    }

    // Create entry element for display in grid
    createEntryElement(entry) {
        const entryDiv = document.createElement('div');
        entryDiv.style.marginBottom = '0.25rem';

        // Entry content matching the HTML structure
        const courseLine = document.createElement('strong');
        courseLine.textContent = entry.courseName || 'Untitled Course';
        entryDiv.appendChild(courseLine);
        
        entryDiv.appendChild(document.createElement('br'));
        
        const detailsLine = document.createTextNode(
            `${entry.faculty || 'TBD'} - ${entry.roomName || 'TBD'}`
        );
        entryDiv.appendChild(detailsLine);
        
        if (entry.slotCode) {
            entryDiv.appendChild(document.createElement('br'));
            const slotSmall = document.createElement('small');
            slotSmall.style.color = 'var(--gray-500)';
            slotSmall.textContent = entry.slotCode;
            entryDiv.appendChild(slotSmall);
        }

        return entryDiv;
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