package dev.pawan.systemdesign.retrymechenism.policy;

public interface RetryPolicy {
    boolean canRetry(int attempt, Throwable throwable);
}