package com.dailydiscover.controller;

import com.dailydiscover.model.Seller;
import com.dailydiscover.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 商家控制器 - RESTful风格
 */
@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
public class SellerController {
    
    private final SellerService sellerService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) {
        try {
            Seller seller = sellerService.findById(id);
            if (seller != null) {
                return ResponseEntity.ok(seller);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers() {
        try {
            List<Seller> sellers = sellerService.findAll();
            return ResponseEntity.ok(sellers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/verified")
    public ResponseEntity<List<Seller>> getVerifiedSellers() {
        try {
            List<Seller> sellers = sellerService.findVerifiedSellers();
            return ResponseEntity.ok(sellers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/premium")
    public ResponseEntity<List<Seller>> getPremiumSellers() {
        try {
            List<Seller> sellers = sellerService.findPremiumSellers();
            return ResponseEntity.ok(sellers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) {
        try {
            sellerService.save(seller);
            return ResponseEntity.status(HttpStatus.CREATED).body(seller);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable Long id, @RequestBody Seller seller) {
        try {
            seller.setId(id);
            sellerService.update(seller);
            return ResponseEntity.ok(seller);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateSeller(@PathVariable Long id) {
        try {
            sellerService.deactivate(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}