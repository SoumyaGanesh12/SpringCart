package com.ecommerce.project.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.dto.PaymentResponseDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.repository.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{
	@Autowired
	private OrderRepository orderRepo;
	
	@Override
	public PaymentResponseDTO createPaymentIntent(PaymentRequestDTO request) {
		// Find the order
		Order order = orderRepo.findByOrderId(request.getOrderId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Order not found with Id: "+ request.getOrderId()));
		
		// Check if order is in valid state for payment
		if(order.getStatus() != Order.OrderStatus.PENDING) {
			throw new BadRequestException("Order is not in PENDING state");
		}
		
		// Convert amount to cents (Stripe uses smallest currency unit)
		Long amountInCents = order.getTotalAmount()
				.multiply(BigDecimal.valueOf(100))
				.longValue();
		
		try {
			// Create payment intent
			PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
					.setAmount(amountInCents)
					.setCurrency("usd")
					.setDescription("Payment for Order: " + order.getOrderId())
					.putMetadata("orderId", order.getOrderId())
					.setAutomaticPaymentMethods(
							PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
								.setEnabled(true)
								.setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
								.build()
							)
					.build();
			
			PaymentIntent paymentIntent = PaymentIntent.create(params);
			
			// Save payment intent ID to order
			order.setPaymentIntentId(paymentIntent.getId());
			orderRepo.save(order);
			
			return new PaymentResponseDTO(
					paymentIntent.getId(),
					paymentIntent.getClientSecret(),
					paymentIntent.getStatus(),
					amountInCents,
					"usd"
			);
		} catch(StripeException e) {
			throw new BadRequestException("Payment failed: " + e.getMessage());
		}
	}
	
    @Override
    public void handlePaymentSuccess(String paymentIntentId) {        
        Optional<Order> orderOpt = orderRepo.findByPaymentIntentId(paymentIntentId);
      
        Order order = orderOpt.orElseThrow(() -> new ResourceNotFoundException(
                "Order not found for payment: " + paymentIntentId));
        
        order.setStatus(Order.OrderStatus.CONFIRMED);
        orderRepo.save(order);
    }
	
	@Override
	public void refundPayment(String paymentIntentId) {
		try {
			RefundCreateParams params = RefundCreateParams.builder()
					.setPaymentIntent(paymentIntentId)
					.build();
			
			Refund.create(params);
			
		}catch (StripeException ex) {
			throw new BadRequestException("Refund failed: " + ex.getMessage());
		}
	}
}
