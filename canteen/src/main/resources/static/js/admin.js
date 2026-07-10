// Load admin dashboard
async function loadAdminDashboard() {
    try {
        const items = await ApiService.getFoodItems();
        const orders = await ApiService.getAllOrders();

        const pendingOrders = orders.filter(o => o.status === 'PENDING').length;

        document.getElementById('totalOrders').textContent = orders.length;
        document.getElementById('totalItems').textContent = items.length;
        document.getElementById('pendingOrders').textContent = pendingOrders;
    } catch (error) {
        console.error('Failed to load dashboard:', error);
    }
}

// Food item form handler
document.getElementById('itemForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('itemName').value;
    const category = document.getElementById('itemCategory').value;
    const description = document.getElementById('itemDescription').value;
    const price = parseFloat(document.getElementById('itemPrice').value);

    try {
        await ApiService.addFoodItem(name, category, description, price);
        showAlert('Food item added successfully!', 'success');
        e.target.reset();
        loadFoodItems();
    } catch (error) {
        showAlert('Failed to add item', 'error');
    }
});

// Load food items for admin
async function loadFoodItems() {
    try {
        const items = await ApiService.getFoodItems();
        const itemsBody = document.getElementById('itemsBody');
        itemsBody.innerHTML = '';

        items.forEach(item => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${item.name}</td>
                <td>${item.category}</td>
                <td>₹${item.price.toFixed(2)}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editFoodItem(${item.itemId})">Edit</button>
                    <button class="btn btn-secondary btn-sm" onclick="deleteFoodItem(${item.itemId})">Delete</button>
                </td>
            `;
            itemsBody.appendChild(row);
        });
    } catch (error) {
        showAlert('Failed to load items', 'error');
    }
}

// Delete food item
async function deleteFoodItem(itemId) {
    if (confirm('Are you sure you want to delete this item?')) {
        try {
            await ApiService.deleteFoodItem(itemId);
            showAlert('Item deleted successfully!', 'success');
            loadFoodItems();
        } catch (error) {
            showAlert('Failed to delete item', 'error');
        }
    }
}

// Load orders for admin
async function loadAdminOrders() {
    try {
        const orders = await ApiService.getAllOrders();
        const ordersBody = document.getElementById('ordersBody');
        ordersBody.innerHTML = '';

        orders.forEach(order => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>#${order.orderId}</td>
                <td>${order.userEmail || 'N/A'}</td>
                <td>₹${order.totalAmount.toFixed(2)}</td>
                <td>
                    <select onchange="updateOrderStatus(${order.orderId}, this.value)">
                        <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="COMPLETED" ${order.status === 'COMPLETED' ? 'selected' : ''}>Completed</option>
                        <option value="CANCELLED" ${order.status === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="viewOrderDetails(${order.orderId})">View</button>
                </td>
            `;
            ordersBody.appendChild(row);
        });
    } catch (error) {
        showAlert('Failed to load orders', 'error');
    }
}

// Update order status
async function updateOrderStatus(orderId, status) {
    try {
        await ApiService.updateOrderStatus(orderId, status);
        showAlert('Order status updated!', 'success');
        loadAdminOrders();
    } catch (error) {
        showAlert('Failed to update order', 'error');
    }
}

function viewOrderDetails(orderId) {
    showAlert(`Order #${orderId} details - Feature coming soon!`, 'success');
}
