class AuthService {
    static setToken(token, userType) {
        localStorage.setItem('token', token);
        localStorage.setItem('userType', userType);
    }

    static getToken() {
        return localStorage.getItem('token');
    }

    static getUserType() {
        return localStorage.getItem('userType');
    }

    static logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('userType');
        localStorage.removeItem('userEmail');
    }

    static isLoggedIn() {
        return !!this.getToken();
    }

    static isAdmin() {
        return this.getUserType() === 'ADMIN';
    }

    static isUser() {
        return this.getUserType() === 'USER';
    }

    static setUserEmail(email) {
        localStorage.setItem('userEmail', email);
    }

    static getUserEmail() {
        return localStorage.getItem('userEmail');
    }
}

// Register form handler
document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const phone = document.getElementById('regPhone').value;
    const password = document.getElementById('regPassword').value;

    try {
        await ApiService.register(name, email, phone, password);
        showAlert('Registration successful! Logging you in...', 'success');
        setTimeout(() => showPage('login'), 1500);
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

// Login form handler
document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await ApiService.login(email, password);
        AuthService.setToken(response.token, 'USER');
        AuthService.setUserEmail(email);
        showAlert('Login successful!', 'success');
        setTimeout(() => {
            updateNavBar();
            showPage('menu');
        }, 1000);
    } catch (error) {
        showAlert(error.message, 'error');
    }
});

// Admin login form handler
document.getElementById('adminLoginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('adminEmail').value;
    const password = document.getElementById('adminPassword').value;

    try {
        const response = await ApiService.adminLogin(email, password);
        AuthService.setToken(response.token, 'ADMIN');
        AuthService.setUserEmail(email);
        showAlert('Admin login successful!', 'success');
        setTimeout(() => {
            updateNavBar();
            loadAdminDashboard();
            showPage('admin-dashboard');
        }, 1000);
    } catch (error) {
        showAlert(error.message, 'error');
    }
});
