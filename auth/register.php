<?php
require_once '../config/db.php';

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = trim($_POST['name'] ?? '');
    $email = trim($_POST['email'] ?? '');
    $phone = trim($_POST['phone'] ?? '');
    $password = $_POST['password'] ?? '';
    $confirm_password = $_POST['confirm_password'] ?? '';

    // Validation
    if (empty($name) || empty($email) || empty($phone) || empty($password) || empty($confirm_password)) {
        $error = 'All fields are required!';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = 'Invalid email format!';
    } elseif (strlen($password) < 6) {
        $error = 'Password must be at least 6 characters!';
    } elseif ($password !== $confirm_password) {
        $error = 'Passwords do not match!';
    } else {
        // Check if email already exists
        $check_query = "SELECT email FROM users WHERE email = ?";
        $stmt = $conn->prepare($check_query);
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $error = 'Email already registered!';
        } else {
            // Hash password
            $hashed_password = password_hash($password, PASSWORD_DEFAULT);

            // Insert user
            $insert_query = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
            $stmt = $conn->prepare($insert_query);
            $stmt->bind_param("ssss", $name, $email, $phone, $hashed_password);

            if ($stmt->execute()) {
                $success = 'Registration successful! Redirecting to login...';
                header('refresh:2;url=login.php');
            } else {
                $error = 'Error registering user. Please try again!';
            }
        }
        $stmt->close();
    }
}
?>

<?php include '../includes/header.php'; ?>

<div class="container">
    <div class="auth-container">
        <h2>Create Account</h2>

        <?php if ($error): ?>
            <div class="alert alert-danger"><?php echo htmlspecialchars($error); ?></div>
        <?php endif; ?>

        <?php if ($success): ?>
            <div class="alert alert-success"><?php echo htmlspecialchars($success); ?></div>
        <?php endif; ?>

        <form method="POST" action="">
            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" value="<?php echo htmlspecialchars($_POST['name'] ?? ''); ?>" required>
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" value="<?php echo htmlspecialchars($_POST['email'] ?? ''); ?>" required>
            </div>

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone" value="<?php echo htmlspecialchars($_POST['phone'] ?? ''); ?>" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>

            <div class="form-group">
                <label for="confirm_password">Confirm Password</label>
                <input type="password" id="confirm_password" name="confirm_password" required>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%;">Register</button>
        </form>

        <div class="auth-links">
            <p>Already have an account? <a href="login.php">Login here</a></p>
            <p><a href="../index.php">Back to Home</a></p>
        </div>
    </div>
</div>

<?php include '../includes/footer.php'; ?>
