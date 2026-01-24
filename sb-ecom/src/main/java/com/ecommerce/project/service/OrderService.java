package com.ecommerce.project.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.dto.PlaceOrderRequestDTO;
import com.ecommerce.project.dto.UpdateOrderStatusRequestDTO;

public interface OrderService {
	// Place order from cart (logged-in user)
	OrderResponseDTO placeOrder(String userId, PlaceOrderRequestDTO placeOrderReqdto);
	
	// Get order by orderId
	OrderResponseDTO getOrderById(String userId, String orderId);
	
	// Get all orders for a user
	List<OrderResponseDTO> getOrdersByUser(String userId);
	
	// Cancel order (order owner / admin)
	OrderResponseDTO cancelOrder(String userId, String orderId);
	
	// Get all orders (admin)
	List<OrderResponseDTO> getAllOrders();
	
	// Get all orders with pagination (admin)
	Page<OrderResponseDTO> getAllOrders(Pageable pageable);
	
	// Update order status (admin)
	OrderResponseDTO updateOrderStatus(String orderId, UpdateOrderStatusRequestDTO statusRequest);
}
