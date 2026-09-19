package dev.pawan.systemdesign.circuitbreaker;

import dev.pawan.systemdesign.circuitbreaker.config.CircuitBreakerConfig;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeCircuitBreaker {

    // 1. Immutable State Container (Snapshot)
    private static final class BreakerSnapshot {
        final CircuitBreakerState state;
        final int failureCount;
        final int halfOpenSuccessCount;
        final long lastStateChangeTime;

        BreakerSnapshot(CircuitBreakerState state, int failureCount, int halfOpenSuccessCount, long lastStateChangeTime) {
            this.state = state;
            this.failureCount = failureCount;
            this.halfOpenSuccessCount = halfOpenSuccessCount;
            this.lastStateChangeTime = lastStateChangeTime;
        }

        // Factory helpers
        static BreakerSnapshot initial() {
            return new BreakerSnapshot(CircuitBreakerState.CLOSED, 0, 0, System.currentTimeMillis());
        }

        BreakerSnapshot toOpen(long now) {
            return new BreakerSnapshot(CircuitBreakerState.OPEN, 0, 0, now);
        }

        BreakerSnapshot toHalfOpen(long now) {
            return new BreakerSnapshot(CircuitBreakerState.HALF_OPEN, 0, 0, now);
        }

        BreakerSnapshot toClosed(long now) {
            return new BreakerSnapshot(CircuitBreakerState.CLOSED, 0, 0, now);
        }
    }

    private final String name;
    private final CircuitBreakerConfig config;
    private final AtomicReference<BreakerSnapshot> snapshot;

    public LockFreeCircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
        this.snapshot = new AtomicReference<>(BreakerSnapshot.initial());
    }

    public <T> T execute(CircuitBreakerOperation<T> operation, CircuitBreakerOperation<T> fallback) {
        if (!allowRequest()) {
            if (fallback != null) return fallback.execute();
            throw new RuntimeException("Circuit breaker [" + name + "] is OPEN.");
        }

        try {
            T result = operation.execute();
            onSuccess();
            return result;
        } catch (Exception ex) {
            onError(ex);
            if (fallback != null) return fallback.execute();
            throw ex;
        }
    }

    // 2. Lock-free allowRequest using CAS
    public boolean allowRequest() {
        while (true) {
            BreakerSnapshot current = snapshot.get();
            long now = System.currentTimeMillis();

            if (current.state == CircuitBreakerState.CLOSED) {
                return true;
            }

            if (current.state == CircuitBreakerState.OPEN) {
                if (now - current.lastStateChangeTime >= config.maxOpenDurationInMillis()) {
                    // Try to atomically step into HALF_OPEN
                    BreakerSnapshot next = current.toHalfOpen(now);
                    if (snapshot.compareAndSet(current, next)) {
                        return true; // Exactly one winning thread transitions and acts as the probe
                    }
                    // If CAS failed, another thread already transitioned or updated the state; retry loop
                    continue;
                }
                return false;
            }

            // In HALF_OPEN: allow trial requests
            return current.state == CircuitBreakerState.HALF_OPEN;
        }
    }

    // 3. Lock-free onSuccess using CAS
    public void onSuccess() {
        while (true) {
            BreakerSnapshot current = snapshot.get();
            long now = System.currentTimeMillis();

            if (current.state == CircuitBreakerState.HALF_OPEN) {
                int newSuccessCount = current.halfOpenSuccessCount + 1;
                BreakerSnapshot next;
                if (newSuccessCount >= config.successCountThreshold()) {
                    next = current.toClosed(now);
                } else {
                    next = new BreakerSnapshot(CircuitBreakerState.HALF_OPEN, 0, newSuccessCount, current.lastStateChangeTime);
                }

                if (snapshot.compareAndSet(current, next)) {
                    return;
                }
            } else if (current.state == CircuitBreakerState.CLOSED && current.failureCount > 0) {
                // Reset failure counter back to 0 on success
                BreakerSnapshot next = new BreakerSnapshot(CircuitBreakerState.CLOSED, 0, 0, current.lastStateChangeTime);
                if (snapshot.compareAndSet(current, next)) {
                    return;
                }
            } else {
                return; // Nothing to change
            }
        }
    }

    // 4. Lock-free onError using CAS
    public void onError(Exception ex) {
        while (true) {
            BreakerSnapshot current = snapshot.get();
            long now = System.currentTimeMillis();

            if (current.state == CircuitBreakerState.HALF_OPEN) {
                // Any single error in HALF_OPEN immediately trips back to OPEN
                BreakerSnapshot next = current.toOpen(now);
                if (snapshot.compareAndSet(current, next)) {
                    return;
                }
                // If CAS failed because another thread already flipped to OPEN, retry will see OPEN and return
            } else if (current.state == CircuitBreakerState.CLOSED) {
                int newFailures = current.failureCount + 1;
                BreakerSnapshot next;
                if (newFailures >= config.failedCountThreshold()) {
                    next = current.toOpen(now);
                } else {
                    next = new BreakerSnapshot(CircuitBreakerState.CLOSED, newFailures, 0, current.lastStateChangeTime);
                }

                if (snapshot.compareAndSet(current, next)) {
                    return;
                }
            } else {
                // State is already OPEN
                return;
            }
        }
    }

    public CircuitBreakerState getState() {
        return snapshot.get().state;
    }
}