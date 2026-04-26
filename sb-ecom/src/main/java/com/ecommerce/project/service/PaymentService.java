package com.ecommerce.project.service;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.dto.PaymentResponseDTO;

public interface PaymentService {
	PaymentResponseDTO createPaymentIntent(PaymentRequestDTO request);
	void handlePaymentSuccess(String paymentIntentId);
	void refundPayment(String paymentIntentId);
}
