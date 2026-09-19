package dev.pawan.systemdesign.circuitbreaker;

@FunctionalInterface
public interface CircuitBreakerOperation<T> {
    public T execute();
}
