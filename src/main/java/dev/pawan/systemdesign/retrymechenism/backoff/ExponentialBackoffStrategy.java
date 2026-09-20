package dev.pawan.systemdesign.retrymechenism.backoff;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class ExponentialBackoffStrategy implements BackoffStrategy {

    private final Duration baseDelay;
    private final Duration maxDelay;
    private final double multiplier;
    private final boolean enableJitter;

    public ExponentialBackoffStrategy(Duration baseDelay, Duration maxDelay, double multiplier, boolean enableJitter) {
        if (baseDelay.isNegative() || baseDelay.isZero()) throw new IllegalArgumentException("baseDelay must be > 0");
        if (maxDelay.compareTo(baseDelay) < 0) throw new IllegalArgumentException("maxDelay must be >= baseDelay");
        if (multiplier < 1.0) throw new IllegalArgumentException("multiplier must be >= 1.0");

        this.baseDelay = baseDelay;
        this.maxDelay = maxDelay;
        this.multiplier = multiplier;
        this.enableJitter = enableJitter;
    }

    @Override
    public Duration computeDelay(int attempt) {
        double factor = Math.pow(multiplier, attempt - 1);
        long rawMillis = (long) (baseDelay.toMillis() * factor);
        long cappedMillis = Math.min(rawMillis, maxDelay.toMillis());

        if (!enableJitter) {
            return Duration.ofMillis(cappedMillis);
        }

        long randomizedMillis = ThreadLocalRandom.current().nextLong(cappedMillis + 1);
        return Duration.ofMillis(randomizedMillis);
    }
}