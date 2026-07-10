package com.foodiego.canteen.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodiego.canteen.entity.FoodItem;
import com.foodiego.canteen.repository.FoodItemRepository;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    public FoodItem addFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    public FoodItem getFoodItemById(Integer itemId) {
        return foodItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Food item not found"));
    }

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    public List<FoodItem> getAvailableFoodItems() {
        return foodItemRepository.findByIsAvailableTrue();
    }

    public List<FoodItem> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategory(category);
    }

    public List<FoodItem> getAvailableFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategoryAndIsAvailable(category, true);
    }

    public FoodItem updateFoodItem(FoodItem foodItem) {
        FoodItem existing = getFoodItemById(foodItem.getItemId());
        existing.setName(foodItem.getName());
        existing.setCategory(foodItem.getCategory());
        existing.setDescription(foodItem.getDescription());
        existing.setPrice(foodItem.getPrice());
        existing.setIsAvailable(foodItem.getIsAvailable());
        return foodItemRepository.save(existing);
    }

    public void deleteFoodItem(Integer itemId) {
        foodItemRepository.deleteById(itemId);
    }

    public void toggleAvailability(Integer itemId) {
        FoodItem foodItem = getFoodItemById(itemId);
        foodItem.setIsAvailable(!foodItem.getIsAvailable());
        foodItemRepository.save(foodItem);
    }
    public FoodItem saveFoodItem(FoodItem item) {
        return foodItemRepository.save(item);
    }
}
