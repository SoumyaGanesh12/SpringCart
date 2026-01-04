package com.ecommerce.project.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;

public interface ProductService {
	// Create product
	ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
	
	// Get all products
	List<ProductResponseDTO> getAllProducts();
	
	// Get product by Id
	ProductResponseDTO getProductById(Long productId);
	
	// Update product
	ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO);
	
	// Delete product
	String deleteProduct(Long productId);
	
	// Get products by category
	List<ProductResponseDTO> getProductsByCategory(Long categoryId);
	
	// Search products by name
	List<ProductResponseDTO> searchProducts(String keyword);
	
	// Get all products with pagination
	Page<ProductResponseDTO> getAllProducts(Pageable pageable);
	
	// Get products by category with pagination
	Page<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable);
}
