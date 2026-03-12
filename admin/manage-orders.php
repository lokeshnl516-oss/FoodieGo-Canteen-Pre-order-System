<?php
require_once '../config/db.php';

// Check if admin is logged in
if (!isset($_SESSION['admin_id'])) {
    header('Location: ../auth/admin-login.php');
    exit();
}

$success = '';
$error = '';

// Handle status update
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['action'])) {
    if ($_POST['action'] == 'update_status') {
        $order_id = intval($_POST['order_id']);
        $status = trim($_POST['status'] ?? '');

        if (empty($status)) {
            $error = 'Please select a status!';
        } else {
            $update_query = "UPDATE orders SET status=? WHERE order_id=?";
            $stmt = $conn->prepare($update_query);
            $stmt->bind_param("si", $status, $order_id);

            if ($stmt->execute()) {
                $success = 'Order status updated successfully!';
            } else {
                $error = 'Error updating order status!';
            }
            $stmt->close();
        }
    }
}

// Fetch all orders
$query = "SELECT o.*, u.name, u.email FROM orders o 
          JOIN users u ON o.user_id = u.user_id 
          ORDER BY o.order_date DESC";
$result = $conn->query($query);
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <h1 style="text-align: center; margin-bottom: 2rem; color: var(--primary-color);">Manage Orders</h1>

    <?php if ($success): ?>
        <div class="alert alert-success"><?php echo htmlspecialchars($success); ?></div>
    <?php endif; ?>

    <?php if ($error): ?>
        <div class="alert alert-danger"><?php echo htmlspecialchars($error); ?></div>
    <?php endif; ?>

    <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); overflow-x: auto;">
        <?php if ($result->num_rows > 0): ?>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer Name</th>
                        <th>Email</th>
                        <th>Amount (₹)</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th>Items</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <?php
                    while ($order = $result->fetch_assoc()) {
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

                        $items_list = '';
                        while ($item = $items_result->fetch_assoc()) {
                            $items_list .= $item['name'] . ' x' . $item['quantity'] . ', ';
                        }
                        $items_list = rtrim($items_list, ', ');

                        echo '<tr>
                            <td>#' . $order['order_id'] . '</td>
                            <td>' . htmlspecialchars($order['name']) . '</td>
                            <td>' . htmlspecialchars($order['email']) . '</td>
                            <td>₹' . number_format($order['total_amount'], 2) . '</td>
                            <td>' . date('Y-m-d H:i', strtotime($order['order_date'])) . '</td>
                            <td><span class="order-status ' . strtolower($order['status']) . '">' . htmlspecialchars($order['status']) . '</span></td>
                            <td>' . htmlspecialchars($items_list) . '</td>
                            <td>
                                <form method="POST" style="display: flex; gap: 0.5rem;">
                                    <input type="hidden" name="action" value="update_status">
                                    <input type="hidden" name="order_id" value="' . $order['order_id'] . '">
                                    <select name="status" required style="padding: 0.4rem; border-radius: 4px; border: 1px solid var(--border-color);">
                                        <option value="Pending" ' . ($order['status'] == 'Pending' ? 'selected' : '') . '>Pending</option>
                                        <option value="Completed" ' . ($order['status'] == 'Completed' ? 'selected' : '') . '>Completed</option>
                                        <option value="Cancelled" ' . ($order['status'] == 'Cancelled' ? 'selected' : '') . '>Cancelled</option>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-secondary">Update</button>
                                </form>
                            </td>
                        </tr>';
                    }
                    ?>
                </tbody>
            </table>
        <?php else: ?>
            <p style="text-align: center; color: var(--text-light);">No orders found.</p>
        <?php endif; ?>
    </div>
</div>

<?php include '../includes/footer.php'; ?>
