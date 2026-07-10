package com.foodiego.canteen.dto;

import java.math.BigDecimal;

public class DashboardStatsResponse {
    private long totalItems;
    private long totalOrders;
    private BigDecimal totalRevenue;

    public DashboardStatsResponse(long totalItems, long totalOrders, BigDecimal totalRevenue) {
        this.totalItems = totalItems;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public long getTotalItems() { return totalItems; }
    public long getTotalOrders() { return totalOrders; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}