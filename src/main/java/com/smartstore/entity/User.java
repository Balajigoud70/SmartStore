package com.smartstore.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.MapsId;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id") // 🎯 నీ పాత కోడ్ ఎర్రర్ రాకుండా 'user_id' గానే ఉంచాం బ్రదర్
    private Long id;

    private String name;
    private String mobileNumber;
    private Integer age;
    private String gender;

    @Column(name = "wallet") // 🎯 డేటాబేస్ వాలెట్ కాలమ్ మ్యాపింగ్ బ్రదర్
    private Double walletBalance = 0.0;

    // 🔗 🌟 కొత్త లాగిన్ టేబుల్ తో వన్-టు-వన్ లింక్ బ్రదర్
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private LoginAuth loginAuth;

    // --- GETTERS AND SETTERS (మాన్యువల్ జావా మెథడ్స్) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }

    // 🔗 లాగిన్ రిలేషన్ యొక్క గెట్టర్ అండ్ సెట్టర్ స్వామి
    public LoginAuth getLoginAuth() { return loginAuth; }
    public void setLoginAuth(LoginAuth loginAuth) { this.loginAuth = loginAuth; }
    
 // User.java లోపల, మిగతా గెట్టర్స్ కింద ఇది యాడ్ చెయ్:

    public String getEmail() {
        if (this.loginAuth != null) {
            return this.loginAuth.getEmail();
        }
        return null;
    }
    
}