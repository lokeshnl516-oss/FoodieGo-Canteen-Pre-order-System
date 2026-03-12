<?php
require_once '../config/db.php';

// Check if user is logged in
if (!isset($_SESSION['user_id'])) {
    header('Location: ../auth/login.php');
    exit();
}

// Fetch food items
$query = "SELECT * FROM food_items ORDER BY category, name";
$result = $conn->query($query);

// Debug: Check if query executed properly
if (!$result) {
    die("Query Error: " . $conn->error);
}

// Handle add to cart
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $item_id = intval($_POST['item_id'] ?? 0);
    $quantity = intval($_POST['quantity'] ?? 1);

    if ($item_id > 0 && $quantity > 0) {
        // Initialize cart if not exists
        if (!isset($_SESSION['cart'])) {
            $_SESSION['cart'] = [];
        }

        // Add item to cart
        if (isset($_SESSION['cart'][$item_id])) {
            $_SESSION['cart'][$item_id] += $quantity;
        } else {
            $_SESSION['cart'][$item_id] = $quantity;
        }

        header('Location: menu.php?added=1');
        exit();
    }
}
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h1 style="color: var(--primary-color); margin: 0;">Our Menu</h1>
        <p style="color: var(--text-light);">
            <?php 
            if ($result && $result->num_rows > 0) {
                echo '<strong>' . $result->num_rows . '</strong> items available';
                $result->data_seek(0); // Reset result pointer
            }
            ?>
        </p>
    </div>

    <?php if (isset($_GET['added'])): ?>
        <div class="alert alert-success">Item added to cart! <a href="cart.php">View Cart</a></div>
    <?php endif; ?>

    <?php
    if ($result && $result->num_rows > 0) {
        // Group items by category
        $categories = [];
        while ($food = $result->fetch_assoc()) {
            $cat = $food['category'] ?: 'Miscellaneous';
            if (!isset($categories[$cat])) {
                $categories[$cat] = [];
            }
            $categories[$cat][] = $food;
        }
        
        // Display each category
        foreach ($categories as $category => $items) {
            echo '<h2 style="color: var(--primary-color); margin-top: 2rem; margin-bottom: 1.5rem; border-bottom: 2px solid var(--primary-color); padding-bottom: 0.5rem;">' . htmlspecialchars($category) . '</h2>';
            echo '<div class="menu-grid">';
            
            foreach ($items as $food) {
                echo '<div class="menu-card">
                    <div class="menu-card-image">🍽️</div>
                    <div class="menu-card-content">
                        <h3 class="menu-card-title">' . htmlspecialchars($food['name']) . '</h3>
                        <p class="menu-card-description">' . htmlspecialchars($food['description']) . '</p>
                        <div class="menu-card-price">₹' . number_format($food['price'], 2) . '</div>
                        <form method="POST" action="">
                            <div class="menu-card-actions">
                                <input type="hidden" name="item_id" value="' . $food['item_id'] . '">
                                <input type="number" name="quantity" value="1" min="1" max="10" required>
                                <button type="submit" class="btn btn-primary btn-sm">Add to Cart</button>
                            </div>
                        </form>
                    </div>
                </div>';
            }
            echo '</div>';
        }
    } else {
        echo '<div style="text-align: center; padding: 3rem 1rem;">
            <p style="color: var(--text-light); font-size: 1.1rem;">No items available</p>
        </div>';
    }
    ?>
</div>

<?php include '../includes/footer.php'; ?>
