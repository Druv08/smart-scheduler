// Smart Scheduler - Enhanced Analytics Dashboard
class DashboardManager {
    constructor() {
        this.sessionInfo = null;
        this.analyticsData = null;
        this.charts = {};
        this.refreshInterval = null;
        this.init();
    }

    async init() {
        try {
            // Skip session validation if simple-app.js already did it
            if (window.authCheckComplete) {
                console.log('[DashboardManager] Auth already validated by simple-app.js');
                this.sessionInfo = { authenticated: true };
            } else {
                // Validate session (fallback if simple-app.js didn't run)
                this.sessionInfo = await this.validateSession();
                if (!this.sessionInfo) {
                    return; // Redirect will happen in validateSession
                }
            }

            // Load analytics data
            await this.loadAnalyticsData();
            
            // Setup event listeners
            this.setupEventListeners();
            
            // Initialize dashboard
            this.initializeDashboard();
            
            // Setup auto-refresh
            this.setupAutoRefresh();

            console.log('DashboardManager initialized successfully');
        } catch (error) {
            console.error('Failed to initialize DashboardManager:', error);
        }
    }

    // Session Management
    async validateSession() {
        try {
            const response = await fetch('/api/session', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                // Session invalid - redirect to login
                window.location.replace('/login.html');
                return null;
            }

            const data = await response.json();
            if (!data.authenticated) {
                // Not authenticated - redirect to login
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
    async loadAnalyticsData() {
        try {
            const response = await fetch('/api/reports/summary', {
                method: 'GET',
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('Failed to load analytics data');
            }

            this.analyticsData = await response.json();
        } catch (error) {
            console.error('Failed to load analytics data:', error);
            this.showMessage('Failed to load dashboard data', 'error');
        }
    }

    // Event Listeners
    setupEventListeners() {
        // Export buttons
        const exportCSV = document.getElementById('exportCSV');
        const exportPDF = document.getElementById('exportPDF');
        const refreshAnalytics = document.getElementById('refreshAnalytics');

        if (exportCSV) {
            exportCSV.addEventListener('click', () => this.exportData('csv'));
        }

        if (exportPDF) {
            exportPDF.addEventListener('click', () => this.exportData('pdf'));
        }

        if (refreshAnalytics) {
            refreshAnalytics.addEventListener('click', () => this.refreshDashboard());
        }
    }

    // Dashboard Initialization
    async initializeDashboard() {
        if (!this.analyticsData) {
            console.warn('No analytics data available');
            return;
        }

        // Update statistics cards
        this.updateStatisticsCards();
        
        // Initialize charts
        this.initializeCharts();
        
        // Update analytics summary
        this.updateAnalyticsSummary();
    }

    // Update Statistics Cards
    updateStatisticsCards() {
        const data = this.analyticsData;

        // Update counts
        const roomCount = document.getElementById('roomCount');
        const courseCount = document.getElementById('courseCount');
        const bookingCount = document.getElementById('bookingCount');
        const userCount = document.getElementById('userCount');

        if (roomCount) roomCount.textContent = data.totalRooms || '0';
        if (courseCount) courseCount.textContent = data.totalCourses || '0';
        if (bookingCount) bookingCount.textContent = data.totalScheduledSlots || '0';
        if (userCount) userCount.textContent = data.totalInstructors || '0';
    }

    // Initialize Charts
    initializeCharts() {
        this.createRoomUtilizationChart();
        this.createDailyClassesChart();
        this.createInstructorWorkloadChart();
        this.createTimeSlotChart();
    }

    // Room Utilization Chart
    createRoomUtilizationChart() {
        const ctx = document.getElementById('roomUtilizationChart');
        if (!ctx) return;

        const roomData = this.analyticsData.roomUtilization || [];
        
        this.charts.roomUtilization = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: roomData.map(room => room.roomName),
                datasets: [{
                    label: 'Utilization %',
                    data: roomData.map(room => room.utilizationPercent),
                    backgroundColor: roomData.map((room, index) => {
                        const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];
                        return colors[index % colors.length];
                    }),
                    borderColor: '#ffffff',
                    borderWidth: 2,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.parsed.y.toFixed(1)}% utilized`;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: function(value) {
                                return value + '%';
                            }
                        }
                    }
                }
            }
        });
    }

    // Daily Classes Chart
    createDailyClassesChart() {
        const ctx = document.getElementById('dailyClassesChart');
        if (!ctx) return;

        const dailyData = this.analyticsData.dailyClassCounts || {};
        const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
        
        this.charts.dailyClasses = new Chart(ctx, {
            type: 'line',
            data: {
                labels: days,
                datasets: [{
                    label: 'Classes',
                    data: days.map(day => dailyData[day] || 0),
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#3b82f6',
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2,
                    pointRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    // Instructor Workload Chart
    createInstructorWorkloadChart() {
        const ctx = document.getElementById('instructorWorkloadChart');
        if (!ctx) return;

        const instructorData = this.analyticsData.instructorWorkload || [];
        const topInstructors = instructorData.slice(0, 6); // Show top 6 instructors
        
        this.charts.instructorWorkload = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: topInstructors.map(instructor => instructor.instructorName),
                datasets: [{
                    label: 'Classes',
                    data: topInstructors.map(instructor => instructor.totalClasses),
                    backgroundColor: [
                        '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'
                    ],
                    borderColor: '#ffffff',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.parsed} classes`;
                            }
                        }
                    }
                }
            }
        });
    }

    // Time Slot Popularity Chart
    createTimeSlotChart() {
        const ctx = document.getElementById('timeSlotChart');
        if (!ctx) return;

        const timeSlotData = this.analyticsData.timeSlotPopularity || {};
        const timeSlots = Object.keys(timeSlotData).sort();
        
        this.charts.timeSlot = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: timeSlots.map(time => this.formatTime(time)),
                datasets: [{
                    label: 'Classes',
                    data: timeSlots.map(time => timeSlotData[time]),
                    backgroundColor: '#10b981',
                    borderColor: '#ffffff',
                    borderWidth: 2,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    }

    // Update Analytics Summary
    updateAnalyticsSummary() {
        const overview = this.analyticsData.weeklyScheduleOverview || {};

        const busiestDay = document.getElementById('busiestDay');
        const peakTime = document.getElementById('peakTime');
        const scheduleDensity = document.getElementById('scheduleDensity');
        const mostUsedRoom = document.getElementById('mostUsedRoom');

        if (busiestDay) {
            busiestDay.textContent = overview.busiestDay || '-';
        }

        if (peakTime) {
            peakTime.textContent = overview.peakTimeSlot ? 
                this.formatTime(overview.peakTimeSlot) : '-';
        }

        if (scheduleDensity) {
            scheduleDensity.textContent = overview.overallScheduleDensity ? 
                `${overview.overallScheduleDensity}%` : '-';
        }

        if (mostUsedRoom) {
            const topRoom = this.analyticsData.roomUtilization && 
                this.analyticsData.roomUtilization[0];
            mostUsedRoom.textContent = topRoom ? topRoom.roomName : '-';
        }
    }

    // Export Data
    async exportData(format) {
        try {
            const url = `/api/export/${format}`;
            
            // Create a temporary link to trigger download
            const link = document.createElement('a');
            link.href = url;
            link.download = `timetable-export.${format}`;
            
            // For CSV, we can directly download
            if (format === 'csv') {
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                this.showMessage('CSV export started', 'success');
            } else {
                // For PDF, show message that it's not implemented yet
                this.showMessage('PDF export not yet implemented', 'error');
            }
            
        } catch (error) {
            console.error('Export failed:', error);
            this.showMessage(`Failed to export ${format.toUpperCase()}`, 'error');
        }
    }

    // Refresh Dashboard
    async refreshDashboard() {
        const refreshBtn = document.getElementById('refreshAnalytics');
        if (refreshBtn) {
            refreshBtn.disabled = true;
            refreshBtn.innerHTML = `
                <svg viewBox="0 0 24 24" class="spinning">
                    <path d="M17.65,6.35C16.2,4.9 14.21,4 12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20C15.73,20 18.84,17.45 19.73,14H17.65C16.83,16.33 14.61,18 12,18A6,6 0 0,1 6,12A6,6 0 0,1 12,6C13.66,6 15.14,6.69 16.22,7.78L13,11H20V4L17.65,6.35Z"/>
                </svg>
            `;
        }

        try {
            await this.loadAnalyticsData();
            
            // Destroy existing charts
            Object.values(this.charts).forEach(chart => {
                if (chart) chart.destroy();
            });
            this.charts = {};
            
            // Reinitialize dashboard
            await this.initializeDashboard();
            
            this.showMessage('Dashboard refreshed successfully', 'success');
        } catch (error) {
            console.error('Refresh failed:', error);
            this.showMessage('Failed to refresh dashboard', 'error');
        } finally {
            if (refreshBtn) {
                refreshBtn.disabled = false;
                refreshBtn.innerHTML = `
                    <svg viewBox="0 0 24 24">
                        <path d="M17.65,6.35C16.2,4.9 14.21,4 12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20C15.73,20 18.84,17.45 19.73,14H17.65C16.83,16.33 14.61,18 12,18A6,6 0 0,1 6,12A6,6 0 0,1 12,6C13.66,6 15.14,6.69 16.22,7.78L13,11H20V4L17.65,6.35Z"/>
                    </svg>
                `;
            }
        }
    }

    // Setup Auto Refresh
    setupAutoRefresh() {
        // Refresh dashboard every 5 minutes
        this.refreshInterval = setInterval(() => {
            this.refreshDashboard();
        }, 300000); // 5 minutes
    }

    // Utility Methods
    formatTime(time) {
        if (!time) return '-';
        const [hour, minute] = time.split(':');
        const hourInt = parseInt(hour);
        const ampm = hourInt >= 12 ? 'PM' : 'AM';
        const displayHour = hourInt > 12 ? hourInt - 12 : hourInt === 0 ? 12 : hourInt;
        return `${displayHour}:${minute} ${ampm}`;
    }

    // Message Display
    showMessage(message, type = 'info') {
        const messageDiv = document.createElement('div');
        messageDiv.className = `dashboard-message message-${type}`;
        messageDiv.innerHTML = `
            <div class="message-content">
                <span class="message-text">${message}</span>
                <button class="message-close" onclick="this.parentNode.parentNode.remove()">&times;</button>
            </div>
        `;
        
        document.body.appendChild(messageDiv);
        
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    }

    // Cleanup
    destroy() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }
        
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.dashboardManager = new DashboardManager();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (window.dashboardManager) {
        window.dashboardManager.destroy();
    }
});