package com.smartstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import com.smartstore.service.UserService;

@RestController
@RequestMapping("/api/auth") // 🎯 యాంగులర్ లో మనం ఇచ్చిన కొత్త మెయిన్ యుఆర్‌ఎల్ బ్రదర్
public class AuthController {

    @Autowired
    private UserService userService;

    // 🎯 యాంగులర్ రిజిస్టర్ పేజీలో ఈమెయిల్ ఆల్రెడీ ఉందో లేదో చెక్ చేసే కొత్త API
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.checkEmailExistsInAuth(email); 
        if (exists) {
            return ResponseEntity.ok("exists"); 
        } else {
            return ResponseEntity.ok("not_exists"); 
        }
    }

    // 🚚 👤 యాంగులర్ రిజిస్ట్రేషన్ ఫామ్ నుండి వచ్చే కస్టమర్ లేదా డెలివరీ మిక్స్డ్ డేటాను మోసుకొచ్చే API స్వామి
    @PostMapping("/register")
    public ResponseEntity<String> registerUserOrPartner(@org.springframework.web.bind.annotation.RequestBody Map<String, Object> registrationData) {
        String response = userService.registerUserOrPartnerInDB(registrationData);
        return ResponseEntity.ok(response);
    }

    // 🔑 OTP వెరిఫికేషన్ కరెక్ట్ అయితేనే టేబుల్స్ లోకి డేటా పంపే API బ్రదర్
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        String response = userService.verifyOtpAndSaveToTables(email, otp);
        if ("success".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}