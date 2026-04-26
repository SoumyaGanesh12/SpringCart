package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.dto.PaymentResponseDTO;
import com.ecommerce.project.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/create-intent")
	public ResponseEntity<PaymentResponseDTO> createPaymentIntent(
			@Valid @RequestBody PaymentRequestDTO req){
		PaymentResponseDTO res = paymentService.createPaymentIntent(req);
		return ResponseEntity.ok(res);
	}
}
