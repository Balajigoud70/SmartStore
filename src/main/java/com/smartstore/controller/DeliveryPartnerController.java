package com.smartstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartstore.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "http://localhost:4200") // CORS ఎర్రర్ పోవడానికి ఇది చాలా ముఖ్యం
public class DeliveryPartnerController {

    @Autowired
    private UserService userService;

    // 🤝 1. ఆర్డర్ పూల్ నుండి డెలివరీ బాయ్ ఆర్డర్ యాక్సెప్ట్ చేసినప్పుడు
    @PostMapping("/accept")
    public ResponseEntity<String> acceptOrder(@RequestParam Long orderId, @RequestParam Long partnerId) {
        String result = userService.acceptOrderAsPartner(orderId, partnerId);
        return ResponseEntity.ok(result);
    }

    // 🛵 2. ఈ పర్టికులర్ డెలివరీ బాయ్ చేతిలో ఉన్న లైవ్ ఆర్డర్లను లాగే ఏపిఐ
    @GetMapping("/picked/{partnerId}")
    public ResponseEntity<List<Map<String, Object>>> getPickedOrders(@PathVariable Long partnerId) {
        List<Map<String, Object>> orders = userService.getPartnerPickedOrders(partnerId);
        return ResponseEntity.ok(orders);
    }

    // 👤 3. ప్రొఫైల్ బొమ్మ క్లిక్ చేసినప్పుడు... పేరు, ఈమెయిల్, బ్యాంక్ అకౌంట్ డేటాబేస్ నుండి లాగే ఏపిఐ
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getPartnerProfile(@RequestParam Long partnerId) {
        Map<String, Object> profile = userService.getDeliveryPartnerProfileFromDB(partnerId);
        return ResponseEntity.ok(profile);
    }

    // ✅ 4. ఆర్డర్ సక్సెస్ ఫుల్ గా డెలివరీ చేసి వాలెట్ లో ₹35 వేసే ఏపిఐ
    @PostMapping("/deliver")
    public ResponseEntity<String> markAsDelivered(@RequestParam Long orderId, @RequestParam Long partnerId) {
        String result = userService.markOrderAsDeliveredByPartner(orderId, partnerId);
        return ResponseEntity.ok(result);
    }

    // 💸 5. వాలెట్ లోంచి డబ్బులు బ్యాంక్ అకౌంట్ కి పంపుకునే ఏపిఐ
    @PostMapping("/bank-transfer")
    public ResponseEntity<String> transferToBank(@RequestParam Long partnerId, @RequestParam Double amount) {
        String result = userService.transferWalletMoneyToBank(partnerId, amount);
        return ResponseEntity.ok(result);
    }

    // 🎯 6. కొత్తగా యాడ్ చేసిన ఏపిఐ (ఇది ఆర్డర్ క్యాన్సిల్ చేయడానికి)
    @PostMapping("/cancel-by-partner")
    public ResponseEntity<String> cancelByPartner(@RequestParam Long orderId, @RequestParam Long partnerId) {
        String result = userService.cancelOrderByDeliveryPartner(orderId);
        if ("success".equals(result)) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
 // 📊 7. డెలివరీ మరియు క్యాన్సిల్డ్ హిస్టరీ చూపే ఏపిఐ
    @GetMapping("/history/{partnerId}")
    public ResponseEntity<List<Map<String, Object>>> getPartnerHistory(@PathVariable Long partnerId) {
        return ResponseEntity.ok(userService.getPartnerHistory(partnerId));
    }
}