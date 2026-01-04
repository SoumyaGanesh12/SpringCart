package com.ecommerce.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@Override
	public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
		// Find category by Id
		Category category = categoryRepo.findById(productRequestDTO.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
					"Category with Id " + productRequestDTO.getCategoryId() + " not found"
				));
		
		// Convert DTO to Entity
		Product product = convertToEntity(productRequestDTO, category);
		
		// Save Product
		Product savedProduct = productRepo.save(product);
		
		// Convert Entity to Response DTO
		return convertToResponseDTO(savedProduct);
	}
	
	@Override
	public List<ProductResponseDTO> getAllProducts(){
		List<Product> products = productRepo.findAll();
		return products.stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	@Override
	public ProductResponseDTO getProductById(Long productId) {
		Product product = productRepo.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"Product with Id " + productId + " not found"
			));
		return convertToResponseDTO(product);
	}
	
	@Override
	public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
		// Find existing product
		Product existingProduct = productRepo.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"Product with Id " + productId + " not found"
			));
		
		// Find category
		Category category = categoryRepo.findById(productRequestDTO.getCategoryId())
			.orElseThrow(() -> new ResourceNotFoundException(
				"Category with Id " + productRequestDTO.getCategoryId() + " not found"
			));
		
		// Update fields
		existingProduct.setProductName(productRequestDTO.getProductName());
		existingProduct.setDescription(productRequestDTO.getDescription());
		existingProduct.setPrice(productRequestDTO.getPrice());
		existingProduct.setStockQuantity(productRequestDTO.getStockQuantity());
		existingProduct.setImageUrl(productRequestDTO.getImageUrl());
		// Can update category
		existingProduct.setCategory(category); 
		
		// Save updated product
		Product updatedProduct = productRepo.save(existingProduct);
		
		return convertToResponseDTO(updatedProduct);
	}
	
	@Override
	public String deleteProduct(Long productId) {
		Product product = productRepo.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"Product with Id " + productId + " not found"
			));
		
		productRepo.delete(product);
		return "Product with Id " + productId + " deleted successfully";
	}
	
	@Override
	public List<ProductResponseDTO> getProductsByCategory(Long categoryId){
		// Verify category exists
		categoryRepo.findById(categoryId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"Category with Id " + categoryId + " not found"
		));
		
		List<Product> products = productRepo.findByCategoryCategoryId(categoryId);
		
		return products.stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	@Override
	public List<ProductResponseDTO> searchProducts(String keyword){
		List<Product> products = productRepo.findByProductNameContainingIgnoreCase(keyword);
		
		return products.stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	@Override
	public Page<ProductResponseDTO> getAllProducts(Pageable pageable){
		Page<Product> productPage = productRepo.findAll(pageable);
		return productPage.map(this::convertToResponseDTO);
	}
	
	@Override
	public Page<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable){
		// Verify category exists
		categoryRepo.findById(categoryId)
			.orElseThrow(() -> new ResourceNotFoundException(
					"Category with Id " + categoryId + " not found"
			));
		
		Page<Product> productPage = productRepo.findByCategoryCategoryId(categoryId, pageable);
		return productPage.map(this::convertToResponseDTO);
	}
	
	// Helper methods
	// Convert Request DTO to Entity
	private Product convertToEntity(ProductRequestDTO dto, Category category) {
		Product product = new Product();
		product.setProductName(dto.getProductName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setStockQuantity(dto.getStockQuantity());
		product.setImageUrl(dto.getImageUrl());
		product.setCategory(category);
		product.setActive(true);
		
		return product;
	}
	
	// Convert Entity to Response DTO
	private ProductResponseDTO convertToResponseDTO(Product product) {
		return new ProductResponseDTO(
			product.getProductId(),
			product.getProductName(),
			product.getDescription(),
			product.getPrice(),
			product.getStockQuantity(),
			product.getImageUrl(),
			product.getActive(),
			product.getCategory().getCategoryId(),
			product.getCategory().getCategoryName(),
			product.getCreatedAt(),
			product.getUpdatedAt()
		);
	}
	
}
