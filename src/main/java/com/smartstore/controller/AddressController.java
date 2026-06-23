package com.smartstore.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.smartstore.entity.UserAddress;
import com.smartstore.repository.UserAddressRepository;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private UserAddressRepository addressRepository;

    // 🏠 🏢 యూజర్ యొక్క అన్ని అడ్రస్‌లను తెచ్చే API
    @GetMapping("/user/{userId}")
    public List<UserAddress> getAddressesByUserId(@PathVariable Long userId) {
        return addressRepository.findByUserId(userId);
    }

    // ➕ కొత్త అడ్రస్ యాడ్ చేసే API
    @PostMapping("/add")
    public UserAddress addAddress(@RequestBody UserAddress address) {
        return addressRepository.save(address);
    }

    // 🗑️ వద్దు అనుకున్న అడrarు డిలీట్ చేసే API బ్రదర్
    @DeleteMapping("/delete/{id}")
    public String deleteAddress(@PathVariable Long id) {
        addressRepository.deleteById(id);
        return "success";
    }
}