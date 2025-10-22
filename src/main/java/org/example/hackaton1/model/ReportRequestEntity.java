package org.example.hackaton1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="report_requests")
public class ReportRequestEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requestId;
    private String status;
    private Instant requestedAt = Instant.now();
    private String requestedBy;
    // + fields like fromDate, toDate, branch, emailTo
}