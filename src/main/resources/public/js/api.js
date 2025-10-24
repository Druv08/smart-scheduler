// Smart Scheduler API Client
class SmartSchedulerAPI {
    constructor() {
        this.baseURL = '';
        this.token = localStorage.getItem('authToken');
    }

    // Generic request method
    async request(endpoint, method = 'GET', data = null) {
        const config = {
            method,
            headers: {
                'Content-Type': 'application/json',
            }
        };

        if (this.token) {
            config.headers['Authorization'] = `Bearer ${this.token}`;
        }

        if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
            config.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(`${this.baseURL}${endpoint}`, config);
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.error || `HTTP ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error(`API request failed: ${endpoint}`, error);
            throw error;
        }
    }

    // Authentication methods
    async login(username, password) {
        const response = await this.request('/api/auth/login', 'POST', { username, password });
        if (response.success) {
            this.token = response.token;
            localStorage.setItem('authToken', response.token);
            localStorage.setItem('user', JSON.stringify(response.user));
        }
        return response;
    }

    async register(username, email, password, role = 'student') {
        const response = await this.request('/api/auth/register', 'POST', { username, email, password, role });
        if (response.success) {
            this.token = response.token;
            localStorage.setItem('authToken', response.token);
            localStorage.setItem('user', JSON.stringify(response.user));
        }
        return response;
    }

    logout() {
        this.token = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
    }

    // User management
    async getUsers() {
        const response = await this.request('/api/users');
        return response.success ? response.users : [];
    }

    async createUser(userData) {
        return await this.request('/api/users', 'POST', userData);
    }

    async updateUser(id, userData) {
        return await this.request(`/api/users/${id}`, 'PUT', userData);
    }

    async deleteUser(id) {
        return await this.request(`/api/users/${id}`, 'DELETE');
    }

    // Room management
    async getRooms() {
        const response = await this.request('/api/rooms');
        return response.success ? response.rooms : [];
    }

    async createRoom(roomData) {
        return await this.request('/api/rooms', 'POST', roomData);
    }

    async updateRoom(id, roomData) {
        return await this.request(`/api/rooms/${id}`, 'PUT', roomData);
    }

    async deleteRoom(id) {
        return await this.request(`/api/rooms/${id}`, 'DELETE');
    }

    // Course management
    async getCourses() {
        const response = await this.request('/api/courses');
        return response.success ? response.courses : [];
    }

    async createCourse(courseData) {
        return await this.request('/api/courses', 'POST', courseData);
    }

    async updateCourse(id, courseData) {
        return await this.request(`/api/courses/${id}`, 'PUT', courseData);
    }

    async deleteCourse(id) {
        return await this.request(`/api/courses/${id}`, 'DELETE');
    }

    // Timetable management
    async getTimetable() {
        const response = await this.request('/api/timetable');
        return response.success ? response.timetable : [];
    }

    async createTimetableEntry(entryData) {
        return await this.request('/api/timetable', 'POST', entryData);
    }

    async updateTimetableEntry(id, entryData) {
        return await this.request(`/api/timetable/${id}`, 'PUT', entryData);
    }

    async deleteTimetableEntry(id) {
        return await this.request(`/api/timetable/${id}`, 'DELETE');
    }

    // Booking management
    async getBookings() {
        const response = await this.request('/api/bookings');
        return response.success ? response.bookings : [];
    }

    async createBooking(bookingData) {
        return await this.request('/api/bookings', 'POST', bookingData);
    }

    async updateBooking(id, bookingData) {
        return await this.request(`/api/bookings/${id}`, 'PUT', bookingData);
    }

    async cancelBooking(id) {
        return await this.request(`/api/bookings/${id}`, 'PUT', { status: 'cancelled' });
    }

    async deleteBooking(id) {
        return await this.request(`/api/bookings/${id}`, 'DELETE');
    }

    // Dashboard stats
    async getDashboardStats() {
        const response = await this.request('/api/dashboard/stats');
        return response.success ? response.stats : {};
    }

    // Utility methods
    isAuthenticated() {
        return !!this.token;
    }

    getCurrentUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }

    hasRole(roles) {
        const user = this.getCurrentUser();
        if (!user || !user.role) return false;
        return Array.isArray(roles) ? roles.includes(user.role) : user.role === roles;
    }
}

// Legacy API object for backwards compatibility
const api = {
    async login(username, password) {
        const apiClient = new SmartSchedulerAPI();
        const result = await apiClient.login(username, password);
        if (!result.success) throw new Error(result.error);
        return result.user;
    },

    async getUsers() {
        const apiClient = new SmartSchedulerAPI();
        return await apiClient.getUsers();
    },

    async getRooms() {
        const apiClient = new SmartSchedulerAPI();
        return await apiClient.getRooms();
    },

    async getCourses() {
        const apiClient = new SmartSchedulerAPI();
        return await apiClient.getCourses();
    },

    async getTimetable() {
        const apiClient = new SmartSchedulerAPI();
        return await apiClient.getTimetable();
    },

    async getBookings() {
        const apiClient = new SmartSchedulerAPI();
        return await apiClient.getBookings();
    }
};