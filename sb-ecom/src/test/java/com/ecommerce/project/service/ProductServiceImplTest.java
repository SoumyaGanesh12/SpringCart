package com.ecommerce.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.ecommerce.project.dto.ProductRequestDTO;
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
	@Mock
	private ProductRepository proRepo;
	
	@Mock
	private CategoryRepository catRepo;
	
	@InjectMocks
	private ProductServiceImpl proServ;
	
	private Product pro1;
	private Product pro2;
	private Category category;
	private ProductRequestDTO proReqDTO;
	
	@BeforeEach
	public void setUp() {
		// Create test category
		category = new Category();
		category.setCategoryId(1L);
		category.setCategoryName("Electronics");
		category.setDescription("Electronic Devices");
		
		// Create test products
		pro1 = new Product();
		pro1.setProductId(1001L);
		pro1.setProductName("MacBook Pro");
		pro1.setDescription("16-inch laptop");
		pro1.setPrice(new BigDecimal("2499.99"));
		pro1.setStockQuantity(50);
		pro1.setActive(true);
		pro1.setCategory(category);
		
		pro2 = new Product();
		pro2.setProductId(1002L);
		pro2.setProductName("iPhone 15");
		pro2.setDescription("Latest iPhone");
		pro2.setPrice(new BigDecimal("899.99"));
		pro2.setStockQuantity(100);
		pro2.setActive(true);
		pro2.setCategory(category);
		
		// Create Request DTO
		proReqDTO = new ProductRequestDTO();
		proReqDTO.setProductName("iPad Pro");
		proReqDTO.setDescription("12.9-inch tablet");
		proReqDTO.setPrice(new BigDecimal("1099.99"));
		proReqDTO.setStockQuantity(30);
		proReqDTO.setImageUrl("https://example.com/ipad.jpg");
		proReqDTO.setCategoryId(1L);
	}
	
	@Test
	public void getAllProducts_ShouldReturnList() {
		when(proRepo.findAll()).thenReturn(Arrays.asList(pro1, pro2));
		
		List<ProductResponseDTO> result = proServ.getAllProducts();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("MacBook Pro", result.get(0).getProductName());
		assertEquals("iPhone 15", result.get(1).getProductName());
		
		verify(proRepo, times(1)).findAll();
	}
	
	@Test
	public void getProductById_ShouldReturnProduct() {
		when(proRepo.findById(1001L)).thenReturn(Optional.of(pro1));
		
		ProductResponseDTO res = proServ.getProductById(1001L);
		
		assertNotNull(res);
		assertEquals(1001L, res.getProductId());
		assertEquals("MacBook Pro", res.getProductName());
		assertEquals(new BigDecimal("2499.99"), res.getPrice());
		
		verify(proRepo, times(1)).findById(1001L);
	}
	
	@Test
	public void getProductById_ShouldFail_WhenNotFound() {
		when(proRepo.findById(99L)).thenReturn(Optional.empty());
		
		assertThrows(
				ResourceNotFoundException.class,
				() -> proServ.getProductById(99L)
		);
		
		verify(proRepo, times(1)).findById(99L);
	}
	
	@Test
	public void createProduct_ShouldSucess() {
		Product savedProduct = new Product();
		savedProduct.setProductId(1003L);
		savedProduct.setProductName("iPad Pro");
		savedProduct.setPrice(new BigDecimal("1099.99"));
		savedProduct.setCategory(category);
		
		when(catRepo.findById(1L)).thenReturn(Optional.of(category));
		when(proRepo.save(any(Product.class))).thenReturn(savedProduct);
		
		ProductResponseDTO res = proServ.createProduct(proReqDTO);
		
		assertNotNull(res);
		assertEquals("iPad Pro", res.getProductName());
        assertEquals(1L, res.getCategoryId());
        
		verify(catRepo, times(1)).findById(1L);
		verify(proRepo, times(1)).save(any(Product.class));
	}
	
	@Test
	public void createProduct_ShouldFail_WhenCategoryNotFound() {
		when(catRepo.findById(99L)).thenReturn(Optional.empty());
		proReqDTO.setCategoryId(99L);
		
		assertThrows(
				ResourceNotFoundException.class,
				() -> proServ.createProduct(proReqDTO)
		);
		
		verify(catRepo, times(1)).findById(99L);
		verify(proRepo, never()).save(any(Product.class));
	}
	
	@Test
	public void deleteProduct_ShouldSuccess() {
		when(proRepo.findById(1001L)).thenReturn(Optional.of(pro1));
		doNothing().when(proRepo).delete(pro1);
		
		String result = proServ.deleteProduct(1001L);
		assertTrue(result.contains("deleted successfully"));
		verify(proRepo, times(1)).findById(1001L);
		verify(proRepo, times(1)).delete(pro1);
	}
	
	@Test
	public void deleteProduct_ShouldFail_WhenNotFound() {
	    // Arrange
	    when(proRepo.findById(999L)).thenReturn(Optional.empty());
	    
	    // Act & Assert
	    assertThrows(ResourceNotFoundException.class,
	        () -> proServ.deleteProduct(999L));
	    
	    verify(proRepo, times(1)).findById(999L);
	    verify(proRepo, never()).delete(any(Product.class));
	}
	
	@Test
	public void updateProduct_ShouldSuccess() {
		ProductRequestDTO updateDTO = new ProductRequestDTO();
		updateDTO.setProductName("Updated MacBook Pro");
	    updateDTO.setDescription("Updated 16-inch laptop");
	    updateDTO.setPrice(new BigDecimal("2699.99"));
	    updateDTO.setStockQuantity(40);
	    updateDTO.setImageUrl("https://example.com/updated.jpg");
	    updateDTO.setCategoryId(1L);
	    
	    Product updatedProduct = new Product();
	    updatedProduct.setProductId(1001L);
	    updatedProduct.setProductName("Updated MacBook Pro");
	    updatedProduct.setPrice(new BigDecimal("2699.99"));
	    updatedProduct.setCategory(category);
	    
	    when(proRepo.findById(1001L)).thenReturn(Optional.of(pro1));
	    when(catRepo.findById(1L)).thenReturn(Optional.of(category));
	    when(proRepo.save(any(Product.class))).thenReturn(updatedProduct);
	    
	    ProductResponseDTO res = proServ.updateProduct(1001L, updateDTO);
	    
	    assertNotNull(res);
	    assertEquals("Updated MacBook Pro", res.getProductName());
	    assertEquals(new BigDecimal("2699.99"), res.getPrice());
	    
	    verify(proRepo, times(1)).findById(1001L);
	    verify(catRepo, times(1)).findById(1L);
	    verify(proRepo, times(1)).save(any(Product.class));
	}
	
	@Test
	public void updateProduct_ShouldFail_WhenProductNotFound() {
		when(proRepo.findById(99L)).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class,
				() -> proServ.updateProduct(99L, proReqDTO));
		
		verify(proRepo, times(1)).findById(99L);
		verify(proRepo, never()).save(any(Product.class));
	}
	
	@Test
	public void searchProducts_ShouldReturnMatchingProducts() {
		when(proRepo.findByProductNameContainingIgnoreCase("mac"))
			.thenReturn(Arrays.asList(pro1));
		
		List<ProductResponseDTO> result = proServ.searchProducts("mac");
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("MacBook Pro", result.get(0).getProductName());
		
		verify(proRepo, times(1)).findByProductNameContainingIgnoreCase("mac");
	}
	
	@Test
	public void getProductsByCategory_ShouldReturnProducts() {
		when(catRepo.findById(1L)).thenReturn(Optional.of(category));
		when(proRepo.findByCategoryCategoryId(1L)).thenReturn(Arrays.asList(pro1, pro2));
		
		List<ProductResponseDTO> res = proServ.getProductsByCategory(1L);
		
		assertNotNull(res);
		assertEquals(2, res.size());
		
		verify(catRepo, times(1)).findById(1L);
		verify(proRepo, times(1)).findByCategoryCategoryId(1L);
	}
	
	@Test
	public void getAllProductsWithPagination_ShouldReturnPage() {
		Pageable pageable = PageRequest.of(0,  10);
		List<Product> prodList = Arrays.asList(pro1, pro2);
		Page<Product> productPage = new PageImpl<>(prodList, pageable, 2);
		
		when(proRepo.findAll(pageable)).thenReturn(productPage);
		
		Page<ProductResponseDTO> result = proServ.getAllProducts(pageable);
		
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		assertEquals(2, result.getContent().size());
		assertEquals("MacBook Pro", result.getContent().get(0).getProductName());
		
		verify(proRepo, times(1)).findAll(pageable);
	}
	
	@Test
	public void getProductsByCategoryWithPagination_ShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Product> prodList = Arrays.asList(pro1, pro2);
		Page<Product> prodPage = new PageImpl<>(prodList, pageable, 2);
				
		when(catRepo.findById(1L)).thenReturn(Optional.of(category));
		when(proRepo.findByCategoryCategoryId(1L, pageable)).thenReturn(prodPage);
		
		Page<ProductResponseDTO> res = proServ.getProductsByCategory(1L, pageable);
		
		assertNotNull(res);
		assertEquals(2, res.getTotalElements());
		assertEquals(2, res.getContent().size());
		
		verify(catRepo, times(1)).findById(1L);
		verify(proRepo, times(1)).findByCategoryCategoryId(1L, pageable);
	}
}
