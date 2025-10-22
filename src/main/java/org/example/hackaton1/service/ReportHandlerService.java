package org.example.hackaton1.service;

import org.example.hackaton1.model.ReportRequestEntity;
import org.example.hackaton1.repository.ReportRequestRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReportHandlerService {

    private final SalesAggregationService aggregationService;
    private final GithubModelsClient modelsClient;
    private final EmailService emailService;
    private final ReportRequestRepository reportRepo;

    public ReportHandlerService(SalesAggregationService aggregationService,
                                GithubModelsClient modelsClient,
                                EmailService emailService,
                                ReportRequestRepository reportRepo) {
        this.aggregationService = aggregationService;
        this.modelsClient = modelsClient;
        this.emailService = emailService;
        this.reportRepo = reportRepo;
    }

    @Async
    @EventListener
    public void handleReportRequest(ReportRequestedEvent ev) {
        ReportRequestEntity rr = reportRepo.findById(ev.requestId()).orElse(null);
        if (rr == null) return;

        try {
            rr.setStatus("PROCESSING");
            rr.setUpdatedAt(LocalDateTime.now());
            reportRepo.save(rr);

            var ag = aggregationService.calculateAggregates(
                    ev.from().atStartOfDay(),
                    ev.to().plusDays(1).atStartOfDay().minusSeconds(1),
                    ev.branch()
            );

            String summary = modelsClient.generateSummary(
                    ag.totalUnits(), ag.totalRevenue(), ag.topSku(), ag.topBranch()
            );

            String html = """
                    <html><body>
                    <h2>🍪 Reporte Semanal Oreo</h2>
                    <p>%s</p>
                    <ul>
                      <li><b>Unidades:</b> %d</li>
                      <li><b>Recaudación:</b> %s</li>
                      <li><b>SKU top:</b> %s</li>
                      <li><b>Sucursal top:</b> %s</li>
                    </ul>
                    </body></html>
                    """.formatted(summary, ag.totalUnits(), ag.totalRevenue(), ag.topSku(), ag.topBranch());

            emailService.sendHtml(ev.emailTo(),
                    "Reporte Semanal Oreo - " + ev.from() + " a " + ev.to(),
                    html);

            rr.setStatus("SENT");
            rr.setUpdatedAt(LocalDateTime.now());
            reportRepo.save(rr);
        } catch (Exception e) {
            rr.setStatus("FAILED");
            rr.setUpdatedAt(LocalDateTime.now());
            reportRepo.save(rr);
            // En un controller devolverías 503 si falla el servicio externo;
            // aquí solo dejamos el rastro de fallo.
        }
    }

    /** Utilidad para crear y persistir el request */
    public String createRequest(LocalDate from, LocalDate to, String branch, String emailTo) {
        String reqId = "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        ReportRequestEntity rr = new ReportRequestEntity();
        rr.setId(reqId);
        rr.setFrom(from);
        rr.setTo(to);
        rr.setBranch(branch);
        rr.setEmailTo(emailTo);
        rr.setStatus("RECEIVED");
        rr.setCreatedAt(LocalDateTime.now());
        rr.setUpdatedAt(LocalDateTime.now());
        reportRepo.save(rr);
        return reqId;
    }

    /** Evento que publican los controllers */
    public record ReportRequestedEvent(String requestId, LocalDate from, LocalDate to, String branch, String emailTo) {}
}
