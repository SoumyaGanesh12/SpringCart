package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.dto.CategoryRequestDTO;
import com.ecommerce.project.dto.CategoryResponseDTO;

public interface CategoryService {
	List<CategoryResponseDTO> getAllCategories();
	CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);
	String deleteCategory(Long categoryId);
//	Category updateCategory(Category category, Long categoryId);
	CategoryResponseDTO updateCategory(CategoryRequestDTO categoryRequestDTO, Long categoryId);
}
