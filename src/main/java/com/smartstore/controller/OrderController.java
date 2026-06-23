package com.smartstore.controller;

import com.smartstore.entity.OrderRequestDTO;
import com.smartstore.service.OrderService;
import java.util.List; // 👈 లిస్ట్ కోసం ఇంపోర్ట్ బ్రదర్
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 1. సైన్-ఇన్ అయిన యూజర్ కార్ట్ నుండి ఆర్డర్ ప్లేస్ చేసే పాత ఏపిఐ బ్రదర్
    @PostMapping("/place")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        boolean success = orderService.placeOrder(orderRequest);
        if (success) {
            return ResponseEntity.ok("🎉 Order placed successfully and stock updated!");
        } else {
            return ResponseEntity.badRequest().body("❌ Failed to place order. Check stock availability!");
        }
    }

    // =========================================================================
    // 🎯 🌟 ఇక్కడి నుండి కొత్తగా యాడ్ చేసిన రియల్-టైమ్ మై ఆర్డర్స్ ఏపిఐ బ్రదర్!
    // =========================================================================

    // 🛍️ 2. లాగిన్ అయిన యూజర్ ఐడీని బట్టి డేటాబేస్ నుండి లైవ్ ఆర్డర్స్ అన్నింటినీ లాగే API
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<?>> getUserOrders(@PathVariable Long userId) {
        // 🎯 🌟 ఇక్కడ List<any> తీసేసి List<?> పెట్టా బ్రదర్, జావా ఎర్రర్ పోతుంది!
        List<?> orders = orderService.getOrdersByUserId(userId); 
        return ResponseEntity.ok(orders);
    }
 // =========================================================================
    // 🚚 🌟 సరికొత్త టాస్క్: డెలివరీ పార్ట్నర్ కోసం డేటాబేస్ లోని మొత్తం ఆర్డర్స్ లాగే API బ్రదర్!
    // =========================================================================
    @GetMapping("/all")
    public ResponseEntity<List<?>> getAllOrders() {
        // 🚀 ఆర్డర్ సర్వీస్ నుండి మొత్తం ఆర్డర్స్ లిస్ట్ ని లాగి యాంగులర్ కి పంపుతాం స్వామి
        List<?> allOrders = orderService.getAllOrdersFromDB(); 
        return ResponseEntity.ok(allOrders);
    }
}