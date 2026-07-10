package com.foodiego.canteen.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodiego.canteen.entity.FoodItem;
import com.foodiego.canteen.entity.Order;
import com.foodiego.canteen.entity.OrderItem;
import com.foodiego.canteen.service.FoodItemService;
import com.foodiego.canteen.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private OrderService orderService;

    private void checkAdminSession(HttpSession session) {
        if (session.getAttribute("admin_id") == null) {
            throw new RuntimeException("Unauthorized");
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        checkAdminSession(session);
        
        long pendingOrders = orderService.getPendingOrdersCount();
        long completedOrders = orderService.getCompletedOrdersCount();
        long totalFoodItems = foodItemService.getAllFoodItems().size();
        
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("totalFoodItems", totalFoodItems);
        return "admin/dashboard";
    }

    @GetMapping("/manage-items")
    public String showManageItems(HttpSession session, Model model) {
        checkAdminSession(session);
        
        List<FoodItem> items = foodItemService.getAllFoodItems();
        model.addAttribute("items", items);
        return "admin/manage-items";
    }

    @PostMapping("/add-item")
    public String addItem(@ModelAttribute FoodItem foodItem, HttpSession session) {
        checkAdminSession(session);
        
        foodItemService.addFoodItem(foodItem);
        return "redirect:/admin/manage-items?success=Item added";
    }

    @PostMapping("/update-item/{itemId}")
    public String updateItem(@PathVariable Integer itemId, @ModelAttribute FoodItem foodItem,
                            HttpSession session) {
        checkAdminSession(session);
        
        foodItem.setItemId(itemId);
        foodItemService.updateFoodItem(foodItem);
        return "redirect:/admin/manage-items?success=Item updated";
    }

    @GetMapping("/delete-item/{itemId}")
    public String deleteItem(@PathVariable Integer itemId, HttpSession session) {
        checkAdminSession(session);
        
        foodItemService.deleteFoodItem(itemId);
        return "redirect:/admin/manage-items?success=Item deleted";
    }

    @GetMapping("/manage-orders")
    public String showManageOrders(HttpSession session, Model model) {
        checkAdminSession(session);
        
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/manage-orders";
    }

    @PostMapping("/update-order-status/{orderId}")
    public String updateOrderStatus(@PathVariable Integer orderId,
                                   @RequestParam String status,
                                   HttpSession session) {
        checkAdminSession(session);
        
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        orderService.updateOrderStatus(orderId, orderStatus);
        return "redirect:/admin/manage-orders?success=Order status updated";
    }

    @GetMapping("/order-details/{orderId}")
    public String showOrderDetails(@PathVariable Integer orderId, HttpSession session, Model model) {
        checkAdminSession(session);
        
        Order order = orderService.getOrderById(orderId);
        List<OrderItem> items = orderService.getOrderItems(orderId);
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        return "admin/order-details";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
