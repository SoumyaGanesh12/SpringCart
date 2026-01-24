package com.ecommerce.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.project.dto.OrderItemDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.dto.PlaceOrderRequestDTO;
import com.ecommerce.project.dto.UpdateOrderStatusRequestDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private CartRepository cartRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
    private UserRepository userRepo;
	
	// Get authenticated user
//	private User getAuthenticatedUser() {
//		Object principal = SecurityContextHolder.getContext()
//				.getAuthentication()
//				.getPrincipal();
//		
//		if(!(principal instanceof CustomUserDetails)) {
//			throw new BadRequestException("User not authenticated");
//		}
//		
//		User user = ((CustomUserDetails) principal).getUser();
//		if(!user.getActive()) throw new BadRequestException("User account is inactive");
//		
//		return user;
//	}
	
	// Place order from cart
	@Override
	public OrderResponseDTO placeOrder(String userId, PlaceOrderRequestDTO placeOrderReqdto) {
		// Get authenticated user
		User user = userRepo.findByUserId(userId)
	         .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		// FInd user's cart
		Cart cart = cartRepo.findByUserId(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Cart not found for user " + user.getUserId()
		));
		
		// Check if cart is empty
		if(cart.getCartItems().isEmpty()) {
			throw new BadRequestException("Cannot place order. Cart is empty");
		}
		
		// Validate stock for all items before creating order
		for(CartItem cartItem: cart.getCartItems()) {
			Product product = cartItem.getProduct();
			if(product.getStockQuantity() < cartItem.getQuantity()) {
				throw new BadRequestException(
						"Insufficient stock for " + product.getProductName() +
						". Only " + product.getStockQuantity() + " units available"
				);
			}
		}
		
		// Create new order
		Order order = new Order();
		order.setUser(user);
		order.setShippingAddress(placeOrderReqdto.getShippingAddress());
		order.setStatus(Order.OrderStatus.PENDING);
		
		// Convert cart items to order items
		List<OrderItem> orderItems = new ArrayList<>();
		
		for(CartItem cartItem: cart.getCartItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());
			
			orderItems.add(orderItem);
			
			// Deduct stock from product
			Product product = cartItem.getProduct();
			product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
			productRepo.save(product);
		}
		
		// Set order items to order
		order.setOrderItems(orderItems);
		
		// Calculate total
		order.calculateTotalAmount();
		
		// Save order(cascade saves order items too)
		Order savedOrder = orderRepo.save(order);
		
		// Clear cart
		cart.getCartItems().clear();
		cart.setTotalAmount(java.math.BigDecimal.ZERO);
		cartRepo.save(cart);
		
		return convertToResponseDTO(savedOrder);
	}
	
	// Get order by orderId
	@Override
	public OrderResponseDTO getOrderById(String userId, String orderId) {
		// Get authenticated user
		User user = userRepo.findByUserId(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		    
		
		Order order = orderRepo.findByOrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Order with Id " + orderId + " not found"
		));
		
		// Only the order owner or admin can view
	    boolean isOwner = order.getUser().getId().equals(user.getId());
	    boolean isAdmin = user.getRole().toString().equalsIgnoreCase("ADMIN");

	    if (!isOwner && !isAdmin) {
	        throw new BadRequestException("Permission denied to view this order");
	    }
		
		return convertToResponseDTO(order);
	}
	
	// Get all orders for a user
	public List<OrderResponseDTO> getOrdersByUser(String userId){
		// Get authenticated user
		User user = userRepo.findByUserId(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		        
		List<Order> orders = orderRepo.findByUserId(user.getId());
		
		return orders.stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	// Cancel order by owner/ admin
	@Override
	public OrderResponseDTO cancelOrder(String userId, String orderId) {
		// Get authenticated user
		User user = userRepo.findByUserId(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		Order order = orderRepo.findByOrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Order with Id " + orderId + " not found"
		));
		
		// Only the order owner or an admin can cancel
	    boolean isOwner = order.getUser().getId().equals(user.getId());
	    boolean isAdmin = user.getRole().toString().equalsIgnoreCase("ADMIN");
		
	    if (!isOwner && !isAdmin) {
	        throw new BadRequestException("Permission denied to cancel this order");
	    }
	    
		// Can only cancel if not shipped
		if(order.getStatus() == Order.OrderStatus.SHIPPED ||
				order.getStatus() == Order.OrderStatus.DELIVERED) {
			throw new BadRequestException("Cannot cancel order that has been shipped or delivered");
		}
		
		if(order.getStatus() == Order.OrderStatus.CANCELLED) {
			 throw new BadRequestException("Order is already cancelled");
		}
		
		// Restore stock for all items
		for(OrderItem orderItem: order.getOrderItems()) {
			Product product = orderItem.getProduct();
			product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
			productRepo.save(product);
		}
		
		// Update order status
		order.setStatus(Order.OrderStatus.CANCELLED);
		Order cancelledOrder = orderRepo.save(order);
		return convertToResponseDTO(cancelledOrder);
	}
	
	// Get all orders (admin)
	@Override
	public List<OrderResponseDTO> getAllOrders(){
		List<Order> orders = orderRepo.findAll();
		return orders.stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	// Get all orders with pagination (admin)
	@Override
	public Page<OrderResponseDTO> getAllOrders(Pageable pageable){
		Page<Order> orderPage = orderRepo.findAll(pageable);
		return orderPage.map(this::convertToResponseDTO);
	}
	
	// Update order status
	@Override
	public OrderResponseDTO updateOrderStatus(String orderId, UpdateOrderStatusRequestDTO statusRequest) {
		Order order = orderRepo.findByOrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Order with Id " + orderId + " not found"
		));
		
		// Validate status transition
		Order.OrderStatus newStatus;
		try {
			newStatus =Order.OrderStatus.valueOf(statusRequest.getStatus().toUpperCase());
		}catch(IllegalArgumentException e) {
			throw new BadRequestException(
					"Invalid status. Valid values : PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED"
			);
		}
		
		// Business rules for status transitions
		if(order.getStatus() == Order.OrderStatus.DELIVERED) {
			throw new BadRequestException("Cannot update status of delivered order");
		}
		
		if(order.getStatus() == Order.OrderStatus.CANCELLED) {
			throw new BadRequestException("Cannot update status of cancelled order");
		}
		
		// Update status
		order.setStatus(newStatus);
		Order updatedOrder = orderRepo.save(order);
		
		return convertToResponseDTO(updatedOrder);
	}
	
	// Helped methods
	private OrderResponseDTO convertToResponseDTO(Order order) {
        // Convert order items to DTOs
		List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
				.map(this::convertOrderItemToDTO)
				.collect(Collectors.toList());
		
		return new OrderResponseDTO(
				order.getOrderId(),
	            order.getUser().getUserId(),
	            order.getUser().getEmail(),
	            order.getUser().getFullName(),
	            itemDTOs,
	            order.getTotalAmount(),
	            order.getOrderItems().size(),
	            order.getStatus().toString(),
	            order.getShippingAddress(),
	            order.getCreatedAt(),
	            order.getUpdatedAt()
		);
	}
	
	private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getOrderItemId(),
                orderItem.getProduct().getProductId(),
                orderItem.getProduct().getProductName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getSubtotal()
            );
        }
}
