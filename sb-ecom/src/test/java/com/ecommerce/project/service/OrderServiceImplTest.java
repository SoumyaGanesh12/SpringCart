package com.ecommerce.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.dto.PlaceOrderRequestDTO;
import com.ecommerce.project.dto.UpdateOrderStatusRequestDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    
    @Mock
    private OrderRepository orderRepo;
    
    @Mock
    private CartRepository cartRepo;
    
    @Mock
    private ProductRepository prodRepo;
    
    @Mock
    private UserRepository userRepo;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    private User user;
    private User adminUser;
    private Cart cart;
    private Product product;
    private Category category;
    private CartItem cartItem;
    private Order order;
    private PlaceOrderRequestDTO placeOrderRequest;
    
    @BeforeEach
    public void setUp() {
        // Create test user (customer)
        user = new User();
        user.setId(1L);
        user.setUserId("U0001");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.CUSTOMER);
        user.setActive(true);
        
        // Create test admin user
        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUserId("U0002");
        adminUser.setEmail("admin@springcart.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setActive(true);
        
        // Create test category
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        
        // Create test product
        product = new Product();
        product.setProductId(1L);
        product.setProductName("MacBook Pro");
        product.setPrice(new BigDecimal("2499.99"));
        product.setStockQuantity(50);
        product.setActive(true);
        product.setCategory(category);
        
        // Create test cart with items
        cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(user);
        cart.setTotalAmount(new BigDecimal("4999.98"));
        
        cartItem = new CartItem();
        cartItem.setCartItemId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(new BigDecimal("2499.99"));
        
        cart.setCartItems(new ArrayList<>(Arrays.asList(cartItem)));
        
        // Create test order
        order = new Order();
        order.setId(1L);
        order.setOrderId("ORD-test-uuid");
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("4999.98"));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress("123 Main St, NY");
        order.setOrderItems(new ArrayList<>());
        
        // Create place order request
        placeOrderRequest = new PlaceOrderRequestDTO();
        placeOrderRequest.setShippingAddress("123 Main St, New York, NY");
    }
    
    @Test
    public void placeOrder_ShouldSuccess() {
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(prodRepo.save(any(Product.class))).thenReturn(product);
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);
        
        OrderResponseDTO result = orderService.placeOrder("U0001", placeOrderRequest);
        
        assertNotNull(result);
        assertEquals("ORD-test-uuid", result.getOrderId());
        
        verify(userRepo, times(1)).findByUserId("U0001");
        verify(cartRepo, times(1)).findByUserId(1L);
        verify(orderRepo, times(1)).save(any(Order.class));
        verify(prodRepo, times(1)).save(any(Product.class));  // Stock deduction
        verify(cartRepo, times(1)).save(any(Cart.class));  // Cart cleared
    }
    
    @Test
    public void placeOrder_ShouldFail_WhenUserNotFound() {
        when(userRepo.findByUserId("U9999")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.placeOrder("U9999", placeOrderRequest));
        
        verify(orderRepo, never()).save(any(Order.class));
    }
    
    @Test
    public void placeOrder_ShouldFail_WhenCartEmpty() {
        cart.setCartItems(new ArrayList<>());  // Empty cart
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.placeOrder("U0001", placeOrderRequest)
        );
        
        assertTrue(exception.getMessage().contains("Cart is empty"));
        verify(orderRepo, never()).save(any(Order.class));
    }
    
    @Test
    public void placeOrder_ShouldFail_WhenInsufficientStock() {
        product.setStockQuantity(1);  // Only 1 in stock
        cartItem.setQuantity(5);  // Cart has 5
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(cartRepo.findByUserId(1L)).thenReturn(Optional.of(cart));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.placeOrder("U0001", placeOrderRequest)
        );
        
        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(orderRepo, never()).save(any(Order.class));
    }
    
    @Test
    public void getOrderById_ShouldSuccess_WhenOwner() {
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        OrderResponseDTO result = orderService.getOrderById("U0001", "ORD-test-uuid");
        
        assertNotNull(result);
        assertEquals("ORD-test-uuid", result.getOrderId());
        
        verify(orderRepo, times(1)).findByOrderId("ORD-test-uuid");
    }
    
    @Test
    public void getOrderById_ShouldSuccess_WhenAdmin() {
        when(userRepo.findByUserId("U0002")).thenReturn(Optional.of(adminUser));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        OrderResponseDTO result = orderService.getOrderById("U0002", "ORD-test-uuid");
        
        assertNotNull(result);
        assertEquals("ORD-test-uuid", result.getOrderId());
    }
    
    @Test
    public void getOrderById_ShouldFail_WhenNotOwnerOrAdmin() {        
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUserId("U0003");
        anotherUser.setRole(User.Role.CUSTOMER);
        
        when(userRepo.findByUserId("U0003")).thenReturn(Optional.of(anotherUser));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.getOrderById("U0003", "ORD-test-uuid")
        );
        
        assertTrue(exception.getMessage().contains("Permission denied"));
    }
    
    @Test
    public void getOrdersByUser_ShouldReturnOrders() {
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(orderRepo.findByUserId(1L)).thenReturn(Arrays.asList(order));
        
        List<OrderResponseDTO> result = orderService.getOrdersByUser("U0001");
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-test-uuid", result.get(0).getOrderId());
        
        verify(orderRepo, times(1)).findByUserId(1L);
    }
    
    @Test
    public void getAllOrders_ShouldReturnAllOrders() {
        when(orderRepo.findAll()).thenReturn(Arrays.asList(order));
        
        List<OrderResponseDTO> result = orderService.getAllOrders();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(orderRepo, times(1)).findAll();
    }
    
    @Test
    public void updateOrderStatus_ShouldSuccess() {
        UpdateOrderStatusRequestDTO statusRequest = new UpdateOrderStatusRequestDTO();
        statusRequest.setStatus("CONFIRMED");
        
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        
        OrderResponseDTO result = orderService.updateOrderStatus("ORD-test-uuid", statusRequest);
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
        
        verify(orderRepo, times(1)).save(any(Order.class));
    }
    
    @Test
    public void updateOrderStatus_ShouldFail_WithInvalidStatus() {
        UpdateOrderStatusRequestDTO statusRequest = new UpdateOrderStatusRequestDTO();
        statusRequest.setStatus("INVALID_STATUS");
        
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.updateOrderStatus("ORD-test-uuid", statusRequest)
        );
        
        assertTrue(exception.getMessage().contains("Invalid status"));
    }
    
    @Test
    public void updateOrderStatus_ShouldFail_WhenOrderDelivered() {
        order.setStatus(Order.OrderStatus.DELIVERED);
        UpdateOrderStatusRequestDTO statusRequest = new UpdateOrderStatusRequestDTO();
        statusRequest.setStatus("CANCELLED");
        
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
       
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.updateOrderStatus("ORD-test-uuid", statusRequest)
        );
        
        assertTrue(exception.getMessage().contains("Cannot update status of delivered order"));
    }
    
    @Test
    public void cancelOrder_ShouldSuccess() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("2499.99"));
        order.setOrderItems(new ArrayList<>(Arrays.asList(orderItem)));
        
        product.setStockQuantity(48);  // Stock after order
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        when(prodRepo.save(any(Product.class))).thenReturn(product);
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        
        OrderResponseDTO result = orderService.cancelOrder("U0001", "ORD-test-uuid");
        
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
        assertEquals(50, product.getStockQuantity());  // Stock restored (48 + 2 = 50)
        
        verify(prodRepo, times(1)).save(any(Product.class));  // Stock restoration
        verify(orderRepo, times(1)).save(any(Order.class));
    }
    
    @Test
    public void cancelOrder_ShouldFail_WhenNotOwner() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUserId("U0003");
        otherUser.setRole(User.Role.CUSTOMER);
        
        when(userRepo.findByUserId("U0003")).thenReturn(Optional.of(otherUser));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.cancelOrder("U0003", "ORD-test-uuid")
        );
        
        assertTrue(exception.getMessage().contains("Permission denied"));
    }
    
    @Test
    public void cancelOrder_ShouldFail_WhenOrderShipped() {
        order.setStatus(Order.OrderStatus.SHIPPED);
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.cancelOrder("U0001", "ORD-test-uuid")
        );
        
        assertTrue(exception.getMessage().contains("shipped or delivered"));
        verify(orderRepo, never()).save(any(Order.class));
    }
    
    @Test
    public void cancelOrder_ShouldFail_WhenAlreadyCancelled() {
        order.setStatus(Order.OrderStatus.CANCELLED);
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user));
        when(orderRepo.findByOrderId("ORD-test-uuid")).thenReturn(Optional.of(order));
        
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> orderService.cancelOrder("U0001", "ORD-test-uuid")
        );
        
        assertTrue(exception.getMessage().contains("already cancelled"));
    }
}