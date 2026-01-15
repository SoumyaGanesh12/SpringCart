package com.ecommerce.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDTO {
	@NotBlank(message = "Status is required")
	@Pattern(regexp = "PENDING|CONFIRMED|PROCESSING|SHIPPED|DELIVERED|CANCELLED",
			message = "Status must be one of PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED" )
	private String status;
}
