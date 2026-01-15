package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.CategoryRequestDTO;
import com.ecommerce.project.dto.CategoryResponseDTO;
import com.ecommerce.project.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CategoryController {	
//	Constructor injection
//	private CategoryService catSer;
	
//	public CategoryController(CategoryService catServObj) {
//		this.catSer = catServObj;
//	}
	
//	Field injection
	@Autowired
	private CategoryService catSer;
	
	// Get all categories
	@GetMapping("/public/categories")
	public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(){
		List<CategoryResponseDTO> categoriesList = catSer.getAllCategories();
		return ResponseEntity.ok(categoriesList);
	}
	
	// Create category
	@PostMapping("/public/categories")
	// public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO ) {
	public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO ) {	
		CategoryResponseDTO createdCategory = catSer.createCategory(categoryRequestDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
	}
	
	@DeleteMapping("/admin/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
		String msg = catSer.deleteCategory(categoryId);
		return ResponseEntity.ok(msg);
	}
	
	@PutMapping("/public/categories/{categoryId}")
	public ResponseEntity<CategoryResponseDTO> updateCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO, @PathVariable Long categoryId){
		CategoryResponseDTO updatedCategory = catSer.updateCategory(categoryRequestDTO, categoryId);
		return ResponseEntity.ok(updatedCategory);
	}

}
