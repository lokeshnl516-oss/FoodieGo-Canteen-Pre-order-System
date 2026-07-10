// Show specific page
function showPage(pageId) {
    if (pageId !== 'home' && pageId !== 'login' && pageId !== 'register' && pageId !== 'admin-login' && !AuthService.isLoggedIn()) {
        showAlert('Please login first', 'error');
        showPage('login');
        return;
    }

    if ((pageId === 'admin-dashboard' || pageId === 'manage-items' || pageId === 'manage-orders') && !AuthService.isAdmin()) {
        showAlert('Admin access only', 'error');
        return;
    }

    // Hide all pages
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });

    // Show requested page
    const page = document.getElementById(pageId);
    if (page) {
        page.classList.add('active');

        // Load data based on page
        if (pageId === 'menu') {
            loadMenu();
        } else if (pageId === 'cart') {
            loadCart();
        } else if (pageId === 'orders') {
            loadOrders();
        } else if (pageId === 'manage-items') {
            loadFoodItems();
        } else if (pageId === 'manage-orders') {
            loadAdminOrders();
        }
    }
}

// Update navigation bar based on login status
function updateNavBar() {
    const navMenu = document.getElementById('navMenu');
    const navUser = document.getElementById('navUser');

    navMenu.innerHTML = '<li><a href="#" onclick="showPage(\'home\')">Home</a></li>';

    if (AuthService.isLoggedIn()) {
        if (AuthService.isUser()) {
            navMenu.innerHTML += `
                <li><a href="#" onclick="showPage('menu')">Menu</a></li>
                <li><a href="#" onclick="showPage('cart')">Cart</a></li>
                <li><a href="#" onclick="showPage('orders')">My Orders</a></li>
            `;
            navUser.innerHTML = `
                <span>${AuthService.getUserEmail()}</span>
                <button class="btn btn-secondary btn-sm" onclick="handleLogout()">Logout</button>
            `;
        } else if (AuthService.isAdmin()) {
            navMenu.innerHTML += `
                <li><a href="#" onclick="showPage('admin-dashboard')">Dashboard</a></li>
                <li><a href="#" onclick="showPage('manage-items')">Items</a></li>
                <li><a href="#" onclick="showPage('manage-orders')">Orders</a></li>
            `;
            navUser.innerHTML = `
                <span>${AuthService.getUserEmail()}</span>
                <button class="btn btn-secondary btn-sm" onclick="handleLogout()">Logout</button>
            `;
        }
    } else {
        navMenu.innerHTML += `
            <li><a href="#" onclick="showPage('login')">Login</a></li>
            <li><a href="#" onclick="showPage('register')">Register</a></li>
        `;
    }
}

// Handle logout
function handleLogout() {
    AuthService.logout();
    CartService.clearCart();
    showAlert('Logged out successfully', 'success');
    updateNavBar();
    showPage('home');
}

// Show alert message
function showAlert(message, type = 'info') {
    const alert = document.getElementById('alert');
    alert.textContent = message;
    alert.className = `alert show ${type}`;
    
    setTimeout(() => {
        alert.classList.remove('show');
    }, 3000);
}

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    updateNavBar();
    showPage('home');

    // Add CORS support to fetch
    if (window.location.hostname === 'localhost') {
        console.log('FoodieGo Frontend Ready!');
        console.log('API Base URL:', 'http://localhost:8080/api');
    }
});
