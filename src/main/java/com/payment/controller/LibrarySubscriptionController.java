package com.payment.controller;

import com.payment.config.BillingData;
import com.payment.config.PaymentStatusNotification;
import com.payment.dto.SubscriptionRequest;
import com.payment.service.PaymobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class LibrarySubscriptionController {

    private final PaymobService paymobService;

    public LibrarySubscriptionController(PaymobService paymobService) {
        this.paymobService = paymobService;
    }

    @PostMapping("/create-subscription")
    public ResponseEntity<?> createSubscription(@RequestBody SubscriptionRequest request) {
        try {
            String paymentUrl = paymobService.createSubscription(request);
            return ResponseEntity.ok(paymentUrl); // Send the payment URL to the client
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating subscription: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> paymentWebhook(@RequestBody PaymentStatusNotification notification) {
        try {
            paymobService.handlePaymentStatus(notification);
            return ResponseEntity.ok("Received webhook notification");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling webhook: " + e.getMessage());
        }
    }
}

