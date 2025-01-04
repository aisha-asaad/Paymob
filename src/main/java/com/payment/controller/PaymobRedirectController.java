package com.payment.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/paymob")
public class PaymobRedirectController {

    private final String iframeId = "YourId";
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectToPayment(@RequestParam String paymentToken) {
        // تكوين رابط الدفع
        String paymentUrl = "https://accept.paymobsolutions.com/api/acceptance/iframes/" + iframeId + "?payment_token=" + paymentToken;

        // إعادة توجيه المستخدم
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(paymentUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }}
