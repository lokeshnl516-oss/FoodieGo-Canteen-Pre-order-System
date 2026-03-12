<?php
require_once '../config/db.php';

$error = '';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = trim($_POST['email'] ?? '');
    $password = $_POST['password'] ?? '';

    if (empty($email) || empty($password)) {
        $error = 'Please fill in all fields!';
    } else {
        // Query admin
        $query = "SELECT admin_id, password FROM admin WHERE email = ?";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows == 1) {
            $admin = $result->fetch_assoc();
            if (password_verify($password, $admin['password'])) {
                $_SESSION['admin_id'] = $admin['admin_id'];
                $_SESSION['admin_email'] = $email;
                header('Location: ../admin/dashboard.php');
                exit();
            } else {
                $error = 'Invalid password!';
            }
        } else {
            $error = 'Admin email not found!';
        }
        $stmt->close();
    }
}
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <div class="auth-container">
        <h2>Admin Login</h2>

        <?php if ($error): ?>
            <div class="alert alert-danger"><?php echo htmlspecialchars($error); ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <div class="form-group">
                <label for="email">Admin Email</label>
                <input type="email" id="email" name="email" value="<?php echo htmlspecialchars($_POST['email'] ?? ''); ?>" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%;">Login</button>
        </form>

        <div class="auth-links">
            <p><a href="login.php">User Login</a></p>
            <p><a href="../index.php">Back to Home</a></p>
        </div>
    </div>
</div>

<?php include '../includes/footer.php'; ?>
