<?php
require_once '../config/db.php';

// Check if admin is logged in
if (!isset($_SESSION['admin_id'])) {
    header('Location: ../auth/admin-login.php');
    exit();
}

$success = '';
$error = '';

// Handle add item
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['action'])) {
    if ($_POST['action'] == 'add') {
        $name = trim($_POST['name'] ?? '');
        $description = trim($_POST['description'] ?? '');
        $price = floatval($_POST['price'] ?? 0);
        $category = trim($_POST['category'] ?? '');

        if (empty($name) || empty($price) || empty($category)) {
            $error = 'Please fill in all required fields!';
        } else {
            $insert_query = "INSERT INTO food_items (name, description, price, category) VALUES (?, ?, ?, ?)";
            $stmt = $conn->prepare($insert_query);
            $stmt->bind_param("ssds", $name, $description, $price, $category);

            if ($stmt->execute()) {
                $success = 'Food item added successfully!';
            } else {
                $error = 'Error adding food item!';
            }
            $stmt->close();
        }
    } elseif ($_POST['action'] == 'update') {
        $item_id = intval($_POST['item_id']);
        $name = trim($_POST['name'] ?? '');
        $description = trim($_POST['description'] ?? '');
        $price = floatval($_POST['price'] ?? 0);
        $category = trim($_POST['category'] ?? '');

        if (empty($name) || empty($price) || empty($category)) {
            $error = 'Please fill in all required fields!';
        } else {
            $update_query = "UPDATE food_items SET name=?, description=?, price=?, category=? WHERE item_id=?";
            $stmt = $conn->prepare($update_query);
            $stmt->bind_param("ssdsi", $name, $description, $price, $category, $item_id);

            if ($stmt->execute()) {
                $success = 'Food item updated successfully!';
            } else {
                $error = 'Error updating food item!';
            }
            $stmt->close();
        }
    } elseif ($_POST['action'] == 'delete') {
        $item_id = intval($_POST['item_id']);
        $delete_query = "DELETE FROM food_items WHERE item_id=?";
        $stmt = $conn->prepare($delete_query);
        $stmt->bind_param("i", $item_id);

        if ($stmt->execute()) {
            $success = 'Food item deleted successfully!';
        } else {
            $error = 'Error deleting food item!';
        }
        $stmt->close();
    }
}

// Fetch all food items
$query = "SELECT * FROM food_items ORDER BY category";
$result = $conn->query($query);
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <h1 style="text-align: center; margin-bottom: 2rem; color: var(--primary-color);">Manage Food Items</h1>

    <?php if ($success): ?>
        <div class="alert alert-success"><?php echo htmlspecialchars($success); ?></div>
    <?php endif; ?>

    <?php if ($error): ?>
        <div class="alert alert-danger"><?php echo htmlspecialchars($error); ?></div>
    <?php endif; ?>

    <!-- Add Item Form -->
    <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); margin-bottom: 2rem;">
        <h2 style="margin-bottom: 1.5rem;">Add New Item</h2>
        <form method="POST" action="">
            <input type="hidden" name="action" value="add">

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
                <div class="form-group">
                    <label>Item Name</label>
                    <input type="text" name="name" required>
                </div>

                <div class="form-group">
                    <label>Category</label>
                    <input type="text" name="category" placeholder="e.g., Main Course" required>
                </div>

                <div class="form-group">
                    <label>Price (₹)</label>
                    <input type="number" name="price" step="0.01" required>
                </div>

                <div class="form-group" style="grid-column: 1 / -1;">
                    <label>Description</label>
                    <textarea name="description" rows="3"></textarea>
                </div>
            </div>

            <button type="submit" class="btn btn-primary">Add Item</button>
        </form>
    </div>

    <!-- Food Items Table -->
    <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
        <h2 style="margin-bottom: 1.5rem;">All Food Items</h2>

        <?php if ($result->num_rows > 0): ?>
            <div style="overflow-x: auto;">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Item Name</th>
                            <th>Category</th>
                            <th>Price (₹)</th>
                            <th>Description</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php
                        while ($item = $result->fetch_assoc()) {
                            echo '<tr>
                                <td>' . htmlspecialchars($item['name']) . '</td>
                                <td>' . htmlspecialchars($item['category']) . '</td>
                                <td>₹' . number_format($item['price'], 2) . '</td>
                                <td>' . htmlspecialchars(substr($item['description'], 0, 50)) . '...</td>
                                <td>
                                    <div class="admin-actions">
                                        <button class="btn btn-sm btn-secondary" onclick="editItem(' . $item['item_id'] . ', \'' . htmlspecialchars(addslashes($item['name'])) . '\', \'' . htmlspecialchars(addslashes($item['category'])) . '\', ' . $item['price'] . ', \'' . htmlspecialchars(addslashes($item['description'])) . '\')">Edit</button>
                                        <form method="POST" style="display: inline;" onsubmit="return confirm(\'Are you sure?\');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="item_id" value="' . $item['item_id'] . '">
                                            <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>';
                        }
                        ?>
                    </tbody>
                </table>
            </div>
        <?php else: ?>
            <p style="text-align: center; color: var(--text-light);">No food items found.</p>
        <?php endif; ?>
    </div>
</div>

<script>
function editItem(id, name, category, price, description) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.innerHTML = `
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="item_id" value="${id}">
        
        <div class="container" style="max-width: 600px; margin: 2rem auto;">
            <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
                <h2 style="margin-bottom: 1.5rem;">Edit Food Item</h2>
                
                <div class="form-group">
                    <label>Item Name</label>
                    <input type="text" name="name" value="${name}" required>
                </div>
                
                <div class="form-group">
                    <label>Category</label>
                    <input type="text" name="category" value="${category}" required>
                </div>
                
                <div class="form-group">
                    <label>Price (₹)</label>
                    <input type="number" name="price" step="0.01" value="${price}" required>
                </div>
                
                <div class="form-group">
                    <label>Description</label>
                    <textarea name="description" rows="3">${description}</textarea>
                </div>
                
                <button type="submit" class="btn btn-primary">Update Item</button>
                <a href="manage-items.php" class="btn btn-secondary" style="margin-left: 0.5rem;">Cancel</a>
            </div>
        </div>
    `;
    
    document.body.innerHTML = form.innerHTML;
}
</script>

<?php include '../includes/footer.php'; ?>
