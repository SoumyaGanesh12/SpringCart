package com.ecommerce.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.project.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	// Find order by custom orderId
	Optional<Order> findByOrderId(String orderId);
	
	// Find all orders by user
	List<Order> findByUserId(Long userId);
	
	// Find all orders by user with pagination
	Page<Order> findByUserId(Long userId, Pageable pageable);
	
	// Find orders by user's custom userId
	List<Order> findByUserUserId(String userId);
	
	// Find orders by status
	List<Order> findByStatus(Order.OrderStatus status);
	
	// Find all orders with pagination
	Page<Order> findAll(Pageable pageable);
}
