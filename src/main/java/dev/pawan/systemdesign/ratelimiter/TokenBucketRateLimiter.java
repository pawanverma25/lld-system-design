package dev.pawan.systemdesign.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenBucketRateLimiter extends RateLimiter{

    public class Bucket{
        protected int tokens;
        protected long lastRefillTimestamp;
        public Bucket(int tokens, long lastRefillTimestamp) {
            this.tokens = tokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(long windowSizeInMillis, int maxAllowedRequests) {
        super(windowSizeInMillis, maxAllowedRequests);
    }

    @Override
    public boolean isAllowed(String userId) {
        AtomicBoolean isAllowed = new AtomicBoolean(false);
        userBuckets.compute(userId, (key, bucket) -> {
            long now = System.currentTimeMillis();
            if(bucket == null){
                isAllowed.set(true);
                return new Bucket(maxAllowedRequests - 1, now);
            } else {
                long elapsedTime = now - bucket.lastRefillTimestamp;
                int tokensToAdd = (int) (elapsedTime * maxAllowedRequests / windowSizeInMillis);
                bucket.tokens = Math.min(bucket.tokens + tokensToAdd, maxAllowedRequests);
                bucket.lastRefillTimestamp = now;

                if(bucket.tokens > 0){
                    bucket.tokens--;
                    isAllowed.set(true);
                } else {
                    isAllowed.set(false);
                }
                return bucket;
            }
        });
        return isAllowed.get();
    }
}
