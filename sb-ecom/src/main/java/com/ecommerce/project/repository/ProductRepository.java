package com.ecommerce.project.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	// Find products by category
	List<Product> findByCategoryCategoryId(Long categoryId);
	
	// Search products by name
	List<Product> findByProductNameContainingIgnoreCase(String keyword);
	
	// Find products within price range
	List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
	
	// Find active products only
	List<Product> findByActiveTrue();
	
	// Find products by category with pagination
	Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);
	
	// Search products with pagination
	Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
	
	// Find all products with pagination
	Page<Product> findAll(Pageable pageable);
}
