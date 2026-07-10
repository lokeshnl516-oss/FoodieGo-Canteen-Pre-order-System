package com.foodiego.canteen.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiego.canteen.dto.ApiResponse;
import com.foodiego.canteen.dto.FoodItemDTO;
import com.foodiego.canteen.entity.FoodItem;
import com.foodiego.canteen.service.FoodItemService;

@RestController
@RequestMapping("/api/food-items")
@CrossOrigin(origins = "*")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodItemDTO>>> getAllFoodItems() {
        List<FoodItem> items = foodItemService.getAllFoodItems();
        List<FoodItemDTO> dtos = items.stream()
            .map(item -> new FoodItemDTO(item.getItemId(), item.getName(), item.getCategory(),
                item.getDescription(), item.getPrice(), item.getIsAvailable()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Food items retrieved", dtos));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<FoodItemDTO>> getFoodItemById(@PathVariable Integer itemId) {
        FoodItem item = foodItemService.getFoodItemById(itemId);
        if (item != null) {
            FoodItemDTO dto = new FoodItemDTO(item.getItemId(), item.getName(), item.getCategory(),
                item.getDescription(), item.getPrice(), item.getIsAvailable());
            return ResponseEntity.ok(ApiResponse.success("Food item found", dto));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<FoodItemDTO>>> getFoodItemsByCategory(@PathVariable String category) {
        List<FoodItem> items = foodItemService.getFoodItemsByCategory(category);
        List<FoodItemDTO> dtos = items.stream()
            .map(item -> new FoodItemDTO(item.getItemId(), item.getName(), item.getCategory(),
                item.getDescription(), item.getPrice(), item.getIsAvailable()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Food items by category", dtos));
    }
}
