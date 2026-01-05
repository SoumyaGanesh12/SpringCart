package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddToCartRequestDTO;
import com.ecommerce.project.dto.CartResponseDTO;
import com.ecommerce.project.dto.UpdateCartItemRequestDTO;

public interface CartService {

	// Add product to cart
	CartResponseDTO addToCart(String userId, AddToCartRequestDTO addReqDto);
	
	// Get user's cart
	CartResponseDTO getCart(String userId);
	
	// Update cart item quantity
	CartResponseDTO updateCartItem(String userId, Long cartItemId, UpdateCartItemRequestDTO updateRequest);
    
    // Remove item from cart
    CartResponseDTO removeCartItem(String userId, Long cartItemId);
    
    // Clear entire cart
    String clearCart(String userId);
}
