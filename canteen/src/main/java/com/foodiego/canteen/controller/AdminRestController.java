package com.foodiego.canteen.controller;


import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodiego.canteen.dto.ApiResponse;
import com.foodiego.canteen.dto.DashboardStatsResponse;
import com.foodiego.canteen.dto.FoodItemDTO;
import com.foodiego.canteen.dto.OrderResponse;
import com.foodiego.canteen.entity.FoodItem;
import com.foodiego.canteen.entity.Order;
import com.foodiego.canteen.service.FoodItemService;
import com.foodiego.canteen.service.OrderService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "https://lokeshnl516-oss.github.io", allowCredentials = "true")
public class AdminRestController {    

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> getDashboard() {
        long totalItems = foodItemService.getAllFoodItems().size();
        long totalOrders = orderService.getAllOrders().size();
        BigDecimal totalRevenue = orderService.getAllOrders().stream()
            .map(Order::getTotalAmount)
            .filter(amount -> amount != null) // Safeguard against null values
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", 
        	    new DashboardStatsResponse(totalItems, totalOrders, totalRevenue)
        	)); 
    }

    @GetMapping("/food-items")
    public ResponseEntity<ApiResponse<List<FoodItemDTO>>> getAllFoodItems() {
        List<FoodItem> items = foodItemService.getAllFoodItems();
        List<FoodItemDTO> dtos = items.stream()
            .map(item -> new FoodItemDTO(item.getItemId(), item.getName(), item.getCategory(),
                item.getDescription(), item.getPrice(), item.getIsAvailable())) 
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved", dtos));
    }

    @PostMapping("/food-items")
    public ResponseEntity<ApiResponse<FoodItemDTO>> createFoodItem(@RequestBody FoodItemDTO dto) {
        FoodItem item = new FoodItem();
        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setIsAvailable(true);

        FoodItem saved = foodItemService.saveFoodItem(item);
        FoodItemDTO response = new FoodItemDTO(saved.getItemId(), saved.getName(), saved.getCategory(),
            saved.getDescription(), saved.getPrice(), saved.getIsAvailable());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Food item created", response));
    }

    @PutMapping("/food-items/{itemId}")
    public ResponseEntity<ApiResponse<FoodItemDTO>> updateFoodItem(@PathVariable Integer itemId, @RequestBody FoodItemDTO dto) {
        FoodItem item = foodItemService.getFoodItemById(itemId);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "Food item not found"));
        }

        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());

        FoodItem updated = foodItemService.saveFoodItem(item);
        FoodItemDTO response = new FoodItemDTO(updated.getItemId(), updated.getName(), updated.getCategory(),
            updated.getDescription(), updated.getPrice(), updated.getIsAvailable());

        return ResponseEntity.ok(ApiResponse.success("Food item updated", response));
    }

    @DeleteMapping("/food-items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteFoodItem(@PathVariable Integer itemId) {
        FoodItem item = foodItemService.getFoodItemById(itemId);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "Food item not found"));
        }

        foodItemService.deleteFoodItem(itemId);
        return ResponseEntity.ok(ApiResponse.success("Food item deleted", null));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponse> responses = orders.stream()
            .map(this::convertToOrderResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All orders", responses));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Integer orderId, 
                                                                        @RequestParam String status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        if (order != null) {
            return ResponseEntity.ok(ApiResponse.success("Order status updated", convertToOrderResponse(order)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(404, "Order not found"));
    }

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getOrderItems().stream()
            .map(oi -> new OrderResponse.OrderItemResponse(
                oi.getFoodItem().getItemId(),
                oi.getFoodItem().getName(),
                oi.getQuantity(),
                oi.getPrice()
            ))
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getOrderId(),
            order.getUser().getUserId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            items
        );
    }
}
