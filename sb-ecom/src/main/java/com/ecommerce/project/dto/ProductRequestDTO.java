package com.ecommerce.project.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
	
	private String productName;
	private String description;
	private BigDecimal price;
	private Integer stockQuantity;
	private String imageUrl;
	private Long categoryId;
	
}
