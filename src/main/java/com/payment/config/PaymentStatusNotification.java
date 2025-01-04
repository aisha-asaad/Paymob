package com.payment.config;

public class PaymentStatusNotification {
    private String status;
    private String paymentId;
    // بيانات إضافية زي: المبلغ، تاريخ الدفع، إلخ.

    // Getters , setters


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
