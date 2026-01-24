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

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.project.dto.CategoryRequestDTO;
import com.ecommerce.project.dto.CategoryResponseDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
	@Mock
	private CategoryRepository catRepo;
	
	@InjectMocks
	private CategoryServiceImpl catServ;
	
	private Category category1;
	private Category category2;
	private CategoryRequestDTO catReqDTO;
	
	@BeforeEach
	public void setUp() {
		// Create test data before each test
		category1 = new Category();
		category1.setCategoryId(1L);
		category1.setCategoryName("Electronics");
		category1.setDescription("Electronic devices");
		
		category2 = new Category();
		category2.setCategoryId(2L);
		category2.setCategoryName("Books");
		category2.setDescription("Reading materials");
		
		catReqDTO = new CategoryRequestDTO();
		catReqDTO.setCategoryName("Clothing");
		catReqDTO.setDescription("Apparel and fashion");
	}
	
	@Test
	public void testGetAllcategories_ReturnsListOfCategories() {
		List<Category> categories = Arrays.asList(category1, category2);
		when(catRepo.findAll()).thenReturn(categories);
		
		List<CategoryResponseDTO> result = catServ.getAllCategories();
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Electronics", result.get(0).getCategoryName());
		assertEquals("Books", result.get(1).getCategoryName());
		
		verify(catRepo, times(1)).findAll();
	}
	
	@Test
	public void testCreateCategory_Success() {
		// Arrange
		Category savedCategory = new Category();
		savedCategory.setCategoryId(3L);
		savedCategory.setCategoryName("Clothing");
		savedCategory.setDescription("Apparel and fashion");
		
		when(catRepo.findByCategoryName("Clothing")).thenReturn(Optional.empty());
		when(catRepo.save(any(Category.class))).thenReturn(savedCategory);
		
		// Act
		CategoryResponseDTO result = catServ.createCategory(catReqDTO);
		
		// Assert
		assertNotNull(result);
		assertEquals(3L, result.getCategoryId());
		assertEquals("Clothing", result.getCategoryName());
		
		verify(catRepo, times(1)).findByCategoryName("Clothing");
		verify(catRepo, times(1)).save(any(Category.class));
	}
	
	@Test
	public void testCreateCategory_WhenDuplicateName() {
		when(catRepo.findByCategoryName("Clothing")).thenReturn(Optional.of(category1));
		
		BadRequestException exception = assertThrows(
				BadRequestException.class,
				() -> catServ.createCategory(catReqDTO)
		);
		
		assertEquals("Category with name 'Clothing' already exists", exception.getMessage());
		verify(catRepo, times(1)).findByCategoryName("Clothing");
		verify(catRepo, never()).save(any(Category.class));
	}
	
	@Test
	public void testDeleteCategory_Success() {
		when(catRepo.findById(1L)).thenReturn(Optional.of(category1));
		doNothing().when(catRepo).delete(category1);
		
		String result = catServ.deleteCategory(1L);
		
		assertEquals("Category with Id: 1 deleted successfully!", result);
		verify(catRepo, times(1)).findById(1L);
		verify(catRepo, times(1)).delete(category1);
	}
	
	@Test
	public void testDeleteCategory_WhenNotFound() {
		when(catRepo.findById(99L)).thenReturn(Optional.empty());
		
		ResourceNotFoundException ex = assertThrows(
				ResourceNotFoundException.class,
				() -> catServ.deleteCategory(99L)
		);
		
		assertTrue(ex.getMessage().contains("not found"));
		verify(catRepo, times(1)).findById(99L);
		verify(catRepo, never()).delete(any(Category.class));
	}
	
	@Test
	public void testUpdateCategory_Success() {
		CategoryRequestDTO updateDTO = new CategoryRequestDTO();
		updateDTO.setCategoryName("Updated Electronics");
		updateDTO.setDescription("Updated - Electronic devices");
		
		Category updatedCat = new Category();
		updatedCat.setCategoryId(1L);
		updatedCat.setCategoryName("Updated Electronics");
		updatedCat.setDescription("Updated - Electronic devices");
		
		when(catRepo.findById(1L)).thenReturn(Optional.of(category1));
		when(catRepo.save(any(Category.class))).thenReturn(updatedCat);
		
		CategoryResponseDTO res = catServ.updateCategory(updateDTO, 1L);
		
		assertNotNull(res);
		assertEquals("Updated Electronics", res.getCategoryName());
		assertEquals("Updated - Electronic devices", res.getDescription());
		
		verify(catRepo, times(1)).findById(1L);
		verify(catRepo, times(1)).save(any(Category.class));
	}
}
