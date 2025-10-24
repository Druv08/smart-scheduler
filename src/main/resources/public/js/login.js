// Smart Scheduler - Login Handler
(function() {
    "use strict";
    
    console.log("Login.js loaded successfully!");
    
    // Clear any existing login state when login page loads
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("userRole");
    localStorage.removeItem("username");
    
    console.log("LocalStorage cleared");
    
    document.addEventListener("DOMContentLoaded", function() {
        console.log("DOM Content Loaded - Setting up login form");
        
        var loginForm = document.getElementById("loginForm");
        var togglePassword = document.querySelector(".toggle-password");
        var passwordInput = document.getElementById("password");

        if (togglePassword && passwordInput) {
            togglePassword.addEventListener("click", function() {
                var type = passwordInput.getAttribute("type") === "password" ? "text" : "password";
                passwordInput.setAttribute("type", type);
            });
        }

        if (loginForm) {
            console.log("Login form found, attaching submit listener");
            loginForm.addEventListener("submit", function(e) {
                console.log("Form submit event triggered!");
                e.preventDefault();
                console.log("Default form submission prevented");
                
                var email = document.getElementById("email").value;
                var password = document.getElementById("password").value;
                
                console.log("Attempting login with username:", email);
                
                if (!email || !password) {
                    showMessage("Please fill in all fields", "error");
                    return;
                }
                
                var loginBtn = document.querySelector(".btn-login");
                var originalText = loginBtn.innerHTML;
                
                loginBtn.innerHTML = "<span>Logging in...</span>";
                loginBtn.disabled = true;
                
                fetch("/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify({
                        username: email,
                        password: password
                    })
                })
                .then(function(response) {
                    console.log("Response status:", response.status, "ok:", response.ok, "content-type:", response.headers.get("content-type"));
                    if (!response.ok) {
                        throw new Error("HTTP " + response.status);
                    }
                    var ct = response.headers.get("content-type") || "";
                    if (!ct.includes("application/json")) {
                        return Promise.reject(new Error("Non-JSON response"));
                    }
                    return response.json();
                })
                .then(function(data) {
                    console.log("Response data:", data);
                    if (data.success) {
                        localStorage.setItem("isLoggedIn", "true");
                        localStorage.setItem("userRole", data.role);
                        localStorage.setItem("username", data.username);
                        
                        showMessage("Login successful! Redirecting...", "success");
                        
                        setTimeout(function() {
                            window.location.href = "dashboard.html";
                        }, 1000);
                    } else {
                        showMessage(data.error || "Login failed. Please try again.", "error");
                        loginBtn.innerHTML = originalText;
                        loginBtn.disabled = false;
                    }
                })
                .catch(function(error) {
                    console.error("Login error:", error);
                    showMessage("Connection error. Please try again.", "error");
                    loginBtn.innerHTML = originalText;
                    loginBtn.disabled = false;
                });
            });
        }
        
        function showMessage(message, type) {
            var existingMessage = document.querySelector(".login-message");
            if (existingMessage) {
                existingMessage.remove();
            }
            
            var messageDiv = document.createElement("div");
            messageDiv.className = "login-message " + type;
            messageDiv.textContent = message;
            
            messageDiv.style.padding = "12px";
            messageDiv.style.marginBottom = "16px";
            messageDiv.style.borderRadius = "6px";
            messageDiv.style.fontWeight = "500";
            
            if (type === "success") {
                messageDiv.style.backgroundColor = "#d1fae5";
                messageDiv.style.color = "#065f46";
                messageDiv.style.border = "1px solid #34d399";
            } else if (type === "error") {
                messageDiv.style.backgroundColor = "#fee2e2";
                messageDiv.style.color = "#991b1b";
                messageDiv.style.border = "1px solid #f87171";
            }
            
            var loginBox = document.querySelector(".login-box");
            if (loginBox) {
                loginBox.insertBefore(messageDiv, loginBox.firstChild);
            }
            
            setTimeout(function() {
                if (messageDiv.parentNode) {
                    messageDiv.remove();
                }
            }, 5000);
        }
    });
})();
