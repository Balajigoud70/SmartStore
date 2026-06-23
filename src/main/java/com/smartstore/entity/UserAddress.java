package com.smartstore.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId; 
    
    @Column(name = "address_type")
    private String addressType; 

    // 🎯 కొత్త 5 అడ్రస్ కాలమ్స్ బ్రదర్!
    @Column(name = "house_no")
    private String houseNo;

    @Column(name = "street_or_village")
    private String streetOrVillage;

    @Column(name = "city_or_town")
    private String cityOrTown; 

    @Column(name = "pin_code")
    private String pinCode;

    private String state;

    // Default Constructor
    public UserAddress() {}

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    public String getHouseNo() { return houseNo; }
    public void setHouseNo(String houseNo) { this.houseNo = houseNo; }

    public String getStreetOrVillage() { return streetOrVillage; }
    public void setStreetOrVillage(String streetOrVillage) { this.streetOrVillage = streetOrVillage; }

    public String getCityOrTown() { return cityOrTown; }
    public void setCityOrTown(String cityOrTown) { this.cityOrTown = cityOrTown; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; } // 🎯 ఇక్కడ ఇందాక మిస్టేక్ పడింది, ఇప్పుడు సెట్ అయింది బ్రదర్!

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}