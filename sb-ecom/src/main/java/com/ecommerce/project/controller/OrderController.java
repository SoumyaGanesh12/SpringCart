package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.dto.PlaceOrderRequestDTO;
import com.ecommerce.project.dto.UpdateOrderStatusRequestDTO;
import com.ecommerce.project.service.OrderService;

@RestController
@RequestMapping("/api")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    // Place order from cart
    // @PostMapping("/users/{userId}/orders/place") // Remove userId from URL and get user details from JWT token
    @PostMapping("/orders/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        
        OrderResponseDTO order = orderService.placeOrder(placeOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    // Get order by orderId (user)
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
    
    // Get user's order history
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUser() {
        List<OrderResponseDTO> orders = orderService.getOrdersByUser();
        return ResponseEntity.ok(orders);
    }
    
    // Cancel order by orderId (owner/admin)
    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable String orderId) {
        OrderResponseDTO order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }
    
    // Get all orders
    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    // Get all orders with pagination
    @GetMapping("/admin/orders/page")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrdersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderResponseDTO> orderPage = orderService.getAllOrders(pageable);
        
        return ResponseEntity.ok(orderPage);
    }
    
    // Update order status
    @PatchMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequestDTO statusRequest) {
        
        OrderResponseDTO order = orderService.updateOrderStatus(orderId, statusRequest);
        return ResponseEntity.ok(order);
    }
}