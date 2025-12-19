package com.shoutx.dtos;

public class PaymentDTO {
    private String paymentMethod; // UPI, CARD, PAYPAL
    private String planId;
    private Integer amount;

    public PaymentDTO() {}
    
    public PaymentDTO(String paymentMethod, String planId, Integer amount) {
        this.paymentMethod = paymentMethod;
        this.planId = planId;
        this.amount = amount;
    }

    // Getters and Setters
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}
