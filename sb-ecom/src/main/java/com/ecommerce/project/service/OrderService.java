package com.ecommerce.project.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.dto.PlaceOrderRequestDTO;
import com.ecommerce.project.dto.UpdateOrderStatusRequestDTO;

public interface OrderService {
	// Place order from cart
	OrderResponseDTO placeOrder(String userId, PlaceOrderRequestDTO placeOrderReqdto);
	
	// Get order by orderId
	OrderResponseDTO getOrderById(String orderId);
	
	// Get all orders for a user
	List<OrderResponseDTO> getOrdersByUser(String userId);
	
	// Get all orders
	List<OrderResponseDTO> getAllOrders();
	
	// Get all orders with pagination
	Page<OrderResponseDTO> getAllOrders(Pageable pageable);
	
	// Update order status
	OrderResponseDTO updateOrderStatus(String orderId, UpdateOrderStatusRequestDTO statusRequest);
	
	// Cancel order
	OrderResponseDTO cancelOrder(String orderId);
}
