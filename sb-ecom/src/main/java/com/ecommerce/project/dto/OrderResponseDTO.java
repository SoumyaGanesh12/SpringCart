package com.ecommerce.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
	private String orderId;
	private String userId;
	private String userEmail;
	private String userName;
	private List<OrderItemDTO> items;
	private BigDecimal totalAmount;
	private Integer itemCount;
	private String status;
	private String shippingAddress;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
