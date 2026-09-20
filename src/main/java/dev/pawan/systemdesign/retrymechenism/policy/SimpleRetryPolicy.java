package dev.pawan.systemdesign.retrymechenism.policy;

import java.util.Set;

public class SimpleRetryPolicy implements RetryPolicy {

    private final int maxAttempts;
    private final Set<Class<? extends Throwable>> retryableExceptions;

    public SimpleRetryPolicy(int maxAttempts, Set<Class<? extends Throwable>> retryableExceptions) {
        if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts must be >= 1");
        this.maxAttempts = maxAttempts;
        this.retryableExceptions = Set.copyOf(retryableExceptions);
    }

    @Override
    public boolean canRetry(int attempt, Throwable throwable) {
        if (attempt >= maxAttempts || throwable instanceof InterruptedException) {
            return false;
        }
        return isRetryable(throwable);
    }

    private boolean isRetryable(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            for (Class<? extends Throwable> retryableType : retryableExceptions) {
                if (retryableType.isAssignableFrom(current.getClass())) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }
}