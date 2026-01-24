package com.ecommerce.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.project.dto.AddToCartRequestDTO;
import com.ecommerce.project.dto.CartResponseDTO;
import com.ecommerce.project.dto.UpdateCartItemRequestDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
	@Mock
	private CartRepository cartRepo;
	
	@Mock
	private CartItemRepository cartItemRepo;
	
	@Mock
	private UserRepository userRepo;
	
	@Mock
	private ProductRepository prodRepo;
	
	@InjectMocks
	private CartServiceImpl cartServ;
	
	private User user;
	private Cart cart;
	private Product product;
	private Category category;
	private CartItem cartItem;
	private AddToCartRequestDTO addToCartReqDTO;
	
	@BeforeEach
	public void setup() {
		user = new User();
		user.setId(1L);
		user.setUserId("U0001");
		user.setEmail("john@example.com");
		user.setFirstName("John");
		user.setLastName("Mark");
		user.setPhoneNumber("1234567890");
		user.setAddress("123 Main St");
        user.setRole(User.Role.CUSTOMER);
        user.setActive(true);
        
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        
        product = new Product();
        product.setProductId(1L);
        product.setProductName("MacBook Pro");
        product.setPrice(new BigDecimal("2499.99"));
        product.setStockQuantity(40);
        product.setActive(true);
        product.setCategory(category);
        
        cart = new Cart();
        cart.setCartId(101L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);
        
        cartItem = new CartItem();
        cartItem.setCartItemId(201L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(new BigDecimal("2499.99"));
        
        addToCartReqDTO = new AddToCartRequestDTO();
        addToCartReqDTO.setProductId(1L);
        addToCartReqDTO.setQuantity(2);
	}
	
	@Test
	public void addToCart_ShouldCreateNewCart_WhenCartDoesNotExist() {
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
		when(prodRepo.findById(1L)).thenReturn(Optional.of(product));
		when(cartRepo.findByUserId(1L)).thenReturn(Optional.empty());
		when(cartRepo.save(any(Cart.class))).thenReturn(cart);
		when(cartItemRepo.findByCartCartIdAndProductProductId(anyLong(), anyLong()))
			.thenReturn(Optional.empty());
		when(cartItemRepo.save(any(CartItem.class))).thenReturn(cartItem);
		
		CartResponseDTO result = cartServ.addToCart("U0001",addToCartReqDTO);
		
		assertNotNull(result);
		
		verify(userRepo, times(1)).findByUserId("U0001");
		verify(prodRepo, times(1)).findById(1L);
		verify(cartRepo, times(2)).save(any(Cart.class)); // Create and update
		verify(cartItemRepo, times(1)).save(any(CartItem.class));
	}
	
	@Test
	public void addToCart_ShouldAddNewItem_WhenProductNotInCart() {
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(prodRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepo.findByCartCartIdAndProductProductId(101L, 1L))
            .thenReturn(Optional.empty());
        when(cartItemRepo.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        
        // Act
        CartResponseDTO result = cartServ.addToCart("U0001", addToCartReqDTO);
        
        // Assert
        assertNotNull(result);
        
        verify(cartItemRepo, times(1)).save(any(CartItem.class));
        verify(cartRepo, times(1)).save(any(Cart.class));
	}
	
	@Test
	public void addToCart_ShouldUpdateQuantity_WhenProductExists() {
		cart.getCartItems().add(cartItem);
		cartItem.setQuantity(2);
		
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
		when(prodRepo.findById(1L)).thenReturn(Optional.of(product));
		when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(cartItemRepo.findByCartCartIdAndProductProductId(101L, 1L))
		.thenReturn(Optional.of(cartItem));
		when(cartItemRepo.save(any(CartItem.class))).thenReturn(cartItem);
		when(cartRepo.save(any(Cart.class))).thenReturn(cart);
		
		CartResponseDTO res = cartServ.addToCart("U0001", addToCartReqDTO);
		
		assertNotNull(res);
		assertEquals(4, cartItem.getQuantity());
		verify(cartItemRepo, times(1)).save(any(CartItem.class));
	}
	
	@Test
	public void addToCart_ShouldFail_WhenUserNotFound() {
		when(userRepo.findByUserId("U9999")).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class,
				() -> cartServ.addToCart("U9999", addToCartReqDTO));
		
		verify(userRepo, times(1)).findByUserId("U9999");
		verify(cartRepo, never()).save(any(Cart.class));
	}
	
	@Test
	public void addToCart_ShouldFail_WhenProductNotFound() {
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
		when(prodRepo.findById(999L)).thenReturn(Optional.empty());
		
		addToCartReqDTO.setProductId(999L);
		
		assertThrows(ResourceNotFoundException.class,
				() -> cartServ.addToCart("U0001", addToCartReqDTO));
		
		verify(prodRepo, times(1)).findById(999L);
		verify(cartItemRepo, never()).save(any(CartItem.class));
	}
	
	@Test
	public void addToCart_ShouldFail_WhenInsufficientStock() {
        // Arrange
        product.setStockQuantity(1);  // Only 1 in stock
        addToCartReqDTO.setQuantity(5);  // Trying to add 5
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(prodRepo.findById(1L)).thenReturn(Optional.of(product));
        
        // Act & Assert
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> cartServ.addToCart("U0001", addToCartReqDTO)
        );
        
        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(cartItemRepo, never()).save(any(CartItem.class));
    }
    
    @Test
    public void addToCart_ShouldFail_WhenProductInactive() {
        // Arrange
        product.setActive(false);
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(prodRepo.findById(1L)).thenReturn(Optional.of(product));
        
        // Act & Assert
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> cartServ.addToCart("U0001", addToCartReqDTO)
        );
        
        assertTrue(exception.getMessage().contains("not available"));
    }
	
    @Test
    public void addToCart_ShouldFail_WhenUserInactive() {
    	user.setActive(false);
    	
    	when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
    	
    	BadRequestException ex = assertThrows(BadRequestException.class,
    			() -> cartServ.addToCart("U0001", addToCartReqDTO));
    	
    	assertTrue(ex.getMessage().contains("inactive"));
    }
    
    @Test
    public void getCart_ShouldReturnCart() {
    	cart.getCartItems().add(cartItem);
    	
    	when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
    	when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
    	
    	CartResponseDTO result = cartServ.getCart("U0001");
    	
    	assertNotNull(result);
    	assertEquals(101L, result.getCartId());
    	assertEquals("U0001", result.getUserId());
    	assertEquals(1, result.getItemCount());
    	
    	verify(userRepo, times(1)).findByUserId("U0001");
    }
    
    @Test
    public void updateCartItem_ShouldUpdateQuantity() {
    	cart.getCartItems().add(cartItem);
    	UpdateCartItemRequestDTO updateReq = new UpdateCartItemRequestDTO();
    	updateReq.setQuantity(5);
    	
    	when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
    	when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
    	when(cartItemRepo.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepo.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        
        CartResponseDTO result = cartServ.updateCartItem("U0001", 1L, updateReq);
        
        assertNotNull(result);
        assertEquals(5, cartItem.getQuantity());
        
        verify(cartItemRepo, times(1)).save(any(CartItem.class));
    }
    
    @Test
    public void removeCartItem_ShouldRemoveItem() {
    	cart.getCartItems().add(cartItem);
    	
    	when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
    	when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepo.findById(1L)).thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepo).delete(cartItem);
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        
        CartResponseDTO res = cartServ.removeCartItem("U0001", 1L);
        
        assertNotNull(res);
        verify(cartItemRepo, times(1)).delete(cartItem);
        verify(cartRepo, times(1)).save(any(Cart.class));
    }
    
    @Test
    public void clearCart_ShouldClearAllItems() {
        // Arrange
        cart.getCartItems().add(cartItem);
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        
        // Act
        String result = cartServ.clearCart("U0001");
        
        // Assert
        assertTrue(result.contains("cleared successfully"));
        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalAmount());
        
        verify(cartRepo, times(1)).save(any(Cart.class));
    }
}

