package dev.pawan.systemdesign.ratelimiter;


public abstract class RateLimiter {
    final long windowSizeInMillis;
    final int maxAllowedRequests;

    public RateLimiter(long windowSizeInMillis, int maxAllowedRequests){
        this.windowSizeInMillis = windowSizeInMillis;
        this.maxAllowedRequests = maxAllowedRequests;
    }

    public abstract boolean isAllowed(String userId);


}