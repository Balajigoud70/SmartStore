package com.smartstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartstore.entity.LoginAuth;

@Repository
public interface LoginAuthRepository extends JpaRepository<LoginAuth, Long> {
    
    // 👈 ఈమెయిల్ ఆల్రెడీ రిజిస్టర్ అయి ఉందో లేదో చెక్ చేయడానికి బ్రదర్
    boolean existsByEmail(String email); 

    // 🎯 లాగిన్ అప్పుడు మరియు ఓటీపీ వెరిఫికేషన్ అప్పుడు ఈమెయిల్ ద్వారా రికార్డ్‌ను వెతకడానికి స్వామి
    Optional<LoginAuth> findByEmail(String email); 
}