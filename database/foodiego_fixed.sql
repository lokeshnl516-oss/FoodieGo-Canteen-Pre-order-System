-- Drop existing tables if they exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS food_items;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS admin;

-- Create Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Food Items Table
CREATE TABLE food_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    image_url VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Admin Table
CREATE TABLE admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Orders Table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Order Items Table
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES food_items(item_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert Sample Food Items
INSERT INTO food_items (name, description, price, category, is_available) VALUES
('Biryani', 'Delicious fragrant rice with spices and meat', 150.00, 'Main Course', TRUE),
('Butter Chicken', 'Creamy tomato-based curry with tender chicken pieces', 200.00, 'Main Course', TRUE),
('Paneer Tikka', 'Grilled cottage cheese marinated in yogurt and spices', 180.00, 'Appetizer', TRUE),
('Dal Makhani', 'Creamy lentil curry cooked with butter and cream', 120.00, 'Main Course', TRUE),
('Naan', 'Traditional Indian flatbread baked in tandoor', 40.00, 'Bread', TRUE),
('Samosa', 'Crispy pastry pockets filled with spiced potato', 60.00, 'Appetizer', TRUE),
('Gulab Jamun', 'Sweet dessert balls soaked in sugar syrup', 80.00, 'Dessert', TRUE),
('Lassi', 'Refreshing yogurt-based traditional Indian drink', 50.00, 'Beverage', TRUE);

-- Insert Sample Admin (password: admin123)
INSERT INTO admin (email, password, name) VALUES
('admin@foodiego.com', '$2y$10$Y9LD31Wv0eS3aX4vZ7qK9OPST5vL2mK3xK9Q1R5pU8vW2xY3zZ4Qm', 'Canteen Admin');

-- Create indexes for better performance
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_admin_email ON admin(email);
CREATE INDEX idx_user_orders ON orders(user_id);
CREATE INDEX idx_order_items ON order_items(order_id);
