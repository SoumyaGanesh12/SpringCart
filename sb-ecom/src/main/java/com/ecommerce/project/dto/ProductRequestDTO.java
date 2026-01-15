package com.ecommerce.project.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
	
	@NotBlank(message = "Product name is required")
	@Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
	private String productName;
	
	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;
	
	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.01", message = "Price must be greater than 0")
	private BigDecimal price;
	
	@NotNull(message = "Stocky quantity is required")
	@Min(value = 0, message = "Stock quantity cannot be negative")
	private Integer stockQuantity;
	
	@Size(max = 500, message = "Image URL cannot exceed 500 characters")
	private String imageUrl;
	
	@NotNull(message = "Category is required")
	private Long categoryId;
	
}
