package org.example.hackaton1.controller;

import org.example.hackaton1.dto.SummaryRequest;
import org.example.hackaton1.dto.SummaryResponse;
import org.example.hackaton1.service.ReportHandlerService;
import org.example.hackaton1.service.ReportHandlerService.ReportRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/sales/summary")
public class SummaryController {

    private final ApplicationEventPublisher publisher;
    private final ReportHandlerService reportHandler;

    public SummaryController(ApplicationEventPublisher publisher, ReportHandlerService reportHandler) {
        this.publisher = publisher;
        this.reportHandler = reportHandler;
    }

    @PostMapping("/weekly")
    public ResponseEntity<SummaryResponse> weekly(@RequestBody SummaryRequest req) {
        LocalDate now = LocalDate.now();
        LocalDate from = req.getFrom() != null ? req.getFrom() : now.minusDays(7);
        LocalDate to = req.getTo() != null ? req.getTo() : now.minusDays(1);

        if (req.getEmailTo() == null || req.getEmailTo().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String requestId = reportHandler.createRequest(from, to, req.getBranch(), req.getEmailTo());

        publisher.publishEvent(new ReportRequestedEvent(
                requestId, from, to, req.getBranch(), req.getEmailTo()
        ));

        SummaryResponse resp = new SummaryResponse();
        resp.setRequestId(requestId);
        resp.setStatus("PROCESSING");
        resp.setMessage("Su solicitud de reporte está siendo procesada. Recibirá el resumen en " + req.getEmailTo() + " en unos momentos.");
        resp.setEstimatedTime("30-60 segundos");
        resp.setRequestedAt(java.time.Instant.now());

        return ResponseEntity.accepted().body(resp);
    }
}