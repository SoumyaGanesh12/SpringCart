package com.ecommerce.project.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

	private Long cartItemId;
	private Long productId;
	private String productName;
	private Integer quantity;
	private BigDecimal price; // Price per unit
	private BigDecimal subtotal; // price x quantity
}
