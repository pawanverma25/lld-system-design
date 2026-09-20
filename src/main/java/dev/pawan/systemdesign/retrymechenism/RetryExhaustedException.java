package dev.pawan.systemdesign.retrymechenism;

public class RetryExhaustedException extends RuntimeException {
    private final int attemptsMade;

    public RetryExhaustedException(String message, int attemptsMade, Throwable cause) {
        super(message, cause);
        this.attemptsMade = attemptsMade;
    }

    public int getAttemptsMade() {
        return attemptsMade;
    }
}
