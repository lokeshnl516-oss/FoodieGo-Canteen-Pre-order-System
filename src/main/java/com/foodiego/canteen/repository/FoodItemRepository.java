package com.foodiego.canteen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodiego.canteen.entity.FoodItem;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {
    List<FoodItem> findByCategory(String category);
    List<FoodItem> findByIsAvailableTrue();
    List<FoodItem> findByCategoryAndIsAvailable(String category, Boolean isAvailable);
}
