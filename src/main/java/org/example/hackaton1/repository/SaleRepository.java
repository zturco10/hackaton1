package org.example.hackaton1.repository;

import org.example.hackaton1.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("select s from Sale s where (:branch is null or s.branch = :branch) and s.soldAt >= :from and s.soldAt <= :to")
    List<Sale> findByDateRangeAndBranch(@Param("from") Instant from, @Param("to") Instant to, @Param("branch") String branch);

    List<Sale> findByBranchAndSoldAtBetween(String branch, Instant from, Instant to);
}