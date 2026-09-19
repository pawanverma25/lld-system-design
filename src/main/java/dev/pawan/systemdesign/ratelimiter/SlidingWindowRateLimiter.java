package dev.pawan.systemdesign.ratelimiter;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SlidingWindowRateLimiter extends RateLimiter {
    Map<String, Queue<Long>> userRequestTimestamps = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(long windowSizeInMillis, int maxAllowedRequests) {
        super(windowSizeInMillis, maxAllowedRequests);
    }

    @Override
    public boolean isAllowed(String userId) {
        AtomicBoolean isAllowed = new AtomicBoolean(false);
        userRequestTimestamps.compute(userId, (key, timestamps) -> {
            long now = System.currentTimeMillis();
            if (timestamps == null) {
                isAllowed.set(true);
                return (Queue<Long>) new LinkedList<Long>();
            } else {
                while (!timestamps.isEmpty() && now - timestamps.peek() >= windowSizeInMillis) {
                    timestamps.poll();
                }
                if (timestamps.size() < maxAllowedRequests) {
                    isAllowed.set(true);
                }
                return timestamps;
            }
        });
        return isAllowed.get();
    }
}
