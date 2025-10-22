package org.example.hackaton1.event;

import java.time.LocalDate;

public record ReportRequestedEvent(
        String requestId,
        LocalDate from,
        LocalDate to,
        String branch,
        String emailTo
) {}