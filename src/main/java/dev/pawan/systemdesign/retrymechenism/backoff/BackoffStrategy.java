package dev.pawan.systemdesign.retrymechenism.backoff;

import java.time.Duration;

@FunctionalInterface
public interface BackoffStrategy {
    Duration computeDelay(int attempt);
}