package dev.pawan.systemdesign.circuitbreaker.config;

public record CircuitBreakerConfig (
        int failedCountThreshold,
        int successCountThreshold,
        long maxOpenDurationInMillis
){
}
