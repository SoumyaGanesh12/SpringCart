package com.ecommerce.project.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long productId;
	private String productName;
	private String description;
	private BigDecimal price;
	private Integer stockQuantity;
	private String imageUrl;
	private Boolean active;
	private Long categoryId;
	private String categoryName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
