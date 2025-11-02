// Minimal Login Handler for debugging
console.log("=== MINIMAL LOGIN.JS LOADING ===");

document.addEventListener('DOMContentLoaded', function() {
    console.log("=== MINIMAL LOGIN.JS DOM LOADED ===");
    
    // Just log that the page is ready, no other functionality
    console.log("Login page ready, no redirects or localStorage checks");
    
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        console.log("Login form found");
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            console.log("Login form submitted, but prevented default action for debugging");
        });
    }
    
    console.log("=== MINIMAL LOGIN.JS SETUP COMPLETE ===");
});

console.log("=== MINIMAL LOGIN.JS LOADED ===");
