package com.foodiego.canteen.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.foodiego.canteen.entity.Order.OrderStatus; // Adjust package location if needed

public class OrderResponse {
    
    private Integer orderId;       
    private Integer userId;        
    private BigDecimal totalAmount; 
    private OrderStatus status;      
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt; 
    private List<OrderItemResponse> items;

    // Outer No-Args Constructor
    public OrderResponse() {}

    // Outer All-Args Constructor (Clears the bottom controller error)
    public OrderResponse(Integer integer, Integer integer2, BigDecimal totalAmount, 
                         OrderStatus status, LocalDateTime createdAt, 
                         LocalDateTime updatedAt, List<OrderItemResponse> items) {
        this.orderId = integer;
        this.userId = integer2;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
    }

    // --- 🌟 INNER CLASS WITH CORRESPONDING MANUAL CONSTRUCTOR 🌟 ---
    public static class OrderItemResponse {
        private Integer itemId;      // Changed to Long to match entity
        private String itemName;
        private Integer quantity;
        private BigDecimal price; // Changed to BigDecimal to match database precision

        // Inner No-Args Constructor
        public OrderItemResponse() {}

        // Inner All-Args Constructor (Clears the stream map map error!)
        public OrderItemResponse(Integer integer, String itemName, Integer quantity, BigDecimal price) {
            this.itemId = integer;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
        }

        // Inner Getters and Setters
        public Integer getItemId() { return itemId; }
        public void setItemId(Integer itemId) { this.itemId = itemId; }

        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // --- Outer Getters and Setters ---
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}