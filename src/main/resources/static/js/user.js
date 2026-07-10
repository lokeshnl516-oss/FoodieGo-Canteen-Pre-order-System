class CartService {
    static getCart() {
        const cart = localStorage.getItem('cart');
        return cart ? JSON.parse(cart) : [];
    }

    static addToCart(item) {
        const cart = this.getCart();
        const existingItem = cart.find(i => i.itemId === item.itemId);

        if (existingItem) {
            existingItem.quantity += item.quantity;
        } else {
            cart.push(item);
        }

        localStorage.setItem('cart', JSON.stringify(cart));
        showAlert('Added to cart!', 'success');
    }

    static removeFromCart(itemId) {
        let cart = this.getCart();
        cart = cart.filter(i => i.itemId !== itemId);
        localStorage.setItem('cart', JSON.stringify(cart));
    }

    static updateQuantity(itemId, quantity) {
        const cart = this.getCart();
        const item = cart.find(i => i.itemId === itemId);
        if (item) {
            if (quantity <= 0) {
                this.removeFromCart(itemId);
            } else {
                item.quantity = quantity;
                localStorage.setItem('cart', JSON.stringify(cart));
            }
        }
    }

    static clearCart() {
        localStorage.removeItem('cart');
    }

    static getTotalAmount() {
        const cart = this.getCart();
        return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    }
}

// Load menu items
async function loadMenu() {
    try {
        const items = await ApiService.getFoodItems();
        const menuGrid = document.getElementById('menuGrid');
        menuGrid.innerHTML = '';

        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'menu-card';
            card.innerHTML = `
                <div class="menu-card-image">🍽️</div>
                <div class="menu-card-content">
                    <h3 class="menu-card-title">${item.name}</h3>
                    <p class="menu-card-category">${item.category}</p>
                    <p class="menu-card-description">${item.description}</p>
                    <div class="menu-card-price">₹${item.price.toFixed(2)}</div>
                    <div class="menu-card-actions">
                        <input type="number" id="qty-${item.itemId}" value="1" min="1" max="10">
                        <button class="btn btn-primary btn-sm" onclick="addItemToCart(${item.itemId}, ${item.price})">Add to Cart</button>
                    </div>
                </div>
            `;
            menuGrid.appendChild(card);
        });
    } catch (error) {
        showAlert('Failed to load menu', 'error');
    }
}

function addItemToCart(itemId, price) {
    const quantity = parseInt(document.getElementById(`qty-${itemId}`).value);
    CartService.addToCart({
        itemId,
        quantity,
        price
    });
}

// Load cart
function loadCart() {
    const cart = CartService.getCart();
    const cartBody = document.getElementById('cartBody');
    cartBody.innerHTML = '';

    let total = 0;
    cart.forEach(item => {
        const row = document.createElement('tr');
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        row.innerHTML = `
            <td>Item #${item.itemId}</td>
            <td>₹${item.price.toFixed(2)}</td>
            <td>
                <input type="number" value="${item.quantity}" min="1" 
                       onchange="updateCartQuantity(${item.itemId}, this.value)">
            </td>
            <td>₹${itemTotal.toFixed(2)}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="removeCartItem(${item.itemId})">Remove</button>
            </td>
        `;
        cartBody.appendChild(row);
    });

    document.getElementById('totalAmount').textContent = total.toFixed(2);
}

function updateCartQuantity(itemId, quantity) {
    CartService.updateQuantity(itemId, parseInt(quantity));
    loadCart();
}

function removeCartItem(itemId) {
    CartService.removeFromCart(itemId);
    loadCart();
}

// Place order
async function placeOrder() {
    if (AuthService.isLoggedIn()) {
        const cart = CartService.getCart();
        if (cart.length === 0) {
            showAlert('Cart is empty', 'error');
            return;
        }

        try {
            // Map cart items: remove price field, keep only itemId and quantity
            const orderItems = cart.map(item => ({
                itemId: item.itemId,
                quantity: item.quantity
            }));
            console.log('[v0] Placing order with items:', orderItems);
            await ApiService.createOrder(orderItems);
            CartService.clearCart();
            showAlert('Order placed successfully!', 'success');
            setTimeout(() => {
                loadCart();
                showPage('orders');
            }, 1000);
        } catch (error) {
            console.error('[v0] Place order error:', error.message);
            showAlert('Failed to place order: ' + error.message, 'error');
        }
    } else {
        showAlert('Please login first', 'error');
        setTimeout(() => showPage('login'), 1000);
    }
}

// Load orders
async function loadOrders() {
    try {
        const orders = await ApiService.getUserOrders();
        const ordersList = document.getElementById('ordersList');
        ordersList.innerHTML = '';

        orders.forEach(order => {
            const card = document.createElement('div');
            card.className = 'order-card';
            
            let statusClass = 'status-pending';
            if (order.status === 'COMPLETED') statusClass = 'status-completed';
            else if (order.status === 'CANCELLED') statusClass = 'status-cancelled';

            card.innerHTML = `
                <h4>Order #${order.orderId}</h4>
                <p>Date: ${new Date(order.orderDate).toLocaleDateString()}</p>
                <p>Amount: ₹${order.totalAmount.toFixed(2)}</p>
                <span class="order-status ${statusClass}">${order.status}</span>
            `;
            ordersList.appendChild(card);
        });

        if (orders.length === 0) {
            ordersList.innerHTML = '<p>No orders found</p>';
        }
    } catch (error) {
        showAlert('Failed to load orders', 'error');
    }
}
