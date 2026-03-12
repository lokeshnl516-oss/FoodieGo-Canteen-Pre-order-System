-- Create Users Table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
);

-- Create Orders Table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create Order Items Table
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES food_items(item_id)
);

-- Create Admin Table
CREATE TABLE admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Sample Food Items
INSERT INTO food_items (name, description, price, category) VALUES
('Biryani', 'Delicious fragrant rice with spices', 150.00, 'Main Course'),
('Butter Chicken', 'Creamy tomato-based curry with chicken', 200.00, 'Main Course'),
('Paneer Tikka', 'Grilled cottage cheese with spices', 180.00, 'Appetizer'),
('Dal Makhani', 'Creamy lentil curry', 120.00, 'Main Course'),
('Naan', 'Traditional Indian bread', 40.00, 'Bread'),
('Samosa', 'Crispy pastry with potato filling', 60.00, 'Appetizer'),
('Gulab Jamun', 'Sweet dessert balls in syrup', 80.00, 'Dessert'),
('Lassi', 'Yogurt-based traditional drink', 50.00, 'Beverage');

-- Insert Sample Admin (password: admin123)
INSERT INTO admin (email, password, name) VALUES
('admin@foodiego.com', '$2y$10$Y9LD31Wv0eS3aX4vZ7qK9OPST5vL2mK3xK9Q1R5pU8vW2xY3zZ4Qm', 'Canteen Admin');
