package com.smartstore.service;

import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional; 
import java.util.Random;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.jdbc.core.JdbcTemplate; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import com.smartstore.entity.User;
import com.smartstore.entity.LoginAuth;
import com.smartstore.entity.DeliveryPartner;
import com.smartstore.repository.UserRepository;
import com.smartstore.repository.LoginAuthRepository;
import com.smartstore.repository.DeliveryPartnerRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAuthRepository loginAuthRepository; 

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository; 

    @Autowired
    private JdbcTemplate jdbcTemplate; 

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String appPassword;

    private Map<String, String> otpStorage = new HashMap<>();
    private Map<String, Map<String, Object>> temporaryUserStorage = new HashMap<>(); 

    public boolean checkEmailExistsInAuth(String email) {
        return loginAuthRepository.existsByEmail(email);
    }

    public String registerUserOrPartnerInDB(Map<String, Object> registrationData) {
        String email = (String) registrationData.get("email");

        if (loginAuthRepository.existsByEmail(email)) {
            return "ఈమెయిల్ ఆల్రెడీ రిజిస్టర్ అయి ఉంది బ్రదర్!";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp); 
        temporaryUserStorage.put(email, registrationData); 

        System.out.println("Generated OTP for " + email + " : " + otp);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("SmartStore - OTP Verification");
            message.setText("నమస్తే బ్రదర్,\n\nSmartStore లో మీ రిజిస్ట్రేషన్ పూర్తి చేయడానికి మీ 6 అంకెల ఓటీపీ ఇగోండి: " + otp + "\n\nధన్యవాదాలు,\nSmartStore టీమ్.");

            Transport.send(message);
            return "మీ ఈమెయిల్ ఐడి కి OTP పంపాము బ్రదర్! సరిచూసుకోండి.";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail: మెయిల్ పంపడంలో సమస్య వచ్చింది బ్రదర్: " + e.getMessage();
        }
    }

    @Transactional
    public String verifyOtpAndSaveToTables(String email, String otp) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            Map<String, Object> data = temporaryUserStorage.get(email);
            if (data != null) {
                LoginAuth loginAuth = new LoginAuth();
                loginAuth.setEmail(email);
                loginAuth.setPassword((String) data.get("password"));
                loginAuth.setRole(((String) data.get("role")).toUpperCase());
                loginAuth.setVerified(true);
                loginAuth = loginAuthRepository.save(loginAuth);

                String role = (String) data.get("role");
                if ("DELIVERY".equalsIgnoreCase(role)) {
                    DeliveryPartner partner = new DeliveryPartner();
                    partner.setLoginAuth(loginAuth);
                    partner.setFullName((String) data.get("name"));
                    partner.setPhoneNumber((String) data.get("mobileNumber"));
                    partner.setAccountNumber((String) data.get("accountNumber"));
                    partner.setIfscCode((String) data.get("ifscCode"));
                    partner.setBankName((String) data.get("bankName"));
                    partner.setAadhaarNumber("[Aadhaar Redacted]");
                    partner.setPanNumber((String) data.get("panNumber"));
                    partner.setDrivingLicenseNumber((String) data.get("drivingLicenseNumber"));
                    partner.setWalletBalance(0.0);
                    
                    deliveryPartnerRepository.save(partner);
                } else {
                    User user = new User();
                    user.setLoginAuth(loginAuth);
                    user.setName((String) data.get("name"));
                    user.setMobileNumber((String) data.get("mobileNumber"));
                    user.setGender((String) data.get("gender"));
                    user.setWalletBalance(0.0);
                    if (data.get("age") != null) {
                        user.setAge(Integer.parseInt(data.get("age").toString()));
                    }
                    userRepository.save(user);
                }

                otpStorage.remove(email);
                temporaryUserStorage.remove(email);
                return "success";
            }
        }
        return "fail: తప్పుడు ఓటీపీ కొట్టారు బ్రదర్!";
    }

    public boolean checkEmailExists(String email) {
        return loginAuthRepository.existsByEmail(email);
    }

    public String registerUser(User user) {
        return "Moved to new auth route";
    }
    public String verifyOtp(String email, String otp) {
        return verifyOtpAndSaveToTables(email, otp);
    }

    public String loginUser(String email, String password) {
        Optional<LoginAuth> authOpt = loginAuthRepository.findByEmail(email);
        
        if (authOpt.isPresent()) {
            LoginAuth auth = authOpt.get();
            if (auth.getPassword().equals(password)) {
                return "SUCCESS:" + auth.getId() + ":" + auth.getRole().toUpperCase();
            }
        }
        return "తప్పుడు ఈమెయిల్ లేదా పాస్వర్డ్ బ్రదర్!";
    }

    public String sendForgotPasswordOtp(String email) {
        Optional<LoginAuth> authOpt = loginAuthRepository.findByEmail(email);
        if (!authOpt.isPresent()) {
            return "EMAIL_NOT_EXISTS"; 
        }

        String forgotOtp = String.format("%04d", new Random().nextInt(9999));
        otpStorage.put(email, forgotOtp); 

        System.out.println("🔥 Forgot Password OTP Generated for " + email + " : " + forgotOtp);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("SmartStore - Password Reset Verification Code");
            message.setText("హలో బ్రదర్,\n\nమీ స్మార్ట్ స్టోర్ అకౌంట్ పాస్వర్డ్ రీసెట్ చేయడానికి ఓటీపీ కోడ్ ఇగో: " + forgotOtp + "\n\nదయచేసి ఈ కోడ్ ఎవరికీ చెప్పకండి.");

            Transport.send(message);
            return forgotOtp; 
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR_SENDING_MAIL";
        }
    }

    public String resetPasswordFromForgot(String email, String newPassword) {
        Optional<LoginAuth> authOpt = loginAuthRepository.findByEmail(email);
        if (authOpt.isPresent()) {
            LoginAuth auth = authOpt.get();
            auth.setPassword(newPassword);
            loginAuthRepository.save(auth); 
            return "success";
        }
        return "fail";
    }

    @Transactional
    public String cancelOrderAndRestoreStock(Long orderId) {
        try {
            // ఆర్డర్ వివరాలు తీసుకోవడం
            Map<String, Object> orderData = jdbcTemplate.queryForMap("SELECT customer_id, total_amount, status FROM orders WHERE order_id = ?", orderId);
            String status = (String) orderData.get("status");
            
            if ("delivered".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
                return "fail: ఈ ఆర్డర్ ని ఇప్పుడు క్యాన్సిల్ చేయలేము బ్రదర్!";
            }
            
            // 1. ఆర్డర్ స్టేటస్ అప్‌డేట్ (ఇది పర్మినెంట్ గా డేటాబేస్ లో సేవ్ అవుతుంది)
            jdbcTemplate.update("UPDATE orders SET status = 'cancelled', cancel_date = NOW() WHERE order_id = ?", orderId);
            
            // 2. స్టాక్ రీస్టోర్
            jdbcTemplate.update("UPDATE products SET stock_quantity = stock_quantity + (SELECT quantity FROM order_items WHERE order_id = ?) WHERE id = (SELECT product_id FROM order_items WHERE order_id = ? LIMIT 1)", orderId, orderId);
            
            // 3. వాలెట్ రీఫండ్ (ఇది చాలా ముఖ్యం)
            Double refundAmount = ((Number) orderData.get("total_amount")).doubleValue();
            Long customerId = ((Number) orderData.get("customer_id")).longValue();
            jdbcTemplate.update("UPDATE users SET wallet = wallet + ? WHERE user_id = ?", refundAmount, customerId);
            
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
    
    @Transactional
    public String cancelOrderByDeliveryPartner(Long orderId) {
        try {
            // 1. ముందుగా కస్టమర్ ID మరియు అమౌంట్ తీసుకో (అప్‌డేట్ చేయడానికి ముందే)
            Map<String, Object> orderData = jdbcTemplate.queryForMap("SELECT customer_id, total_amount FROM orders WHERE order_id = ?", orderId);
            
            // 2. ఆర్డర్ స్టేటస్ అప్‌డేట్
            jdbcTemplate.update("UPDATE orders SET status = 'cancelled', cancel_date = NOW() WHERE order_id = ?", orderId);
            
            // 3. స్టాక్ రీస్టోర్ (ప్రతి ఐటమ్ కి)
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT product_id, quantity FROM order_items WHERE order_id = ?", orderId);
            for (Map<String, Object> item : items) {
                Long pid = ((Number) item.get("product_id")).longValue();
                int qty = ((Number) item.get("quantity")).intValue();
                jdbcTemplate.update("UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ?", qty, pid);
            }
            
            // 4. వాలెట్ రీఫండ్
            Double refundAmount = ((Number) orderData.get("total_amount")).doubleValue();
            Long customerId = ((Number) orderData.get("customer_id")).longValue();
            jdbcTemplate.update("UPDATE users SET wallet = wallet + ? WHERE user_id = ?", refundAmount, customerId);
            
            return "success";
        } catch (Exception e) {
            e.printStackTrace(); 
            return "fail: " + e.getMessage();
        }
    }
    
    public User getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null); 
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<LoginAuth> authOpt = loginAuthRepository.findById(userId);
        if (authOpt.isPresent()) {
            LoginAuth auth = authOpt.get();
            if (auth.getPassword().equals(oldPassword)) {
                auth.setPassword(newPassword);
                loginAuthRepository.save(auth); 
                return true;
            }
        }
        return false;
    }

    public boolean addWalletMoney(Long userId, Double amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && amount > 0) {
            User user = userOpt.get();
            double currentBalance = user.getWalletBalance() != null ? user.getWalletBalance() : 0.0;
            user.setWalletBalance(currentBalance + amount);
            userRepository.save(user); 
            return true;
        }
        return false;
    }

    public String markOrderAsDeliveredInDB(Long orderId) {
        try {
            String statusSql = "SELECT status FROM orders WHERE order_id = ?";
            String currentStatus = jdbcTemplate.queryForObject(statusSql, String.class, orderId);
            
            if ("cancelled".equalsIgnoreCase(currentStatus)) {
                return "fail: ఈ ఆర్డర్ ఆల్రెడీ క్యాన్సిల్ అయిపోయింది బ్రదర్, డెలివరీ చేయలేము!";
            }
            
            String updateSql = "UPDATE orders SET status = 'delivered', delivery_date = NOW() WHERE order_id = ?";
            int rowsAffected = jdbcTemplate.update(updateSql, orderId);
            
            if (rowsAffected > 0) {
                return "success";
            } else {
                return "fail: డేటాబేస్ లో ఆర్డర్ ఐడీ దొరకలేదు బ్రదర్!";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "error: డెలివరీ స్టేటస్ మార్చడంలో లోపం జరిగింది బ్రదర్: " + e.getMessage();
        }
    }

    @Transactional
    public String acceptOrderAsPartner(Long orderId, Long partnerId) {
        try {
            String updateSql = "UPDATE orders SET delivery_partner_id = ?, status = 'picked' WHERE order_id = ?";
            jdbcTemplate.update(updateSql, partnerId, orderId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail: ఆర్డర్ యాక్సెప్ట్ చేయడంలో లోపం బ్రదర్: " + e.getMessage();
        }
    }

    public List<Map<String, Object>> getPartnerPickedOrders(Long partnerId) {
        List<Map<String, Object>> responseList = new ArrayList<>();
        try {
            // 🎯 ఇక్కడ CONCAT_WS లో 'ua.state' ని యాడ్ చేశాను బ్రదర్
            String sql = "SELECT o.order_id AS orderId, o.total_amount AS totalAmount, o.payment_type AS paymentType, " +
                         "u.name AS customerName, u.mobile_number AS customerPhone, " +
                         "CONCAT_WS(', ', ua.house_no, ua.street_or_village, ua.city_or_town, ua.state, ua.pin_code) AS fullAddress " +
                         "FROM orders o " +
                         "JOIN users u ON o.customer_id = u.user_id " +
                         "LEFT JOIN user_addresses ua ON o.address_id = ua.id " +
                         "WHERE o.delivery_partner_id = ? AND o.status = 'picked'";

            List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql, partnerId);

            for (Map<String, Object> order : orders) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("orderId", order.get("orderId"));
                orderMap.put("totalAmount", order.get("totalAmount"));
                orderMap.put("paymentType", order.get("paymentType"));
                orderMap.put("customerName", order.get("customerName"));
                orderMap.put("customerPhone", order.get("customerPhone"));
                orderMap.put("fullAddress", order.get("fullAddress")); // ఇక్కడ అడ్రస్ వస్తుంది

                // ప్రొడక్ట్స్ లిస్ట్ అలాగే ఉంచుదాం (ఎందుకంటే మోడల్ లో చూపిస్తున్నావు కదా)
                Long orderId = ((Number) order.get("orderId")).longValue();
                String itemsSql = "SELECT p.name, oi.quantity AS qty FROM order_items oi " +
                                  "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
                List<Map<String, Object>> items = jdbcTemplate.queryForList(itemsSql, orderId);
                orderMap.put("products", items);

                responseList.add(orderMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseList;
    }
    public Map<String, Object> getDeliveryPartnerProfileFromDB(Long partnerId) {
        try {
            String sql = "SELECT dp.full_name AS name, dp.phone_number AS mobileNumber, dp.wallet_balance AS walletBalance, dp.account_number AS bankAccountNumber, la.email AS email " +
                         "FROM delivery_partners dp " +
                         "JOIN login_auth la ON dp.user_id = la.id " +
                         "WHERE dp.user_id = ?";
            return jdbcTemplate.queryForMap(sql, partnerId);
        } catch (Exception e) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("name", "Balaji Pitta");
            fallback.put("email", "partner.balu@smartstore.com");
            fallback.put("mobileNumber", "6301130463");
            fallback.put("walletBalance", 0.0);
            fallback.put("bankAccountNumber", "SBI - XXXXXX5678");
            return fallback;
        }
    }

    @Transactional
    public String markOrderAsDeliveredByPartner(Long orderId, Long partnerId) {
        try {
            // స్టేటస్ అప్‌డేట్
            int updated = jdbcTemplate.update("UPDATE orders SET status = 'delivered', delivery_date = NOW() WHERE order_id = ?", orderId);
            
            if(updated > 0) {
                // వాలెట్ యాడ్
                jdbcTemplate.update("UPDATE delivery_partners SET wallet_balance = wallet_balance + 35.00 WHERE user_id = ?", partnerId);
                return "success";
            }
            return "fail: ఆర్డర్ ఐడీ దొరకలేదు!";
        } catch (Exception e) { 
            return "fail: " + e.getMessage(); 
        }
    }
    @Transactional
    public String transferWalletMoneyToBank(Long partnerId, Double amount) {
        try {
            String checkSql = "SELECT wallet_balance FROM delivery_partners WHERE user_id = ?";
            Double currentBalance = jdbcTemplate.queryForObject(checkSql, Double.class, partnerId);

            if (currentBalance == null || currentBalance < amount) {
                return "fail: వాలెట్ లో సరిపడా అమౌంట్ లేదు బ్రదర్!";
            }

            String deductSql = "UPDATE delivery_partners SET wallet_balance = wallet_balance - ? WHERE user_id = ?";
            jdbcTemplate.update(deductSql, amount, partnerId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail: " + e.getMessage();
        }
    }
    public List<Map<String, Object>> getPartnerHistory(Long partnerId) {
        // 🎯 ఫస్ట్ పద్ధతి - క్లీన్ గా, పర్ఫార్మెన్స్ బాగుండేలా
        String sql = "SELECT o.order_id AS orderId, o.status, o.total_amount, o.delivery_date, o.cancel_date " +
                     "FROM orders o " +
                     "WHERE o.delivery_partner_id = ? " +
                     "AND o.status IN ('delivered', 'cancelled') " +
                     "ORDER BY o.order_id DESC";
                     
        return jdbcTemplate.queryForList(sql, partnerId);
    }
}