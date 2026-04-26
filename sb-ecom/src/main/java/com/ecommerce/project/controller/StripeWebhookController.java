package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {
	@Value("${stripe.webhook.secret}")
	private String webhookSecret;
	
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/stripe")
	public ResponseEntity<String> handleStripeWebhook(
			@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader
		){
	    
	    try {
	        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
	        
	        if("payment_intent.succeeded".equals(event.getType())) {
	           
	            String rawJson = event.getDataObjectDeserializer().getRawJson();
	            
	            // Extract id directly from raw JSON string
	            String paymentIntentId = rawJson.split("\"id\":\"")[1].split("\"")[0];
	            
	            if(paymentIntentId != null) {
	                paymentService.handlePaymentSuccess(paymentIntentId);
	            }
	        }
	    
	        return ResponseEntity.ok("Webhook handled");
	    } catch(Exception e) {
	        return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
	    }
	}
}
