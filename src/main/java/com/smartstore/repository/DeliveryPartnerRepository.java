package com.smartstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartstore.entity.DeliveryPartner;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    // ప్రస్తుతానికి ఇందులో ఎలాంటి కస్టమ్ మెథడ్స్ అవసరం లేదు బ్రదర్, JpaRepository బలం సరిపోతుంది!
}