package com.smartstore.repository;

import com.smartstore.entity.Product; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🎯 🌟 నేరుగా MySQL టేబుల్ కాలమ్స్ (id, stock_quantity) ని టార్గెట్ చేసే నేటివ్ క్వెరీ బ్రదర్!
    @Modifying
    @Transactional
    @Query(value = "UPDATE products SET stock_quantity = stock_quantity - :qty WHERE id = :prodId AND stock_quantity >= :qty", nativeQuery = true)
    int decreaseStock(@Param("prodId") int prodId, @Param("qty") int qty);
}