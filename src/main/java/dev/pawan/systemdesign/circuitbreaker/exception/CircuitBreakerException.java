package dev.pawan.systemdesign.circuitbreaker.exception;

public class CircuitBreakerException extends RuntimeException{
    public CircuitBreakerException(String message){
        super(message);
    }
    public CircuitBreakerException(String message, Throwable e){
        super(message, e);
    }
}
