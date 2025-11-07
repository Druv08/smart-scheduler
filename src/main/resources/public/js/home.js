// Home page specific JavaScript
document.addEventListener('DOMContentLoaded', () => {
    console.log("✅ Home page loaded successfully!");
    
    // Handle navigation and interactions on home page
    const getStartedBtn = document.querySelector('.primary');
    
    if (getStartedBtn) {
        getStartedBtn.addEventListener('click', () => {
            // Redirect to dashboard
            window.location.href = 'dashboard.html';
        });
    }
});