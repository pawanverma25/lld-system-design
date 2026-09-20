package dev.pawan.systemdesign.retrymechenism;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface RetryOperations {
    <T> T execute(Callable<T> operation);
}