package com.foodiego.canteen.dto;

import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodItemDTO {
    private Integer itemId;
    private String name;
    private String category;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
}
