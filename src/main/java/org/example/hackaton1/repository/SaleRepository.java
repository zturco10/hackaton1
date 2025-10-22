package org.example.hackaton1.repository;

import org.example.hackaton1.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SaleRepository<Sale> extends JpaRepository<Sale, String> {
    List<Sale> findByDateRangeAndBranch(String branch, LocalDate from, LocalDate to);
}