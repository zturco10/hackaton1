package org.example.hackaton1.repository;

import org.example.hackaton1.model.ReportRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRequestRepository extends JpaRepository<ReportRequestEntity, String> {
}