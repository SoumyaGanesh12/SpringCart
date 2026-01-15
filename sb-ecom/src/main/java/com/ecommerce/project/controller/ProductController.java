package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ProductController {
	@Autowired
	private ProductService proServ;
	
	// Create product
	@PostMapping("/admin/products")
	public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO prReqdto){
		ProductResponseDTO createdProduct = proServ.createProduct(prReqdto);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
	}
	
	// Get all products without pagination
	@GetMapping("/public/products")
	public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
		List<ProductResponseDTO> products = proServ.getAllProducts();
		return ResponseEntity.ok(products);
	}
	
	// Get product by productId
	@GetMapping("/public/products/{productId}")
	public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long productId) {
		ProductResponseDTO product = proServ.getProductById(productId);
		return ResponseEntity.ok(product);
	}
	
	// Update product
	@PutMapping("/admin/products/{productId}")
	public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequestDTO prReqdto){
		ProductResponseDTO updatedProduct = proServ.updateProduct(productId, prReqdto);
		return ResponseEntity.ok(updatedProduct);
	}
	
	// Delete Product
	@DeleteMapping("/admin/products/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
		String message = proServ.deleteProduct(productId);
		return ResponseEntity.ok(message);
	}
	
	// Get products by category
	@GetMapping("/public/categories/{categoryId}/products")
	public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId){
		List<ProductResponseDTO> products = proServ.getProductsByCategory(categoryId);
		return ResponseEntity.ok(products);
	}
	
	// Search products by name
	@GetMapping("/public/products/search")
	public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword){
		List<ProductResponseDTO> products = proServ.searchProducts(keyword);
		return ResponseEntity.ok(products);
	}
	
	// Get all products with pagination
	@GetMapping("/public/products/page")
	public ResponseEntity<Page<ProductResponseDTO>> getAllProductsWithPagination(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "productId") String sortBy,
		@RequestParam(defaultValue = "asc") String sortDir){
		
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<ProductResponseDTO> productPage = proServ.getAllProducts(pageable);
		return ResponseEntity.ok(productPage);
	}
	
	// Get products by category with pagination
	@GetMapping("/public/categories/{categoryId}/products/page")
	public ResponseEntity<Page<ProductResponseDTO>> getProductsByCategoryWithPagination(
		@PathVariable Long categoryId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "productId") String sortBy,
		@RequestParam(defaultValue = "asc") String sortDir){
		
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<ProductResponseDTO> productPage = proServ.getProductsByCategory(categoryId, pageable);
	
		return ResponseEntity.ok(productPage);
	}
	
}
