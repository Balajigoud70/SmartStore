package com.smartstore.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column; // 👈 🎯 ఈ ఒక్క ఇంపోర్ట్ మిస్ అయింది బ్రదర్!

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String imageUrl; // ప్రొడక్ట్ ఫొటో చూపించడానికి యుఆర్ఎల్ బ్రదర్
    private int stockQuantity; // 👈 ఇదే అసలైన స్టాక్ కౌంట్ బ్రదర్ (ఇది 0 అయితే నాట్ అవైలబుల్ అని చూపిస్తాం)
    private String category; // Electronics, Clothing, etc.
 // 🎯 🌟 నీ Product క్లాస్ లోపల ఈ కొత్త వేరియబుల్ యాడ్ చెయ్ బ్రదర్!
    @Column(name = "sub_category") // ఒకవేళ నువ్వు JPA/Hibernate వాడితే ఇది రాయ్ బ్రదర్, లేదా నార్మల్ గా అయినా ఓకే
    private String subCategory;

    // 🎯 🌟 గెట్టర్ అండ్ సెట్టర్స్ ఇగో బ్రదర్!
    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}