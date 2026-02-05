package com.dailydiscover.controller;

import com.dailydiscover.model.Seller;
import com.dailydiscover.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {
    
    @Autowired
    private SellerService sellerService;
    
    @GetMapping("/{id}")
    public Seller getSellerById(@PathVariable Long id) {
        return sellerService.findById(id);
    }
    
    @GetMapping
    public List<Seller> getAllSellers() {
        return sellerService.findAll();
    }
    
    @GetMapping("/verified")
    public List<Seller> getVerifiedSellers() {
        return sellerService.findVerifiedSellers();
    }
    
    @GetMapping("/premium")
    public List<Seller> getPremiumSellers() {
        return sellerService.findPremiumSellers();
    }
    
    @PostMapping
    public void createSeller(@RequestBody Seller seller) {
        sellerService.save(seller);
    }
    
    @PutMapping("/{id}")
    public void updateSeller(@PathVariable Long id, @RequestBody Seller seller) {
        seller.setId(id);
        sellerService.update(seller);
    }
    
    @DeleteMapping("/{id}")
    public void deactivateSeller(@PathVariable Long id) {
        sellerService.deactivate(id);
    }
}