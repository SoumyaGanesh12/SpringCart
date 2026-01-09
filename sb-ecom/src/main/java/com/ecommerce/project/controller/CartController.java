package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ecommerce.project.service.CartService;

@RestController
@RequestMapping("/api")
public class CartController {
	@Autowired
	private CartService cartServ;
	
	// Add product to cart
	// @PostMapping("/users/{userId}/cart/add")
	@PostMapping("/cart/add")
	public ResponseEntity<CartResponseDTO> addToCart(@PathVariable String userId, @RequestBody AddToCartRequestDTO addToCartReq){
		CartResponseDTO cart = cartServ.addToCart(userId, addToCartReq);
		return ResponseEntity.status(HttpStatus.CREATED).body(cart);
	}
	
	// Get user's cart
	@GetMapping("/users/{userId}/cart")
	public ResponseEntity<CartResponseDTO> getCart(@PathVariable String userId){
		CartResponseDTO cart = cartServ.getCart(userId);
		return ResponseEntity.ok(cart);
	}
	
	// Update cart item quantity
	@PutMapping("/users/{userId}/cart/items/{cartItemId}")
	public ResponseEntity<CartResponseDTO> updateCartItem(@PathVariable String userId, @PathVariable Long cartItemId, @RequestBody UpdateCartItemRequestDTO updateRequest) {
		CartResponseDTO cart = cartServ.updateCartItem(userId, cartItemId, updateRequest);
		return ResponseEntity.ok(cart);
	}
	
	//Remove item from cart
	@DeleteMapping("/users/{userId}/cart/items/{cartItemId}")
	public ResponseEntity<CartResponseDTO> removeCartItem(@PathVariable String userId, @PathVariable Long cartItemId){
		CartResponseDTO cart = cartServ.removeCartItem(userId, cartItemId);
		return ResponseEntity.ok(cart);
	}
	
	// Clear entire cart
	@DeleteMapping("/users/{userId}/cart")
	public ResponseEntity<String> clearCart(@PathVariable String userId){
		String message = cartServ.clearCart(userId);
		return ResponseEntity.ok(message);
	}
}
