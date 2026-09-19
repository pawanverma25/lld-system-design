package dev.pawan.systemdesign.circuitbreaker;

import dev.pawan.systemdesign.circuitbreaker.config.CircuitBreakerConfig;
import dev.pawan.systemdesign.circuitbreaker.exception.CircuitBreakerException;


public class SyncCircuitBreaker {
    private final CircuitBreakerConfig config;

    private CircuitBreakerState state;
    private long lastStateChangedTimeMillis;
    private int successCount;
    private int failureCount;

    public SyncCircuitBreaker(CircuitBreakerConfig config) {
        this.config = config;

        state = CircuitBreakerState.CLOSED;
        lastStateChangedTimeMillis = System.currentTimeMillis();
        successCount = 0;
        failureCount = 0;
    }

    public synchronized <T> T execute(CircuitBreakerOperation<T> action, CircuitBreakerOperation<T> fallback) throws CircuitBreakerException{
        long now = System.currentTimeMillis();
        if(state == CircuitBreakerState.OPEN){
            if(now - lastStateChangedTimeMillis >= config.maxOpenDurationInMillis()){
                state = CircuitBreakerState.HALF_OPEN;
                lastStateChangedTimeMillis = now;
            } else {
                return fallback.execute();
            }
        }

        try{
            T result = action.execute();
            onSucess();
            return result;
        } catch (Exception e){
            onFailure();
            return fallback.execute();
        }
    }

    private void onFailure() {
        if(state == CircuitBreakerState.HALF_OPEN){
            successCount++;
            if(successCount >= config.successCountThreshold()) {
                state = CircuitBreakerState.CLOSED;
                successCount = 0;
                failureCount = 0;
                lastStateChangedTimeMillis = System.currentTimeMillis();
            }
        }
    }

    private void onSucess() {
        if(state == CircuitBreakerState.HALF_OPEN){
            failureCount++;
            if(failureCount >= config.failedCountThreshold()) {
                state = CircuitBreakerState.OPEN;
                successCount = 0;
                failureCount = 0;
                lastStateChangedTimeMillis = System.currentTimeMillis();
            }
        }
    }
    
    


}
