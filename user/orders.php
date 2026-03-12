<?php
require_once '../config/db.php';

// Check if user is logged in
if (!isset($_SESSION['user_id'])) {
    header('Location: ../auth/login.php');
    exit();
}

$user_id = $_SESSION['user_id'];

// Fetch user orders
$query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$orders_result = $stmt->get_result();
$stmt->close();
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <h1 style="text-align: center; margin-bottom: 2rem; color: var(--primary-color);">My Orders</h1>

    <?php if ($orders_result->num_rows > 0): ?>
        <div class="orders-grid">
            <?php
            while ($order = $orders_result->fetch_assoc()) {
                $order_id = $order['order_id'];

                // Fetch order items
                $items_query = "SELECT oi.*, fi.name FROM order_items oi 
                                JOIN food_items fi ON oi.item_id = fi.item_id 
                                WHERE oi.order_id = ?";
                $items_stmt = $conn->prepare($items_query);
                $items_stmt->bind_param("i", $order_id);
                $items_stmt->execute();
                $items_result = $items_stmt->get_result();
                $items_stmt->close();

                $status_class = strtolower($order['status']);

                echo '<div class="order-card">
                    <h3>Order #' . $order_id . '</h3>
                    <div class="order-detail">
                        <strong>Date:</strong>
                        <span>' . date('Y-m-d H:i', strtotime($order['order_date'])) . '</span>
                    </div>
                    <div class="order-detail">
                        <strong>Amount:</strong>
                        <span>₹' . number_format($order['total_amount'], 2) . '</span>
                    </div>
                    <div class="order-detail">
                        <strong>Status:</strong>
                        <span class="order-status ' . $status_class . '">' . htmlspecialchars($order['status']) . '</span>
                    </div>
                    <hr style="margin: 1rem 0; border: none; border-top: 1px solid var(--border-color);">
                    <strong style="display: block; margin-bottom: 0.5rem;">Items:</strong>
                    <ul style="list-style: none;">';

                while ($item = $items_result->fetch_assoc()) {
                    echo '<li style="padding: 0.25rem 0;">• ' . htmlspecialchars($item['name']) . ' x' . $item['quantity'] . ' - ₹' . number_format($item['price'] * $item['quantity'], 2) . '</li>';
                }

                echo '</ul>
                </div>';
            }
            ?>
        </div>
    <?php else: ?>
        <div class="empty-state">
            <div class="empty-state-icon">📋</div>
            <h2>No Orders Yet</h2>
            <p>Start ordering from our delicious menu!</p>
            <a href="menu.php" class="btn btn-primary">Browse Menu</a>
        </div>
    <?php endif; ?>
</div>

<?php include '../includes/footer.php'; ?>
