package org.example.hackaton1.controller;

import jakarta.validation.Valid;
import org.example.hackaton1.config.JwtUtil;
import org.example.hackaton1.dto.SaleRequest;
import org.example.hackaton1.dto.SaleResponse;
import org.example.hackaton1.service.SalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
public class SalesController {
    private final SalesService salesService;
    private final JwtUtil jwtUtil;
    public SalesController(SalesService s, JwtUtil j){this.salesService=s; this.jwtUtil=j;}

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@RequestHeader("Authorization") String auth, @Valid @RequestBody SaleRequest req) {
        var userClaims = jwtUtil.validateAndGetClaims(extractToken(auth));
        String role = (String)userClaims.get("role");
        String username = userClaims.getSubject();
        String branchClaim = (String)userClaims.get("branch");
        // SalesService handles permission check
        SaleResponse saved = salesService.createSale(req, username, role, branchClaim);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSale(...) { /* check permissions */ }

    @GetMapping
    public ResponseEntity<Page<SaleResponse>> listSales(...) { /* paging, filters */ }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> updateSale(...) { /* permission checks */ }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CENTRAL')") // or check in service
    public ResponseEntity<Void> deleteSale(@PathVariable Long id){
        salesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}