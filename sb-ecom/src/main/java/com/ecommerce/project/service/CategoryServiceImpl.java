package com.ecommerce.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.project.dto.CategoryRequestDTO;
import com.ecommerce.project.dto.CategoryResponseDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	private CategoryRepository catRepo;
	
	@Override
	public List<CategoryResponseDTO> getAllCategories(){
		List<Category> categories = catRepo.findAll();
		
		// Convert each Category entity to CategoryResponseDTO
		return categories.stream()
				.map(this::convertToResponseDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
		// Check if category name already exists
		if(catRepo.findByCategoryName(categoryRequestDTO.getCategoryName()).isPresent()) {
			throw new BadRequestException(
				"Category with name '" + categoryRequestDTO.getCategoryName() + "' already exists"
			);
		}
		
		// Convert DTO to Entity
		Category category = convertToEntity(categoryRequestDTO);
		
		// Save entity
		Category savedCategory = catRepo.save(category);
		
		// Convert saved entity to DTO and return
		return convertToResponseDTO(savedCategory);
	}
	
	@Override
	public String deleteCategory(Long categoryId) {	
//		Category category = catRepo.findById(categoryId)
//				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));  
		
		Category category = catRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category with Id " + categoryId + " not found!"));
		catRepo.delete(category);
		return "Category with Id: " + categoryId + " deleted successfully!";
	}

	@Override
	public CategoryResponseDTO updateCategory(CategoryRequestDTO categoryRequestDTO, Long categoryId) {
		// Find existing category
	    Category existingCategory = catRepo.findById(categoryId)
	        .orElseThrow(() -> new ResourceNotFoundException("Category with Id" + categoryId + " not found!"));
	    
	    // Update fields
	    existingCategory.setCategoryName(categoryRequestDTO.getCategoryName());
	    existingCategory.setDescription(categoryRequestDTO.getDescription());
	  
	    // Save updated entity
	    Category updatedCategory = catRepo.save(existingCategory);
	    
	    // Convert to DTO and return
	    return convertToResponseDTO(updatedCategory);
	}
	
	// Helper methods for conversion
	private CategoryResponseDTO convertToResponseDTO(Category category) {
		return new CategoryResponseDTO(
			category.getCategoryId(),
			category.getCategoryName(),
			category.getDescription()
		);
	}
	private Category convertToEntity(CategoryRequestDTO categoryReqDTO) {
		Category category = new Category();
		// categoryId is generated in DB
		category.setCategoryName(categoryReqDTO.getCategoryName());
        category.setDescription(categoryReqDTO.getDescription());
        return category;
	}
	
}
