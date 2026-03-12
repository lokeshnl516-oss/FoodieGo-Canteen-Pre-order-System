<?php
require_once 'config/db.php';

// Fetch featured items
$featured_query = "SELECT * FROM food_items LIMIT 6";
$featured_result = $conn->query($featured_query);
?>

<?php include 'includes/header.php'; ?>

<div style="background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)); color: white; padding: 4rem 2rem; text-align: center; border-radius: 8px; margin-bottom: 3rem;">
    <h1 style="font-size: 2.8rem; margin-bottom: 1rem;">Welcome to FoodieGo</h1>
    <p style="font-size: 1.2rem; margin-bottom: 2rem;">Smart Canteen Pre-Order System - Order Your Favorite Food in Advance!</p>
    
    <?php if (!isset($_SESSION['user_id']) && !isset($_SESSION['admin_id'])): ?>
        <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
            <a href="auth/register.php" class="btn btn-secondary">Get Started</a>
            <a href="auth/login.php" class="btn" style="background: rgba(255, 255, 255, 0.2); color: white;">User Login</a>
            <a href="auth/admin-login.php" class="btn" style="background: rgba(255, 255, 255, 0.2); color: white;">Admin Login</a>
        </div>
    <?php elseif (isset($_SESSION['user_id'])): ?>
        <a href="user/menu.php" class="btn btn-secondary">Browse Menu</a>
    <?php elseif (isset($_SESSION['admin_id'])): ?>
        <a href="admin/dashboard.php" class="btn btn-secondary">Go to Dashboard</a>
    <?php endif; ?>
</div>

<div class="container">
    <!-- Features Section -->
    <div style="margin-bottom: 3rem;">
        <h2 style="text-align: center; color: var(--primary-color); margin-bottom: 2rem; font-size: 2rem;">Why Choose FoodieGo?</h2>
        
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 2rem;">
            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 1rem;">⏰</div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">Quick Ordering</h3>
                <p style="color: var(--text-light);">Pre-order your meals in advance and save time waiting in queues.</p>
            </div>

            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 1rem;">💳</div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">Easy Payments</h3>
                <p style="color: var(--text-light);">Secure and convenient payment options for all your orders.</p>
            </div>

            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 1rem;">📍</div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">Order Tracking</h3>
                <p style="color: var(--text-light);">Track your order status in real-time from preparation to delivery.</p>
            </div>

            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 1rem;">🍽️</div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">Diverse Menu</h3>
                <p style="color: var(--text-light);">Choose from a wide variety of delicious food items daily.</p>
            </div>

            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 1rem;">👥</div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">Community</h3>
                <p style="color: var(--text-light);">Join thousands of users enjoying quality food from our canteen.</p>
            </div>

            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="font-size: 2.5rem; margin-bottom: 1rem;">🔒</div>
                <h3 style="color: var(--primary-color); margin-bottom: 0.5rem;">Secure</h3>
                <p style="color: var(--text-light);">Your personal and payment information is always protected.</p>
            </div>
        </div>
    </div>

    <!-- Featured Items Section -->
    <?php if ($featured_result && $featured_result->num_rows > 0): ?>
        <div style="margin-bottom: 3rem;">
            <h2 style="text-align: center; color: var(--primary-color); margin-bottom: 2rem; font-size: 2rem;">Featured Items</h2>
            
            <div class="menu-grid">
                <?php
                while ($food = $featured_result->fetch_assoc()) {
                    echo '<div class="menu-card">
                        <div class="menu-card-image">🍽️</div>
                        <div class="menu-card-content">
                            <h3 class="menu-card-title">' . htmlspecialchars($food['name']) . '</h3>
                            <p class="menu-card-category">' . htmlspecialchars($food['category']) . '</p>
                            <p class="menu-card-description">' . htmlspecialchars($food['description']) . '</p>
                            <div class="menu-card-price">₹' . number_format($food['price'], 2) . '</div>
                        </div>
                    </div>';
                }
                ?>
            </div>
        </div>
    <?php endif; ?>

    <!-- CTA Section -->
    <div style="background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)); color: white; padding: 3rem 2rem; border-radius: 8px; text-align: center;">
        <h2 style="margin-bottom: 1rem;">Ready to Order?</h2>
        <p style="margin-bottom: 2rem; font-size: 1.1rem;">Create an account now and start ordering your favorite meals!</p>
        
        <?php if (!isset($_SESSION['user_id']) && !isset($_SESSION['admin_id'])): ?>
            <a href="auth/register.php" class="btn btn-secondary">Sign Up Now</a>
        <?php elseif (isset($_SESSION['user_id'])): ?>
            <a href="user/menu.php" class="btn btn-secondary">Start Ordering</a>
        <?php endif; ?>
    </div>
</div>

<?php include 'includes/footer.php'; ?>
