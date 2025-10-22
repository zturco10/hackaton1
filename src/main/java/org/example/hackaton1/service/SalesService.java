package org.example.hackaton1.service;
import org.example.hackaton1.dto.SaleRequest;
import org.example.hackaton1.model.Sale;
import org.example.hackaton1.model.User;
import org.example.hackaton1.repository.SaleRepository;
import org.example.hackaton1.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SalesService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    public SalesService(SaleRepository saleRepository, UserRepository userRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
    }

    public Sale create(SaleRequest req) {
        User me = currentUser();

        // BRANCH solo puede crear en su propia sucursal
        if (isBranch(me) && !me.getBranch().equalsIgnoreCase(req.getBranch())) {
            throw new SecurityException("No puedes crear ventas para otra sucursal");
        }

        Sale s = new Sale();
        s.setSku(req.getSku());
        s.setUnits(req.getUnits());
        s.setPrice(new BigDecimal(String.valueOf(req.getPrice())));
        s.setBranch(req.getBranch());
        s.setSoldAt(req.getSoldAt() != null ? req.getSoldAt() : LocalDateTime.now());
        s.setCreatedBy(me.getUsername());
        return saleRepository.save(s);
    }

    public Optional<Sale> get(Long id) {
        User me = currentUser();
        Optional<Sale> s = saleRepository.findById(id);
        return s.filter(sale -> canAccess(me, sale));
    }

    public Page<Sale> list(LocalDateTime from, LocalDateTime to, String branch, int page, int size) {
        User me = currentUser();

        // Si soy BRANCH, fuerzo mi branch
        String effectiveBranch = isBranch(me) ? me.getBranch() : branch;

        // Implementa en tu repo:
        // Page<Sale> findByFilters(LocalDateTime from, LocalDateTime to, String branch, Pageable pageable);
        return saleRepository.findByFilters(from, to, effectiveBranch, PageRequest.of(page, size));
    }

    public Sale update(Long id, SaleRequest req) {
        User me = currentUser();
        Sale s = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no existe"));

        if (!canAccess(me, s)) throw new SecurityException("Sin permisos");

        // CENTRAL puede cambiar branch, BRANCH no
        if (isBranch(me) && !s.getBranch().equalsIgnoreCase(me.getBranch())) {
            throw new SecurityException("No puedes editar ventas de otra sucursal");
        }

        s.setSku(req.getSku());
        s.setUnits(req.getUnits());
        s.setPrice(new BigDecimal(String.valueOf(req.getPrice())));
        s.setBranch(isBranch(me) ? me.getBranch() : req.getBranch());
        s.setSoldAt(req.getSoldAt());
        return saleRepository.save(s);
    }

    public void delete(Long id) {
        User me = currentUser();
        if (isBranch(me)) throw new SecurityException("Solo CENTRAL puede eliminar");
        saleRepository.deleteById(id);
    }

    private boolean canAccess(User me, Sale s) {
        return !isBranch(me) || me.getBranch().equalsIgnoreCase(s.getBranch());
    }

    private boolean isBranch(User u) {
        return "BRANCH".equalsIgnoreCase(u.getRole());
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }
}
