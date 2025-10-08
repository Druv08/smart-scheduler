document.addEventListener('DOMContentLoaded', () => {
    const usersList = document.getElementById('usersList');
    const errorMsg = document.getElementById('errorMessage');

    async function loadUsers() {
        try {
            const users = await api.getUsers();
            usersList.innerHTML = users.map(user => `
                <tr>
                    <td>${user.username}</td>
                    <td>${user.role}</td>
                </tr>
            `).join('');
        } catch (err) {
            errorMsg.textContent = err.message;
            errorMsg.style.display = 'block';
        }
    }

    loadUsers();
});