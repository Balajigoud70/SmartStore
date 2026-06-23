package com.smartstore.service;

import com.smartstore.entity.OrderRequestDTO;
import com.smartstore.entity.OrderItemDTO;
import com.smartstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public boolean placeOrder(OrderRequestDTO orderRequest) {
        try {
            // 1. వాలెట్ బ్యాలెన్స్ చెక్ (కేవలం పేమెంట్ మోడ్ 'wallet' అయితేనే!)
            if ("wallet".equalsIgnoreCase(orderRequest.getPaymentMode())) {
                Double currentWallet = jdbcTemplate.queryForObject("SELECT wallet FROM users WHERE user_id = ?", Double.class, orderRequest.getCustomerId());
                
                if (currentWallet == null || currentWallet < orderRequest.getTotalAmount()) {
                    return false; // బ్యాలెన్స్ లేకపోతే ఇక్కడ ఆగిపోతుంది
                }
                
                // వాలెట్ బ్యాలెన్స్ డిడక్షన్ (ఇది కేవలం వాలెట్ పేమెంట్ కి మాత్రమే)
                jdbcTemplate.update("UPDATE users SET wallet = wallet - ? WHERE user_id = ?", orderRequest.getTotalAmount(), orderRequest.getCustomerId());
            }

            // 2. ఆర్డర్ ఇన్సర్ట్ (ఇది అన్ని పేమెంట్ మోడ్స్ కి వర్తిస్తుంది)
            String orderSql = "INSERT INTO orders (customer_id, total_amount, status, order_date, address_id, payment_type) VALUES (?, ?, 'Booked', CURDATE(), ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, orderRequest.getCustomerId());
                ps.setDouble(2, orderRequest.getTotalAmount());
                ps.setInt(3, orderRequest.getAddressId()); 
                ps.setString(4, orderRequest.getPaymentMode()); 
                return ps;
            }, keyHolder);

            int orderId = keyHolder.getKey().intValue();

            // 3. ఐటమ్స్ ఇన్సర్ట్ & స్టాక్ డిక్రీజ్
            String itemsSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            for (OrderItemDTO item : orderRequest.getItems()) {
                jdbcTemplate.update(itemsSql, orderId, item.getProductId(), item.getQuantity(), item.getPrice());
                productRepository.decreaseStock(item.getProductId(), item.getQuantity());
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Transactional
    public boolean updateOrderStatus(Long orderId, String status) {
        // స్టేటస్ అప్‌డేట్ & సేవ్
        int updated = jdbcTemplate.update("UPDATE orders SET status = ? WHERE order_id = ?", status, orderId);
        return updated > 0;
    }

    public List<Map<String, Object>> getOrdersByUserId(Long userId) {
        String orderQuery = "SELECT o.order_id AS orderId, o.order_date AS date, o.status, o.delivery_date AS deliveryDate, o.total_amount AS totalAmount, o.payment_type AS paymentType, " +
                             "CONCAT(a.house_no, ', ', a.street_or_village, ', ', a.city_or_town, ', ', a.pin_code) AS shippingAddress " +
                             "FROM orders o LEFT JOIN user_addresses a ON o.address_id = a.id " + 
                             "WHERE o.customer_id = ? ORDER BY o.order_id DESC";
                             
        List<Map<String, Object>> orders = jdbcTemplate.queryForList(orderQuery, userId);
        for (Map<String, Object> order : orders) {
            Object orderIdObj = order.get("orderId");
            if (orderIdObj != null) {
                String itemsQuery = "SELECT p.name AS name, oi.quantity AS qty, oi.price AS price FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
                List<Map<String, Object>> products = jdbcTemplate.queryForList(itemsQuery, orderIdObj);
                order.put("products", products); 
            }
        }
        return orders;
    }

    public List<Map<String, Object>> getAllOrdersFromDB() {
        // 🎯 ఇక్కడ 'state' కాలమ్ ని కన్కాట్ చేశాను బ్రదర్
        String sql = "SELECT o.order_id AS orderId, o.order_date AS date, o.status, " +
                     "o.total_amount AS totalAmount, o.payment_type AS paymentType, " +
                     "CONCAT_WS(', ', ua.house_no, ua.street_or_village, ua.city_or_town, ua.state, ua.pin_code) AS fullAddress " +
                     "FROM orders o " +
                     "LEFT JOIN user_addresses ua ON o.address_id = ua.id " +
                     "WHERE o.status = 'Booked' OR o.status = 'booked'"; 
        
        return jdbcTemplate.queryForList(sql);
    }
    }
