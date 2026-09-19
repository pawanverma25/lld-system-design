package dev.pawan.systemdesign.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FixedWindowRateLimiter extends RateLimiter{

    public class Window {
        protected long windowStart;
        protected int requestCount;

        public Window(long windowStart, int requestCount) {
            this.windowStart = windowStart;
            this.requestCount = requestCount;
        }
    }

    private final Map<String, Window> userWindows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(long windowSizeInMillis, int maxAllowedRequests) {
        super(windowSizeInMillis, maxAllowedRequests);
    }

    @Override
    public boolean isAllowed(String userId) {
        AtomicBoolean isAllowed = new AtomicBoolean(false);
        userWindows.compute(userId, (key, window) -> {
            long now = System.currentTimeMillis();
            if(window == null || now - window.windowStart >= windowSizeInMillis){
                isAllowed.set(true);
                return new Window(now, 1);
            } else {
                if(window.requestCount < maxAllowedRequests) {
                    isAllowed.set(true);
                    return new Window(window.windowStart, window.requestCount + 1);
                }
            }
            return window;
        });
        return isAllowed.get();
    }
}
