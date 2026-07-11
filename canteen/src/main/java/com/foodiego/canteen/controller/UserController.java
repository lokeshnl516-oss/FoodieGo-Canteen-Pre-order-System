package com.foodiego.canteen.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.foodiego.canteen.entity.FoodItem;
import com.foodiego.canteen.entity.Order;
import com.foodiego.canteen.entity.OrderItem;
import com.foodiego.canteen.entity.User;
import com.foodiego.canteen.service.FoodItemService;
import com.foodiego.canteen.service.OrderService;
import com.foodiego.canteen.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
@CrossOrigin(origins = "https://lokeshnl516-oss.github.io", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private OrderService orderService;

    private static final String CART_SESSION_KEY = "cart";

    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }
        
        List<FoodItem> items = foodItemService.getAvailableFoodItems();
        Map<String, List<FoodItem>> itemsByCategory = new LinkedHashMap<>();
        for (FoodItem item : items) {
            itemsByCategory.computeIfAbsent(item.getCategory(), k -> new ArrayList<>()).add(item);
        }
        
        model.addAttribute("itemsByCategory", itemsByCategory);
        model.addAttribute("totalItems", items.size());
        return "user/menu";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam Integer itemId, @RequestParam Integer quantity,
                           HttpSession session, Model model) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        FoodItem item = foodItemService.getFoodItemById(itemId);
        
        @SuppressWarnings("unchecked")
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new HashMap<>();
        }

        if (cart.containsKey(itemId)) {
            CartItem cartItem = cart.get(itemId);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cart.put(itemId, new CartItem(item, quantity));
        }

        session.setAttribute(CART_SESSION_KEY, cart);
        model.addAttribute("message", "Item added to cart!");
        return "redirect:/user/menu";
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        @SuppressWarnings("unchecked")
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new HashMap<>();
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cart.values()) {
            totalAmount = totalAmount.add(item.getItem().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        model.addAttribute("cart", cart.values());
        model.addAttribute("totalAmount", totalAmount);
        return "user/cart";
    }

    @PostMapping("/place-order")
    public String placeOrder(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        @SuppressWarnings("unchecked")
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("error", "Cart is empty");
            return "redirect:/user/cart";
        }

        User user = userService.getUserById(userId);
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.values()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItem(cartItem.getItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getItem().getPrice());
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(cartItem.getItem().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }

        orderService.createOrder(user, orderItems, totalAmount);
        session.removeAttribute(CART_SESSION_KEY);
        model.addAttribute("success", "Order placed successfully!");
        return "redirect:/user/orders";
    }

    @GetMapping("/orders")
    public String showOrders(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        List<Order> orders = orderService.getUserOrders(userId);
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Inner class for cart items
    public static class CartItem {
        private FoodItem item;
        private Integer quantity;

        public CartItem(FoodItem item, Integer quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public FoodItem getItem() { return item; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
