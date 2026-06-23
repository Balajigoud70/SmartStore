package com.smartstore.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty; // 👈 🎯 ఈ ఇంపోర్ట్ చాలా ముఖ్యం బ్రదర్!

public class OrderRequestDTO {
    
    @JsonProperty("customerId")
    private int customerId;
    
    @JsonProperty("totalAmount")
    private double totalAmount;
    
    @JsonProperty("items")
    private List<OrderItemDTO> items;

    @JsonProperty("addressId")
    private int addressId;
    
    // 🎯 🌟 ఈ అనోటేషన్ వల్ల యాంగులర్ లోని 'paymentMode' వాల్యూని స్ప్రింగ్ బూట్ పక్కాగా క్యాచ్ చేస్తుంది బ్రదర్!
    @JsonProperty("paymentMode") 
    private String paymentMode; 

    // Getters and Setters
    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}