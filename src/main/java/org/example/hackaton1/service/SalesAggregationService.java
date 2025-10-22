package org.example.hackaton1.service;

import org.example.hackaton1.model.Sale;
import org.example.hackaton1.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesAggregationService {

    private final SaleRepository saleRepository;

    public SalesAggregationService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public SalesAggregates calculateAggregates(LocalDateTime from, LocalDateTime to, String branch) {
        // Implementa en repo: List<Sale> findByRangeAndBranch(LocalDateTime from, LocalDateTime to, String branch);
        List<Sale> sales = saleRepository.findByRangeAndBranch(from, to, branch);

        int totalUnits = sales.stream().mapToInt(Sale::getUnits).sum();
        BigDecimal totalRevenue = sales.stream()
                .map(s -> s.getPrice().multiply(BigDecimal.valueOf(s.getUnits())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String topSku = topKey(sales.stream()
                .collect(Collectors.groupingBy(Sale::getSku, Collectors.summingInt(Sale::getUnits))));

        String topBranch = topKey(sales.stream()
                .collect(Collectors.groupingBy(Sale::getBranch, Collectors.summingInt(Sale::getUnits))));

        return new SalesAggregates(totalUnits, totalRevenue, topSku, topBranch);
    }

    private String topKey(Map<String, Integer> m) {
        return m.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    public record SalesAggregates(int totalUnits, BigDecimal totalRevenue, String topSku, String topBranch) {}
}
