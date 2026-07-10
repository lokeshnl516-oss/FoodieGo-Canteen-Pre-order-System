package com.foodiego.canteen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodiego.canteen.entity.Order;
import com.foodiego.canteen.entity.Order.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserUserIdOrderByOrderDateDesc(Integer userId);
    List<Order> findByStatusOrderByOrderDateDesc(OrderStatus status);
    long countByStatus(OrderStatus status);
}
