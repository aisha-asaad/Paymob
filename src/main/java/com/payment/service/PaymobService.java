package com.payment.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.payment.config.BillingData;
import com.payment.config.PaymentStatusNotification;
import com.payment.dto.SubscriptionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import com.google.gson.JsonObject;

@Service
public class PaymobService {

    private static final String BASE_URL = "https://accept.paymobsolutions.com/api";
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String INTEGRATION_ID = "YOUR_INTEGRATION_ID";

    private final RestTemplate restTemplate;

    public PaymobService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAuthToken() {
        try {
            String url = BASE_URL + "/auth/tokens";
            JsonObject request = new JsonObject();
            request.addProperty("api_key", API_KEY);

            ResponseEntity<String> response = restTemplate.postForEntity(url, getRequestEntity(request), String.class);
            return parseResponse(response, "token");
        } catch (Exception e) {
            logError(e, "Error getting auth token");
            return "Error getting auth token: " + e.getMessage();
        }
    }

    public int createOrder(String authToken, int amountCents) {
        if (amountCents < 10) {
            throw new IllegalArgumentException("Amount should be greater than or equal to 10 cents");
        }
        String url = BASE_URL + "/ecommerce/orders";

        JsonObject request = new JsonObject();
        request.addProperty("auth_token", authToken);
        request.addProperty("delivery_needed", false);
        request.addProperty("amount_cents", amountCents);
        request.add("items", new JsonArray());

        ResponseEntity<String> response = restTemplate.postForEntity(url, getRequestEntity(request), String.class);
        return Integer.parseInt(parseResponse(response, "id"));
    }

    public String generatePaymentKey(String authToken, int orderId, int amountCents, BillingData billingData) {
        String url = BASE_URL + "/acceptance/payment_keys";

        JsonObject billingDataJson = new JsonObject();
        billingDataJson.addProperty("first_name", billingData.getName().split(" ")[0]);
        billingDataJson.addProperty("last_name", billingData.getName().contains(" ") ? billingData.getName().split(" ")[1] : "");
        billingDataJson.addProperty("email", billingData.getEmail());
        billingDataJson.addProperty("phone_number", billingData.getPhone());
        billingDataJson.addProperty("city", "Cairo");
        billingDataJson.addProperty("country", "EG");
        billingDataJson.addProperty("street", "Library Street");
        billingDataJson.addProperty("building", billingData.getBuilding());
        billingDataJson.addProperty("floor", billingData.getFloor());
        billingDataJson.addProperty("apartment", billingData.getApartment());

        JsonObject request = new JsonObject();
        request.addProperty("auth_token", authToken);
        request.addProperty("amount_cents", amountCents);
        request.addProperty("order_id", orderId);
        request.add("billing_data", billingDataJson);
        request.addProperty("currency", "EGP");
        request.addProperty("integration_id", INTEGRATION_ID);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, getRequestEntity(request), String.class);
            return parseResponse(response, "token");
        } catch (Exception e) {
            logError(e, "Error generating payment key");
            throw new RuntimeException("Error generating payment key: " + e.getMessage());
        }
    }

    private HttpEntity<String> getRequestEntity(JsonObject jsonObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private String parseResponse(ResponseEntity<String> response, String key) {
        JsonObject responseBody = new Gson().fromJson(response.getBody(), JsonObject.class);
        return responseBody.get(key).getAsString();
    }

    private void logError(Exception e, String message) {
        // هنا ممكن تستخدم لوجينج زي log4j أو SLF4J بدل من printStackTrace
        System.err.println(message);
        e.printStackTrace();
    }

    public String createSubscription(SubscriptionRequest request) throws Exception {
        try {
            String authToken = getAuthToken();
            int orderId = createOrder(authToken, request.getAmountCents());
            BillingData billingData = request.getBillingData();
            String paymentKey = generatePaymentKey(authToken, orderId, request.getAmountCents(), billingData);
            return "https://paymob.com/pay?token=" + paymentKey;
        } catch (Exception e) {
            logError(e, "Error creating subscription");
            throw new RuntimeException("Error creating subscription: " + e.getMessage());
        }
    }

    public void handlePaymentStatus(PaymentStatusNotification notification) {
        String paymentStatus = notification.getStatus();

        if ("SUCCESS".equals(paymentStatus)) {
            System.out.println("Payment Successful");
            // لو الدفع ناجح، هنحدث حالة الاشتراك في قاعدة البيانات
        } else {
            System.out.println("Payment Failed");
        }
    }
}
