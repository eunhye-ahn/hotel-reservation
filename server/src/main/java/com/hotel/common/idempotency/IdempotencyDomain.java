package com.hotel.common.idempotency;

import java.time.Duration;

public enum IdempotencyDomain {
    RESERVATION(Duration.ofMinutes(1),Duration.ofMinutes(10),Duration.ofMinutes(1)),
    PAYMENT(Duration.ofMinutes(1),Duration.ofMinutes(30),Duration.ofMinutes(1)),
    SETTLEMENT(Duration.ofMinutes(1),Duration.ofHours(1),Duration.ofMinutes(1));

    private final Duration processingTtl;
    private final Duration completedTtl;
    private final Duration failedTtl;

    IdempotencyDomain(Duration processingTtl, Duration completedTtl, Duration failedTtl) {
        this.processingTtl = processingTtl;
        this.completedTtl = completedTtl;
        this.failedTtl = failedTtl;
    }

    public Duration getTtlByStatus(String status) {
        return switch(status){
            case "processing" -> processingTtl;
            case "completed" -> completedTtl;
            case "failed" -> failedTtl;
            //exception 변경
            default -> null;
        };
    }
}
