package com.foodiego.canteen.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodiego.canteen.dto.OrderRequest;
import com.foodiego.canteen.entity.FoodItem;
import com.foodiego.canteen.entity.Order;
import com.foodiego.canteen.entity.Order.OrderStatus;
import com.foodiego.canteen.entity.OrderItem;
import com.foodiego.canteen.entity.User;
import com.foodiego.canteen.repository.FoodItemRepository;
import com.foodiego.canteen.repository.OrderItemRepository;
import com.foodiego.canteen.repository.OrderRepository;
import com.foodiego.canteen.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    public Order createOrder(String email, List<OrderRequest.OrderItemRequest> items) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Instantiate the order object in memory
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItemsList = new ArrayList<>();

        // 🌟 2. Loop through items to calculate the complete total amount FIRST
        for (OrderRequest.OrderItemRequest itemRequest : items) {
            FoodItem foodItem = foodItemRepository.findById(itemRequest.getItemId())
                .orElseThrow(() -> new RuntimeException("Food item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Link memory reference to parent
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(foodItem.getPrice());

            orderItemsList.add(orderItem);

            // Track running total
            BigDecimal itemTotal = foodItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 🌟 3. Set the calculated total amount BEFORE hitting the repository
        order.setTotalAmount(totalAmount);
        
        // 🌟 4. Now save the parent order safely (totalAmount is populated, no more constraint errors!)
        Order savedOrder = orderRepository.save(order);

        // 🌟 5. Finally, save each individual order item referencing the persistent order ID
        for (OrderItem orderItem : orderItemsList) {
            orderItem.setOrder(savedOrder); // Set managed database entity
            orderItemRepository.save(orderItem); 
        }

        return savedOrder;
    }
    public Order getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getUserOrders(Integer userId) {
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(userId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Integer orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }

    public long getPendingOrdersCount() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    public long getCompletedOrdersCount() {
        return orderRepository.countByStatus(OrderStatus.COMPLETED);
    }

    public List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderOrderId(orderId);
    }

   /* public Order createOrder(String email, List<OrderRequest.OrderItemRequest> items) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(java.time.LocalDateTime.now());
        order.setUpdatedAt(java.time.LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        Order savedOrder = orderRepository.save(order);

        for (OrderRequest.OrderItemRequest itemRequest : items) {
            FoodItem foodItem = foodItemRepository.findById(itemRequest.getItemId())
                .orElseThrow(() -> new RuntimeException("Food item not found: " + itemRequest.getItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(foodItem.getPrice());

            orderItemRepository.save(orderItem);
            BigDecimal itemTotal = foodItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }*/

    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserUserIdOrderByOrderDateDesc(user.getUserId()); 
    }

    public Order updateOrderStatus(Integer orderId, String status) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }
 // 🌟 KEEP THE NEW METHOD WE JUST FIXED FOR THE API CONTROLLER, AND ADD THIS ONE BACK BELOW IT:
    public Order createOrder(User user, List<OrderItem> items, BigDecimal totalAmount) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount); // Here totalAmount is already passed in, so it's safe!
        order.setStatus(OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }
        
        return savedOrder;
    }
}
