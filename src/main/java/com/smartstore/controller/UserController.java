package com.smartstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smartstore.entity.User;
import com.smartstore.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 🎯 ఈమెయిల్ ఆల్రెడీ ఉందో లేదో చెక్ చేసే API
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email); 
        if (exists) {
            return ResponseEntity.ok("exists"); 
        } else {
            return ResponseEntity.ok("not_exists"); 
        }
    }

    // 1. సైన్-అప్ వివరాలు తీసుకునే API
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String response = userService.registerUser(user);
        return ResponseEntity.ok(response);
    }

    // 2. OTP వెరిఫికేషన్ మరియు డేటాబేస్ సేవింగ్ API
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        String response = userService.verifyOtp(email, otp);
        if ("success".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 3. లాగిన్ API
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        String response = userService.loginUser(email, password);
        return ResponseEntity.ok(response);
    }

    // 👤 4. పూర్తి ప్రొఫైల్ లాగే API
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestParam Long userId) {
        User user = userService.getUserById(userId); 
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔑 5. సెక్యూరిటీ పాస్‌వర్డ్ చేంజ్ చేసే API
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam Long userId, 
            @RequestParam String oldPassword, 
            @RequestParam String newPassword) {
        
        boolean isChanged = userService.changePassword(userId, oldPassword, newPassword);
        if (isChanged) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.ok("wrong_old_password"); 
        }
    }

    // 💳 6. వాలెట్ లో డబ్బులు యాడ్ చేసే API
    @PutMapping("/wallet/add")
    public ResponseEntity<String> addWalletMoney(
            @RequestParam Long userId, 
            @RequestParam Double amount) {
        
        boolean isAdded = userService.addWalletMoney(userId, amount);
        if (isAdded) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.badRequest().body("failed");
        }
    }

    // =========================================================================
    // 🎯 ఫర్గాట్ పాస్వర్డ్ - రియల్ టైమ్ జిమెయిల్ ఓటీపీ ఏపిఐలు
    // =========================================================================

    // 📩 A. డేటాబేస్ లో ఈమెయిల్ చెక్ చేసి, కరెక్ట్ అయితేనే ఓటీపీ పంపే API బ్రదర్
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<String> sendOtpForPasswordReset(@RequestParam String email) {
        String response = userService.sendForgotPasswordOtp(email); 
        return ResponseEntity.ok(response);
    }

    // 🔑 B. వెరిఫికేషన్ సక్సెస్ అయ్యాక డేటాబేస్ లో పాస్వర్డ్ మార్చే API బ్రదర్
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPasswordFromForgot(@RequestParam String email, @RequestParam String newPassword) {
        String response = userService.resetPasswordFromForgot(email, newPassword);
        if ("success".equals(response)) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // =========================================================================
    // 🎯 🌟 యూజర్ ఫ్రంటెండ్ నుండి ఆర్డర్ క్యాన్సిల్ చేసే ప్రొఫెషనల్ API బ్రదర్!
    // =========================================================================
    @PostMapping("/orders/cancel")
    public ResponseEntity<String> cancelUserOrder(@RequestParam Long orderId) {
        String result = userService.cancelOrderAndRestoreStock(orderId);
        if ("success".equals(result)) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // =========================================================================
    // 🚚 🌟 సరికొత్త టాస్క్: డెలివరీ పార్ట్నర్ ఆర్డర్ డెలివరీ చేసినప్పుడు అప్‌డేట్ చేసే API బ్రదర్!
    // =========================================================================
    @PostMapping("/orders/deliver")
    public ResponseEntity<String> markOrderAsDelivered(@RequestParam Long orderId) {
        // 🚀 ఇగో బ్రదర్! ఇక్కడి నుండి డేటా 'UserService' లో మనం రాయబోయే డెలివరీ మెథడ్ కి వెళ్తుంది!
        String result = userService.markOrderAsDeliveredInDB(orderId);
        if ("success".equals(result)) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}