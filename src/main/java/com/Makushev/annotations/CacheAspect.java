package com.Makushev.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CacheAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final long timeToLive;

    public CacheAspect(RedisTemplate<String, Object> redisTemplate,
                       @Value("${cache.time-to-live}") long timeToLive) {
        this.redisTemplate = redisTemplate;
        this.timeToLive = timeToLive;
    }

    @Around("@annotation(com.Makushev.annotations.Cached)")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = generateKey(joinPoint);

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            System.out.println("From cache: " + key);
            return cached;
        }

        Object result = joinPoint.proceed();

        redisTemplate.opsForValue().set(key, result, timeToLive, TimeUnit.SECONDS);

        return result;
    }

    private String generateKey(ProceedingJoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append(joinPoint.getSignature().getName());

        for (Object arg : joinPoint.getArgs()) {
            sb.append(":").append(arg.toString());
        }

        return sb.toString();
    }
}
