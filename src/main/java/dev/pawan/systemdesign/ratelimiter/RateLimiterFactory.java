package dev.pawan.systemdesign.ratelimiter;

public class RateLimiterFactory {

    public static RateLimiter createRateLimiter(RateLimiterType type, long windowSizeInMillis, int maxAllowedRequests) {
        return switch (type) {
            case FIXED_WINDOW -> new FixedWindowRateLimiter(windowSizeInMillis, maxAllowedRequests);
            case SLIDING_WINDOW -> new SlidingWindowRateLimiter(windowSizeInMillis, maxAllowedRequests);
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(windowSizeInMillis, maxAllowedRequests);
            default -> throw new IllegalArgumentException("Invalid RateLimiterType: " + type);
        };
    }
}
