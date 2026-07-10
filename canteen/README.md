# FoodieGo - Vanilla JavaScript Frontend

## Overview
Complete Vanilla JavaScript frontend for FoodieGo Smart Canteen Pre-Order System REST API built with Spring Boot.

## Features
- User Authentication (Register/Login with JWT)
- Admin Authentication
- Food Menu Browsing
- Shopping Cart Management
- Order Placement & Tracking
- Admin Dashboard
- Food Item Management
- Order Management

## Project Structure
```
foodiego-frontend/
├── index.html          # Main HTML file with all pages
├── styles.css          # Complete responsive styling
├── js/
│   ├── api.js         # API service layer (Fetch API)
│   ├── auth.js        # Authentication handlers
│   ├── user.js        # User features (menu, cart, orders)
│   ├── admin.js       # Admin features
│   └── app.js         # Main app logic & routing
└── README.md          # This file
```

## Getting Started

### Prerequisites
- Modern web browser (Chrome, Firefox, Edge, Safari)
- Spring Boot backend running on http://localhost:8080
- CORS enabled on backend

### Setup

1. **Clone or download the project**
```bash
cd foodiego-frontend
```

2. **Start a local server** (required for CORS and proper functionality)
```bash
# Using Python 3
python -m http.server 8000

# Using Node.js (http-server)
npx http-server

# Using VS Code Live Server extension
# Right-click index.html → Open with Live Server
```

3. **Ensure backend is running**
- Spring Boot app should be running on http://localhost:8080
- Check CORS configuration in SecurityConfig.java

4. **Open in browser**
- http://localhost:8000 (or whatever port your server uses)

## Technology Stack
- **HTML5** - Semantic markup
- **CSS3** - Responsive design with Flexbox/Grid
- **Vanilla JavaScript** - ES6+ features
- **Fetch API** - API communication
- **LocalStorage** - Cart & JWT token persistence
- **JWT** - Authentication

## API Integration

All API calls go through `api.js` using Fetch API:

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login (returns JWT)
- `POST /api/auth/admin-login` - Admin login (returns JWT)

### Food Items Endpoints
- `GET /api/food-items` - Get all food items (public)
- `POST /api/admin/food-items` - Add food item (admin)
- `PUT /api/admin/food-items/{id}` - Update food item (admin)
- `DELETE /api/admin/food-items/{id}` - Delete food item (admin)

### Order Endpoints
- `POST /api/user/orders` - Create order
- `GET /api/user/orders` - Get user orders
- `GET /api/admin/orders` - Get all orders (admin)
- `PUT /api/admin/orders/{id}/status` - Update order status (admin)

## JWT Authentication

Tokens are stored in LocalStorage and automatically sent with every request:
```javascript
Authorization: Bearer <token>
```

Logout clears the token and cart from LocalStorage.

## Pages & Features

### Public Pages
- **Home** - Landing page
- **Login** - User login form
- **Register** - User registration form
- **Admin Login** - Admin login form

### User Pages (Requires USER role)
- **Menu** - Browse all food items and add to cart
- **Cart** - View cart items, adjust quantities, place order
- **Orders** - View order history with status

### Admin Pages (Requires ADMIN role)
- **Dashboard** - Statistics (total orders, items, pending)
- **Manage Items** - Add, edit, delete food items
- **Manage Orders** - View all orders, update status

## Key Functions

### Navigation
```javascript
showPage('menu')        // Navigate to menu page
showPage('cart')        // Navigate to cart
showPage('orders')      // View orders
showPage('admin-dashboard') // Admin dashboard
```

### Authentication
```javascript
AuthService.login()     // Check login status
AuthService.isAdmin()   // Check if user is admin
AuthService.logout()    // Logout user
```

### Cart Management
```javascript
CartService.addToCart()      // Add item to cart
CartService.removeFromCart() // Remove from cart
CartService.clearCart()      // Clear entire cart
CartService.getTotalAmount() // Get total price
```

## Styling

Responsive design with:
- Mobile-first approach
- Flexbox & CSS Grid layouts
- Gradient backgrounds
- Hover effects & animations
- Status badges for orders
- Color-coded status (pending, completed, cancelled)

## Error Handling

All errors are displayed via alert system:
```javascript
showAlert('Error message', 'error')
showAlert('Success message', 'success')
```

## LocalStorage

Used for:
- `token` - JWT authentication token
- `userType` - USER or ADMIN role
- `userEmail` - Logged-in user email
- `cart` - Shopping cart items as JSON

## Testing Credentials

### User Account
- Email: user@example.com
- Password: user123

### Admin Account
- Email: admin@foodiego.com
- Password: admin123

## Browser Compatibility
- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## Performance
- Single HTML file (no build process)
- Minimal CSS (472 lines)
- Modular JavaScript (5 files)
- Fetch API for efficient API calls
- LocalStorage for fast cart access

## Security Notes
- JWT tokens stored in LocalStorage (note: vulnerable to XSS)
- For production: use HttpOnly cookies instead
- Always validate input on backend
- Implement rate limiting on backend

## Future Enhancements
- Session persistent cart
- Real-time order updates (WebSocket)
- Payment gateway integration
- Email notifications
- Order filtering & search
- Image uploads for menu items
- User profile management
- Search functionality

## Support
For issues or questions, refer to REST_API_DOCUMENTATION.md in the backend folder.

## License
MIT License - Feel free to use for your projects!
