const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
    static async request(endpoint, method = 'GET', body = null) {
    const BASE_URL = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1"
    ? "http://localhost:10000"                              
    : "https://foodiego-canteen-system.onrender.com";       
    
    const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
            }
        };

        const token = localStorage.getItem('token');
        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }

        if (body) {
            options.body = JSON.stringify(body);
        }

        try {
            const response = await fetch(url, options);
            
            // Handle non-OK responses
            if (!response.ok) {
                try {
                    const data = await response.json();
                    throw new Error(data.message || `HTTP Error: ${response.status}`);
                } catch (parseError) {
                    // If JSON parse fails, use generic error message
                    throw new Error(`HTTP Error: ${response.status} ${response.statusText}`);
                }
            }

            const data = await response.json();
            return data.data || data;
        } catch (error) {
            console.error('API Error:', error.message);
            throw error;
        }
    }

    // Auth endpoints
    static register(name, email, phone, password) {
        return this.request('/auth/register', 'POST', {
            name, email, phone, password
        });
    }

    static login(email, password) {
        return this.request('/auth/login', 'POST', {
            email, password
        });
    }

    static adminLogin(email, password) {
        return this.request('/auth/admin-login', 'POST', {
            email, password
        });
    }

    // Food items endpoints
    static getFoodItems() {
        return this.request('/food-items');
    }

    static addFoodItem(name, category, description, price) {
        return this.request('/admin/food-items', 'POST', {
            name, category, description, price, isAvailable: true
        });
    }

    static updateFoodItem(id, name, category, description, price) {
        return this.request(`/admin/food-items/${id}`, 'PUT', {
            name, category, description, price
        });
    }

    static deleteFoodItem(id) {
        return this.request(`/admin/food-items/${id}`, 'DELETE');
    }

    // Order endpoints
    static createOrder(items) {
        return this.request('/user/orders', 'POST', { items });
    }

    static getUserOrders() {
        return this.request('/user/orders');
    }

    static getAllOrders() {
        return this.request('/admin/orders');
    }

    static updateOrderStatus(orderId, status) {
        return this.request(`/admin/orders/${orderId}/status`, 'PUT', { status });
    }

    static getOrderDetails(orderId) {
        return this.request(`/orders/${orderId}`);
    }
}
