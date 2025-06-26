package com.Makushev.aop;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Cache {
    private final Map<String, CachedData> cache;

    public Cache(){
        this.cache = new HashMap<>();
    }

    public void put(String key, Object value, LocalDateTime expiredTime){
        cache.put(key, new CachedData(expiredTime, value));
    }

    public Object get(String key){
        if(cache.get(key) != null){
            return cache.get(key).data();
        }
        return null;
    }

    public void evictExpired(){
        cache.values().removeIf(data -> LocalDateTime.now().isAfter(data.time()));
    }
}
