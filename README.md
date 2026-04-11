# Live Demo
[text](https://foodiego.page.gd/)

# 🍽️ FoodieGo - Smart Canteen Pre-Order System

A complete full-stack web application for managing canteen food orders with user registration, menu browsing, shopping cart, and admin dashboard.

## 📋 Features

### For Users
- ✅ User Registration & Authentication
- ✅ Secure Login with Password Hashing
- ✅ Browse Food Menu by Category
- ✅ Add Items to Shopping Cart
- ✅ Adjust Quantity & Remove Items
- ✅ Place Orders with Automatic Calculation
- ✅ View Order History
- ✅ Track Order Status in Real-time

### For Admin
- ✅ Secure Admin Login
- ✅ Dashboard with Key Metrics
- ✅ Add New Food Items
- ✅ Edit Existing Food Items
- ✅ Delete Food Items
- ✅ View All User Orders
- ✅ Update Order Status (Pending → Completed → Cancelled)
- ✅ Revenue Tracking

### Security Features
- ✅ Password Hashing with bcrypt
- ✅ SQL Injection Prevention (Prepared Statements)
- ✅ XSS Protection (htmlspecialchars)
- ✅ Session-Based Authentication
- ✅ Email Validation
- ✅ Duplicate Account Prevention

## 🛠️ Technology Stack

- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Backend:** PHP 7.4+
- **Database:** MySQL 5.7+
- **Server:** Apache with mod_rewrite
- **Design:** Responsive & Mobile-Friendly

## 📁 Project Structure

```
foodiego/
├── config/
│   └── db.php                    # Database configuration
├── database/
│   └── foodiego.sql              # Database schema & sample data
├── includes/
│   ├── header.php                # Navigation & session handling
│   └── footer.php                # Page footer
├── auth/
│   ├── register.php              # User registration
│   ├── login.php                 # User login
│   ├── admin-login.php           # Admin login
│   └── logout.php                # Logout handler
├── user/
│   ├── menu.php                  # Browse menu items
│   ├── cart.php                  # Shopping cart
│   ├── orders.php                # Order history
│   └── logout.php                # User logout
├── admin/
│   ├── dashboard.php             # Admin statistics
│   ├── manage-items.php          # Food item management
│   ├── manage-orders.php         # Order management
│   └── logout.php                # Admin logout
├── assets/
│   ├── css/
│   │   └── style.css             # Main stylesheet (574 lines)
│   └── js/
│       └── script.js             # Utility functions
├── index.php                     # Homepage
├── .htaccess                     # Apache configuration
├── README.md                     # This file
├── SETUP_INSTRUCTIONS.md         # Detailed setup guide
└── composer.json (optional)      # PHP dependencies
```

## 🚀 Quick Start

### Prerequisites
- PHP 7.4 or higher
- MySQL 5.7 or higher
- Apache web server with mod_rewrite enabled
- phpMyAdmin (optional, for database management)

### Installation Steps

#### 1. Create Database
```sql
CREATE DATABASE foodiego;
USE foodiego;
-- Import the SQL file
SOURCE /database/foodiego.sql;
```

#### 2. Configure Database
Edit `/config/db.php`:
```php
define('DB_HOST', 'localhost');
define('DB_USER', 'your_mysql_user');
define('DB_PASS', 'your_mysql_password');
define('DB_NAME', 'foodiego');
```

#### 3. Upload Files
- Upload all files to your web server's root directory
- Ensure proper permissions (755 for folders, 644 for files)

#### 4. Access the Application
```
http://yourserver.com/index.php
```

## 👤 Default Login Credentials

### Admin Account
```
Email: admin@foodiego.com
Password: admin123
```

⚠️ **Change the admin password immediately after first login!**

## 📊 Database Schema

### Users Table
```
user_id INT (Primary Key)
name VARCHAR(100)
email VARCHAR(100) (Unique)
phone VARCHAR(15)
password VARCHAR(255)
created_at TIMESTAMP
```

### Food Items Table
```
item_id INT (Primary Key)
name VARCHAR(100)
description TEXT
price DECIMAL(10, 2)
category VARCHAR(50)
image_url VARCHAR(255)
is_available BOOLEAN
created_at TIMESTAMP
```

### Orders Table
```
order_id INT (Primary Key)
user_id INT (Foreign Key)
total_amount DECIMAL(10, 2)
status VARCHAR(20)
order_date TIMESTAMP
```

### Order Items Table
```
order_item_id INT (Primary Key)
order_id INT (Foreign Key)
item_id INT (Foreign Key)
quantity INT
price DECIMAL(10, 2)
```

### Admin Table
```
admin_id INT (Primary Key)
email VARCHAR(100) (Unique)
password VARCHAR(255)
name VARCHAR(100)
created_at TIMESTAMP
```

## 🔧 Configuration

### Environment Variables (Optional)
For production, use environment variables:
```php
// In config/db.php
define('DB_HOST', getenv('DB_HOST') ?: 'localhost');
define('DB_USER', getenv('DB_USER') ?: 'root');
define('DB_PASS', getenv('DB_PASS') ?: '');
define('DB_NAME', getenv('DB_NAME') ?: 'foodiego');
```

### HTTPS (Recommended)
Add to `.htaccess`:
```apache
<IfModule mod_rewrite.c>
    RewriteCond %{HTTPS} off
    RewriteRule ^(.*)$ https://%{HTTP_HOST}%{REQUEST_URI} [L,R=301]
</IfModule>
```

## 🎨 Customization

### Color Theme
Edit `/assets/css/style.css`:
```css
:root {
    --primary-color: #FF6B6B;      /* Change this */
    --secondary-color: #4ECDC4;    /* Change this */
    --dark-bg: #1a1a1a;
    --light-bg: #f8f9fa;
    /* ... more colors */
}
```

### Add New Food Categories
Simply add items with new category names in the admin panel.

### Modify Food Pricing
Edit prices in the admin panel → Manage Items

## 📱 Responsive Design

The application is fully responsive and works on:
- ✅ Desktop (1200px+)
- ✅ Tablet (768px - 1199px)
- ✅ Mobile (< 768px)

All forms and tables adapt automatically to screen size.

## 🔒 Security Best Practices

### Implemented
- ✅ Password hashing with bcrypt (PASSWORD_DEFAULT)
- ✅ Prepared statements for SQL queries
- ✅ XSS protection with htmlspecialchars()
- ✅ Session-based user authentication
- ✅ Email validation before registration
- ✅ Duplicate account prevention

### Additional Recommendations
- [ ] Enable HTTPS/SSL in production
- [ ] Implement rate limiting for login attempts
- [ ] Add CSRF tokens to forms
- [ ] Use environment variables for secrets
- [ ] Implement email verification
- [ ] Add user input sanitization
- [ ] Set secure cookie flags
- [ ] Regular security audits

## 🐛 Known Issues & Solutions

### Issue: Infinite Loading/Flickering
**Solution:** Fixed by removing redundant session starts and implementing proper `headers_sent()` checks in header.php.

### Issue: Database Connection Fails
**Solution:** Verify MySQL credentials in `/config/db.php` and ensure database exists.

### Issue: Session Not Persisting
**Solution:** Clear browser cookies and cache. Verify PHP session settings.

### Issue: Styling Not Loading
**Solution:** Check file paths and clear browser cache. Ensure CSS file permissions are correct.

## 📈 Future Enhancements

- [ ] Email notifications for order status
- [ ] Payment gateway integration (Stripe, PayPal)
- [ ] User profile management
- [ ] Food item images upload
- [ ] Advanced search and filters
- [ ] Rating and review system
- [ ] Scheduled orders
- [ ] Multiple canteen branches
- [ ] Mobile app (React Native/Flutter)
- [ ] Analytics dashboard

## 📝 License

This project is provided as-is for educational and commercial use.

## 💬 Support

For issues and questions, refer to `SETUP_INSTRUCTIONS.md` for detailed troubleshooting.

## 📞 Contact

For inquiries or feature requests, contact: `support@foodiego.com`

---

**Version:** 1.0  
**Last Updated:** January 2026  
**Status:** ✅ Production Ready  
**Author:** Development Team
