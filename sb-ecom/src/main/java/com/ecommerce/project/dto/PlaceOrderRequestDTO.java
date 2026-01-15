package com.ecommerce.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequestDTO {
	@NotBlank(message = "Shipping Address is required")
	@Size(min = 10, max = 200, message = "Shipping Address must be between 10 and 200 characters")
	private String shippingAddress;
}
