<?php
require_once '../config/db.php';

// Check if user is logged in
if (!isset($_SESSION['user_id'])) {
    header('Location: ../auth/login.php');
    exit();
}

$user_id = $_SESSION['user_id'];
$success = '';
$error = '';

// Handle cart updates
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['action'])) {
        if ($_POST['action'] == 'remove') {
            $item_id = intval($_POST['item_id']);
            if (isset($_SESSION['cart'][$item_id])) {
                unset($_SESSION['cart'][$item_id]);
            }
        } elseif ($_POST['action'] == 'update') {
            $item_id = intval($_POST['item_id']);
            $quantity = intval($_POST['quantity']);
            if ($quantity > 0) {
                $_SESSION['cart'][$item_id] = $quantity;
            }
        } elseif ($_POST['action'] == 'place_order') {
            if (empty($_SESSION['cart'])) {
                $error = 'Cart is empty!';
            } else {
                // Calculate total
                $total = 0;
                $cart_items = [];

                foreach ($_SESSION['cart'] as $item_id => $quantity) {
                    $item_query = "SELECT price FROM food_items WHERE item_id = ?";
                    $stmt = $conn->prepare($item_query);
                    $stmt->bind_param("i", $item_id);
                    $stmt->execute();
                    $item_result = $stmt->get_result();
                    $item = $item_result->fetch_assoc();
                    $stmt->close();

                    if ($item) {
                        $item_total = $item['price'] * $quantity;
                        $total += $item_total;
                        $cart_items[$item_id] = ['quantity' => $quantity, 'price' => $item['price']];
                    }
                }

                // Insert order
                $order_query = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, 'Pending')";
                $stmt = $conn->prepare($order_query);
                $stmt->bind_param("id", $user_id, $total);

                if ($stmt->execute()) {
                    $order_id = $stmt->insert_id;
                    $stmt->close();

                    // Insert order items
                    $insert_item_query = "INSERT INTO order_items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
                    $stmt = $conn->prepare($insert_item_query);

                    foreach ($cart_items as $item_id => $item_data) {
                        $stmt->bind_param("iidi", $order_id, $item_id, $item_data['quantity'], $item_data['price']);
                        $stmt->execute();
                    }
                    $stmt->close();

                    // Clear cart
                    $_SESSION['cart'] = [];
                    $success = 'Order placed successfully! Order ID: ' . $order_id;
                    header('refresh:2;url=orders.php');
                } else {
                    $error = 'Failed to place order. Try again!';
                }
            }
        }
    }
}

// Initialize cart if not exists
if (!isset($_SESSION['cart'])) {
    $_SESSION['cart'] = [];
}
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <h1 style="text-align: center; margin-bottom: 2rem; color: var(--primary-color);">Shopping Cart</h1>

    <?php if ($success): ?>
        <div class="alert alert-success"><?php echo htmlspecialchars($success); ?></div>
    <?php endif; ?>

    <?php if ($error): ?>
        <div class="alert alert-danger"><?php echo htmlspecialchars($error); ?></div>
    <?php endif; ?>

    <?php if (empty($_SESSION['cart'])): ?>
        <div class="empty-state">
            <div class="empty-state-icon">🛒</div>
            <h2>Your Cart is Empty</h2>
            <p>Add some delicious items to your cart!</p>
            <a href="menu.php" class="btn btn-primary">Browse Menu</a>
        </div>
    <?php else: ?>
        <table class="cart-table">
            <thead>
                <tr>
                    <th>Item</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Total</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <?php
                $grand_total = 0;
                foreach ($_SESSION['cart'] as $item_id => $quantity) {
                    $item_query = "SELECT name, price FROM food_items WHERE item_id = ?";
                    $stmt = $conn->prepare($item_query);
                    $stmt->bind_param("i", $item_id);
                    $stmt->execute();
                    $item_result = $stmt->get_result();
                    $item = $item_result->fetch_assoc();
                    $stmt->close();

                    if ($item) {
                        $item_total = $item['price'] * $quantity;
                        $grand_total += $item_total;

                        echo '<tr>
                            <td>' . htmlspecialchars($item['name']) . '</td>
                            <td>₹' . number_format($item['price'], 2) . '</td>
                            <td>
                                <form method="POST" style="display: flex; gap: 0.5rem;">
                                    <input type="hidden" name="action" value="update">
                                    <input type="hidden" name="item_id" value="' . $item_id . '">
                                    <input type="number" name="quantity" value="' . $quantity . '" min="1" max="10" style="width: 60px;">
                                    <button type="submit" class="btn btn-sm btn-secondary">Update</button>
                                </form>
                            </td>
                            <td>₹' . number_format($item_total, 2) . '</td>
                            <td>
                                <form method="POST" style="display: inline;">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="item_id" value="' . $item_id . '">
                                    <button type="submit" class="btn btn-sm btn-danger">Remove</button>
                                </form>
                            </td>
                        </tr>';
                    }
                }
                ?>
            </tbody>
        </table>

        <div class="cart-summary">
            <div class="cart-summary-item">
                <span>Subtotal:</span>
                <span>₹<?php echo number_format($grand_total, 2); ?></span>
            </div>
            <div class="cart-summary-item">
                <span>Tax (5%):</span>
                <span>₹<?php echo number_format($grand_total * 0.05, 2); ?></span>
            </div>
            <div class="cart-summary-item total">
                <span>Total:</span>
                <span>₹<?php echo number_format($grand_total * 1.05, 2); ?></span>
            </div>

            <form method="POST" style="margin-top: 1.5rem;">
                <input type="hidden" name="action" value="place_order">
                <button type="submit" class="btn btn-success" style="width: 100%; padding: 1rem;">Place Order</button>
            </form>
            <a href="menu.php" class="btn btn-secondary" style="width: 100%; padding: 1rem; text-align: center; margin-top: 1rem;">Continue Shopping</a>
        </div>
    <?php endif; ?>
</div>

<?php include '../includes/footer.php'; ?>
