package com.example.nagoyameshi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.nagoyameshi.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@Controller
public class StripeWebhookController {
	private final StripeService stripeService;
	
	@Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
	
	public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }
	
	@PostMapping("/stripe/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
		Stripe.apiKey = stripeApiKey;
        Event event = null;
        
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        switch (event.getType()) {
	        case "checkout.session.completed":
	        	stripeService.processSessionCompleted(event);
	        	break;
	        case "invoice.paid":
	        	
	        	break;
	        case "invoice.payment_failed":
	        	
	        	break;
	        case "customer.subscription.deleted":
	        	
	        	break;
	        default:
	      }
        return new ResponseEntity<>("Success", HttpStatus.OK);
	}
}
 