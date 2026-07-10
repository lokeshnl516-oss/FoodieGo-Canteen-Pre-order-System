package com.foodiego.canteen.controller;


import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiego.canteen.dto.ApiResponse;
import com.foodiego.canteen.dto.OrderRequest;
import com.foodiego.canteen.dto.OrderResponse;
import com.foodiego.canteen.entity.Order;
import com.foodiego.canteen.entity.OrderItem;
import com.foodiego.canteen.service.FoodItemService;
import com.foodiego.canteen.service.OrderService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserRestController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<String>> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success("User profile", email));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Debug logging
        System.out.println("[v0] Received order request from user: " + email);
        System.out.println("[v0] Order items: " + request.getItems());
        
        try {
            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "Order items cannot be empty"));
            }
            
            Order order = orderService.createOrder(email, request.getItems());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", convertToOrderResponse(order)));
        } catch (Exception e) {
            System.err.println("[v0] Order creation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Failed to create order: " + e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        try {
            // Fetch all historical orders belonging to this logged-in user email
            List<Order> orders = orderService.getUserOrders(email); 
             
            // Map the list cleanly using your existing convertToOrderResponse method
            List<OrderResponse> responseList = orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(ApiResponse.success("User orders retrieved successfully", responseList));
        } catch (Exception e) {
            System.err.println("[v0] Error fetching orders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Failed to retrieve orders: " + e.getMessage()));
        }
    }
 /*   @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Order order = orderService.createOrder(email, request.getItems());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", convertToOrderResponse(order)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }*/

    private OrderResponse convertToOrderResponse(Order order) {
    	List<OrderResponse.OrderItemResponse> items = (order.getOrderItems() == null ? new ArrayList<OrderItem>() : order.getOrderItems()).stream()
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

	public FoodItemService getFoodItemService() {
		return foodItemService;
	}

	public void setFoodItemService(FoodItemService foodItemService) {
		this.foodItemService = foodItemService;
	}
}
