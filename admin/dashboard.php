<?php
require_once '../config/db.php';

// Check if admin is logged in
if (!isset($_SESSION['admin_id'])) {
    header('Location: ../auth/admin-login.php');
    exit();
}

// Fetch statistics
$users_query = "SELECT COUNT(*) as count FROM users";
$users_result = $conn->query($users_query);
$users_count = $users_result->fetch_assoc()['count'];

$orders_query = "SELECT COUNT(*) as count FROM orders";
$orders_result = $conn->query($orders_query);
$orders_count = $orders_result->fetch_assoc()['count'];

$items_query = "SELECT COUNT(*) as count FROM food_items";
$items_result = $conn->query($items_query);
$items_count = $items_result->fetch_assoc()['count'];

$pending_orders_query = "SELECT COUNT(*) as count FROM orders WHERE status = 'Pending'";
$pending_orders_result = $conn->query($pending_orders_query);
$pending_orders_count = $pending_orders_result->fetch_assoc()['count'];

$total_revenue_query = "SELECT SUM(total_amount) as total FROM orders WHERE status = 'Completed'";
$total_revenue_result = $conn->query($total_revenue_query);
$total_revenue = $total_revenue_result->fetch_assoc()['total'] ?? 0;
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <h1 style="text-align: center; margin-bottom: 2rem; color: var(--primary-color);">Admin Dashboard</h1>

    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem; margin-bottom: 2rem;">
        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); border-left: 4px solid var(--primary-color);">
            <h3 style="color: var(--text-light); margin-bottom: 0.5rem;">Total Users</h3>
            <div style="font-size: 2.5rem; font-weight: bold; color: var(--primary-color);"><?php echo $users_count; ?></div>
        </div>

        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); border-left: 4px solid var(--secondary-color);">
            <h3 style="color: var(--text-light); margin-bottom: 0.5rem;">Total Orders</h3>
            <div style="font-size: 2.5rem; font-weight: bold; color: var(--secondary-color);"><?php echo $orders_count; ?></div>
        </div>

        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); border-left: 4px solid var(--warning);">
            <h3 style="color: var(--text-light); margin-bottom: 0.5rem;">Food Items</h3>
            <div style="font-size: 2.5rem; font-weight: bold; color: var(--warning);"><?php echo $items_count; ?></div>
        </div>

        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); border-left: 4px solid #FF6B6B;">
            <h3 style="color: var(--text-light); margin-bottom: 0.5rem;">Pending Orders</h3>
            <div style="font-size: 2.5rem; font-weight: bold; color: #FF6B6B;"><?php echo $pending_orders_count; ?></div>
        </div>

        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); border-left: 4px solid var(--success);">
            <h3 style="color: var(--text-light); margin-bottom: 0.5rem;">Revenue (Completed)</h3>
            <div style="font-size: 2.5rem; font-weight: bold; color: var(--success);">₹<?php echo number_format($total_revenue, 2); ?></div>
        </div>
    </div>

    <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
        <h2 style="margin-bottom: 1.5rem; color: var(--primary-color);">Quick Actions</h2>
        <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
            <a href="manage-items.php" class="btn btn-primary">Manage Food Items</a>
            <a href="manage-orders.php" class="btn btn-secondary">View All Orders</a>
        </div>
    </div>
</div>

<?php include '../includes/footer.php'; ?>
