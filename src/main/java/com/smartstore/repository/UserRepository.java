package com.smartstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartstore.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { 
    // 🎯 ఇక్కడి నుండి ఈమెయిల్ మెథడ్స్ తీసేసాం బ్రదర్, ఎందుకంటే ఇవి కొత్త లాగిన్ రిపోజిటరీ లోకి వెళ్తాయి స్వామి!
}