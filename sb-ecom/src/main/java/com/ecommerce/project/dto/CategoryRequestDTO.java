package com.ecommerce.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {
	@NotBlank(message = "Category name is required")
	@Size(min = 3, max = 60, message = "Category name must be between 3 and 50 characters")
	private String categoryName;
	
	@Size(max = 500, message = "Category description cannot extend 500 characters")
	private String description;
}
