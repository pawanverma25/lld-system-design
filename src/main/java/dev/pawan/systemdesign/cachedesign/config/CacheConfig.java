package dev.pawan.systemdesign.cachedesign.config;

import dev.pawan.systemdesign.cachedesign.enums.EvictionPolicyType;

public record CacheConfig (
    int capacity,
    EvictionPolicyType evictionPolicyType,
    int ttl
){}
