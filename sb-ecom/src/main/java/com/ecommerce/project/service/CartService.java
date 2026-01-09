package com.ecommerce.project.service;

import com.ecommerce.project.dto.AddToCartRequestDTO;
import com.ecommerce.project.dto.CartResponseDTO;
import com.ecommerce.project.dto.UpdateCartItemRequestDTO;

public interface CartService {

	// Add product to cart
	CartResponseDTO addToCart(AddToCartRequestDTO addReqDto);
	
	// Get user's cart
	CartResponseDTO getCart();
	
	// Update cart item quantity
	CartResponseDTO updateCartItem(Long cartItemId, UpdateCartItemRequestDTO updateRequest);
    
    // Remove item from cart
    CartResponseDTO removeCartItem(Long cartItemId);
    
    // Clear entire cart
    String clearCart();
}
