const api = {
    async login(username, password) {
        const resp = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const payload = await resp.json();
        if (!payload.success) throw new Error(payload.error);
        localStorage.setItem('authToken', payload.token);
        return payload.data;
    },

    async getUsers() {
        const resp = await fetch('/api/users', {
            headers: { 'Authorization': localStorage.getItem('authToken') }
        });
        const payload = await resp.json();
        if (!payload.success) throw new Error(payload.error);
        return payload.data;
    }
    
    // Add other API methods following the same pattern...
};