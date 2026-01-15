package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.AddToCartRequestDTO;
import com.ecommerce.project.dto.CartResponseDTO;
import com.ecommerce.project.dto.UpdateCartItemRequestDTO;
import com.ecommerce.project.security.CustomUserDetails;
import com.ecommerce.project.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CartController {
	@Autowired
	private CartService cartServ;
	
	// Add product to cart
	// @PostMapping("/users/{userId}/cart/add") // Remove userId from URL and get user details from JWT token
	// public ResponseEntity<CartResponseDTO> addToCart(@PathVariable String userId, @RequestBody AddToCartRequestDTO addToCartReq){
	@PostMapping("/cart/add")
	public ResponseEntity<CartResponseDTO> addToCart(@Valid @RequestBody AddToCartRequestDTO addToCartReq){
		CartResponseDTO cart = cartServ.addToCart(addToCartReq);
		return ResponseEntity.status(HttpStatus.CREATED).body(cart);
	}
	
	// Get user's cart
	// @GetMapping("/users/{userId}/cart")
	@GetMapping("/cart")
	public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal CustomUserDetails userDetails){
		CartResponseDTO cart = cartServ.getCart();
		return ResponseEntity.ok(cart);
	}
	
	// Update cart item quantity
	// @PutMapping("/users/{userId}/cart/items/{cartItemId}")
	@PutMapping("/cart/items/{cartItemId}")
	public ResponseEntity<CartResponseDTO> updateCartItem(@PathVariable Long cartItemId,@Valid @RequestBody UpdateCartItemRequestDTO updateRequest) {
		CartResponseDTO cart = cartServ.updateCartItem(cartItemId, updateRequest);
		return ResponseEntity.ok(cart);
	}
	
	//Remove item from cart
	@DeleteMapping("/cart/items/{cartItemId}")
	public ResponseEntity<CartResponseDTO> removeCartItem(@PathVariable Long cartItemId){
		CartResponseDTO cart = cartServ.removeCartItem(cartItemId);
		return ResponseEntity.ok(cart);
	}
	
	// Clear entire cart
	@DeleteMapping("/cart")
	public ResponseEntity<String> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails){
		String message = cartServ.clearCart();
		return ResponseEntity.ok(message);
	}
}
