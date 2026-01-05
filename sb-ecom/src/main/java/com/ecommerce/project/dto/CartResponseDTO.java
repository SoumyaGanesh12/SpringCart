package com.ecommerce.project.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
	private Long cartId;
	private String userId;
	private List<CartItemDTO> items;
	private BigDecimal totalAmount;
	private Integer itemCount;
}
