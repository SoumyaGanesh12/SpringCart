package com.ecommerce.project.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecommerce.project.dto.AddToCartRequestDTO;
import com.ecommerce.project.dto.CartItemDTO;
import com.ecommerce.project.dto.CartResponseDTO;
import com.ecommerce.project.dto.UpdateCartItemRequestDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.security.CustomUserDetails;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {
	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private CartItemRepository cartItemRepo;
		
	@Autowired
	private ProductRepository productRepo;
	
	// Authenticated User
	private User getAuthenticatedUser() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		
		if(!(principal instanceof CustomUserDetails)) {
			throw new BadRequestException("User not authenticated");
		}
		
		User user = ((CustomUserDetails) principal).getUser();
		
		if(!user.getActive()) {
			throw new BadRequestException("User account is inactive");
		}
		
		return user;
	}
	
	// Add product to cart
	@Override
	public CartResponseDTO addToCart(AddToCartRequestDTO addReqDto) {
	    // Validate quantity
	    Integer quantity = addReqDto.getQuantity();
	    
	    if (quantity == null || quantity <= 0) {
	        throw new BadRequestException("Quantity must be greater than 0");
	    }

	    User user = getAuthenticatedUser();
		
		// Find product
		Product product = productRepo.findById(addReqDto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product with ID " + addReqDto.getProductId() + " not found"
            ));
		
		if(!product.getActive()) {
			throw new BadRequestException("Product is not available");
		}
		
		// Check stock availability
		if(product.getStockQuantity() < addReqDto.getQuantity()) {
			throw new BadRequestException("Insufficient stock. Only " + product.getStockQuantity() + " units available");
		}
		
		// Get or create cart for user
		Cart cart = cartRepo.findByUserId(user.getId())
			.orElseGet(() -> {
				Cart newCart = new Cart();
				newCart.setUser(user);
				return cartRepo.save(newCart);
			});
		
		// Check if product is already in cart
		CartItem existingItem = cartItemRepo.findByCartCartIdAndProductProductId(cart.getCartId(), product.getProductId())
				.orElse(null);
		
		if(existingItem!=null) {
			// Product already in cart - update quantity
			int newQuantity = existingItem.getQuantity() + addReqDto.getQuantity();
			
			// Check stock again for new total quantity
			if(product.getStockQuantity() < newQuantity) {
				throw new BadRequestException(
					"Cannot add " + addReqDto.getQuantity() + " more. Only " +
					(product.getStockQuantity() - existingItem.getQuantity()) + "units available"
				);
			}
			
			existingItem.setQuantity(newQuantity);
			cartItemRepo.save(existingItem);
		}else {
			// New product - create cart item
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setProduct(product);
			newItem.setQuantity(addReqDto.getQuantity());
			newItem.setPrice(product.getPrice());
			
			cart.addCartItem(newItem);
			cartItemRepo.save(newItem);
		}
		
		// Recalculate cart total
		cart.calculateTotalAmount();
		cartRepo.save(cart);
		
		return convertToResponseDTO(cart);
	}
	
	// Get user's cart
	@Override
    public CartResponseDTO getCart() {
        // Find user
		User user = getAuthenticatedUser();
        
        // Get cart (or create empty one if doesn't exist)
        Cart cart = cartRepo.findByUserId(user.getId())
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUser(user);
                return cartRepo.save(newCart);
            });
        
        return convertToResponseDTO(cart);
    }
	
	// Update cart item quantity
	@Override
    public CartResponseDTO updateCartItem(Long cartItemId, 
                                          UpdateCartItemRequestDTO updateRequest) {
		
//	    System.out.println("Quantity: " + updateRequest.getQuantity());
//	    System.out.println("Quantity type: " + updateRequest.getQuantity().getClass());
	    
		// Validate quantity
        if (updateRequest.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        		
        // Find user
        User user = getAuthenticatedUser();
        
        // Find cart
        Cart cart = cartRepo.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart not found"
            ));
        
        // Find cart item
        CartItem cartItem = cartItemRepo.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart item with ID " + cartItemId + " not found"
            ));
        
        // Verify cart item belongs to this user's cart
        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Cart item does not belong to this user");
        }
        
        // Check stock availability
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < updateRequest.getQuantity()) {
            throw new BadRequestException(
                "Insufficient stock. Only " + product.getStockQuantity() + " units available"
            );
        }
        
        // Update quantity
        cartItem.setQuantity(updateRequest.getQuantity());
        cartItemRepo.save(cartItem);
        
        // Recalculate cart total
        cart.calculateTotalAmount();
        cartRepo.save(cart);
        
        return convertToResponseDTO(cart);
    }
	
    // Remove item from cart
	@Override
    public CartResponseDTO removeCartItem(Long cartItemId) {
        // Find user
		User user = getAuthenticatedUser();
        
        // Find cart
        Cart cart = cartRepo.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart not found"
            ));
        
        // Find cart item
        CartItem cartItem = cartItemRepo.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart item with ID " + cartItemId + " not found"
            ));
        
        // Verify cart item belongs to this user's cart
        if (!cartItem.getCart().getCartId().equals(cart.getCartId())) {
            throw new BadRequestException("Cart item does not belong to this user");
        }
        
        // Remove item
        cart.removeCartItem(cartItem);
        cartItemRepo.delete(cartItem);
        
        // Recalculate cart total
        cart.calculateTotalAmount();
        cartRepo.save(cart);
        
        return convertToResponseDTO(cart);
    }
	
    // Clear entire cart
	@Override
    public String clearCart() {
        // Find user
		User user = getAuthenticatedUser();
		
        // Find cart
        Cart cart = cartRepo.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cart not found"
            ));
        
        // Clear all items
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepo.save(cart);
        
        return "Cart cleared successfully";
    }
	
	// Helper methods
	private CartResponseDTO convertToResponseDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
            .map(this::convertCartItemToDTO)
            .collect(Collectors.toList());
        
        return new CartResponseDTO(
            cart.getCartId(),
            cart.getUser().getUserId(),
            itemDTOs,
            cart.getTotalAmount(),
            cart.getCartItems().size()
        );
    }
    
    private CartItemDTO convertCartItemToDTO(CartItem cartItem) {
        return new CartItemDTO(
            cartItem.getCartItemId(),
            cartItem.getProduct().getProductId(),
            cartItem.getProduct().getProductName(),
            cartItem.getQuantity(),
            cartItem.getPrice(),
            cartItem.getSubtotal()
        );
    }
}
