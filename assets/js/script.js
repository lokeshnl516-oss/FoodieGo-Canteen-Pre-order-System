// Utility Functions

/**
 * Format currency to Indian Rupees
 */
function formatCurrency(amount) {
    return '₹' + parseFloat(amount).toFixed(2);
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type}`;
    notification.innerHTML = message;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '1000';
    notification.style.maxWidth = '400px';

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}

/**
 * Confirm action before proceeding
 */
function confirmAction(message = 'Are you sure?') {
    return confirm(message);
}

/**
 * Calculate order total
 */
function calculateTotal(items) {
    let total = 0;
    for (let item of items) {
        total += item.price * item.quantity;
    }
    return total;
}

/**
 * Validate email format
 */
function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

/**
 * Validate phone number
 */
function isValidPhone(phone) {
    const regex = /^[0-9]{10}$/;
    return regex.test(phone);
}

/**
 * Format date
 */
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('en-IN', options);
}

/**
 * Debounce function for search
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Initialize tooltips
 */
document.addEventListener('DOMContentLoaded', function() {
    // Add any initialization code here
    console.log('[v0] FoodieGo application loaded');
});
