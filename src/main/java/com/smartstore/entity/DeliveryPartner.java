package com.smartstore.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.MapsId;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner {

    @Id
    @Column(name = "user_id") // 🎯 లాగిన్ టేబుల్ ఐడీనే దీనికి ప్రైమరీ కీ బ్రదర్
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "wallet_balance")
    private Double walletBalance = 0.0;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "aadhaar_number")
    private String aadhaarNumber;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "driving_license_number")
    private String drivingLicenseNumber;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Column(name = "license_image_path")
    private String licenseImagePath;

    // 🔗 🌟 లాగిన్ టేబుల్ తో వన్-టు-వన్ కనెక్షన్ స్వామి
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private LoginAuth loginAuth;

    // --- GETTERS AND SETTERS (నీ పాత స్టైల్ మాన్యువల్ మెథడ్స్ బ్రదర్) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }

    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public String getLicenseImagePath() { return licenseImagePath; }
    public void setLicenseImagePath(String licenseImagePath) { this.licenseImagePath = licenseImagePath; }

    public LoginAuth getLoginAuth() { return loginAuth; }
    public void setLoginAuth(LoginAuth loginAuth) { this.loginAuth = loginAuth; }
}