package com.ecommerce.project.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
	private Long orderItemId;
	private Long productId;
	private String productName;
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal subtotal;
}
