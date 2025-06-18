package com.Makushev.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class DataCaching {
    private static final String CACHE_ANNOTATION =
            "com.example.task1.annotation.Cached";

    @Value("${app.data.cache-time-min}")
    private String cacheTime;

    private final Cache cache;

    public DataCaching(Map<String, CachedData> cache) {
        this.cache = new Cache();
    }

    @Around("@annotation(" + CACHE_ANNOTATION +")")
    public Object getDataFromCache(ProceedingJoinPoint joinPoint) throws Throwable{
        String hashKey = getHashKey(joinPoint);

        Object result = cache.get(hashKey);

        if(result != null){
            log.debug("Value: " + result + " gets from cache");
            return result;
        }

        result = joinPoint.proceed();

        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(Long.parseLong(cacheTime));
        cache.put(hashKey, result, expiredTime);
        log.debug("Value: " + result + " added to cache");

        return result;
    }

    @Scheduled(fixedRate = 30_000)
    private void removeExpiredDataFromCache(){
        log.debug("Removing values from cache");
        cache.evictExpired();
    }

    private String getHashKey(ProceedingJoinPoint joinPoint){
        return String.format("%s.%s(%d)",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Objects.hash(joinPoint.getArgs()));
    }
}
