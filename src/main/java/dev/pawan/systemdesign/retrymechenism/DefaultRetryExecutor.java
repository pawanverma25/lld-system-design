package dev.pawan.systemdesign.retrymechenism;

import dev.pawan.systemdesign.retrymechenism.backoff.BackoffStrategy;
import dev.pawan.systemdesign.retrymechenism.policy.RetryPolicy;
import dev.pawan.systemdesign.retrymechenism.sleeper.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;

public class DefaultRetryExecutor implements RetryOperations {

    private static final Logger log = LoggerFactory.getLogger(DefaultRetryExecutor.class);

    private final RetryPolicy retryPolicy;
    private final BackoffStrategy backoffStrategy;
    private final Sleeper sleeper;

    public DefaultRetryExecutor(RetryPolicy retryPolicy, BackoffStrategy backoffStrategy) {
        this(retryPolicy, backoffStrategy, Sleeper.DEFAULT);
    }

    public DefaultRetryExecutor(RetryPolicy retryPolicy, BackoffStrategy backoffStrategy, Sleeper sleeper) {
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
        this.backoffStrategy = Objects.requireNonNull(backoffStrategy, "backoffStrategy must not be null");
        this.sleeper = Objects.requireNonNull(sleeper, "sleeper must not be null");
    }

    @Override
    public <T> T execute(Callable<T> operation) {
        int attempt = 0;
        Throwable lastThrowable;

        while (true) {
            attempt++;
            try {
                return operation.call();
            } catch (Throwable ex) {
                lastThrowable = ex;

                if (!retryPolicy.canRetry(attempt, ex)) {
                    log.warn("Retry policy rejected attempt {} for exception: {}", attempt, ex.getMessage());
                    throw unwrapOrWrapException(attempt, lastThrowable);
                }

                Duration delay = backoffStrategy.computeDelay(attempt);
                log.info("Attempt {} failed. Retrying in {} ms...", attempt, delay.toMillis());

                try {
                    sleeper.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Execution was interrupted during retry backoff", ie);
                }
            }
        }
    }

    private RuntimeException unwrapOrWrapException(int attempts, Throwable cause) {
        return new RetryExhaustedException("Operation failed after " + attempts + " attempts", attempts, cause);
    }
}