package org.example.hackaton1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report_requests")
public class ReportRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String branch;

    private LocalDateTime requestDate;

    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}