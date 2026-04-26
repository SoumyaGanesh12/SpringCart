package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponseDTO {
	private String paymentIntentId;
	private String clientSecret;
	private String status;
	private Long amount;
	private String currency;
}
