package com.Makushev.annotations;

import com.Makushev.model.TimeLimitExceedLog;
import com.Makushev.repository.TimeLimitExceedLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class MetricAspect {

    private final TimeLimitExceedLogRepository logRepository;
    private final long limitTime;

    public MetricAspect(TimeLimitExceedLogRepository logRepository,
                        @Value("${metric.time-limit-ms}")
                        long limitTime) {
        this.logRepository = logRepository;
        this.limitTime = limitTime;
    }


    @Around("@annotation(com.Makushev.annotations.Metric)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > limitTime) {
                TimeLimitExceedLog logEntry = new TimeLimitExceedLog();
                logEntry.setSignature(joinPoint.getSignature().toShortString());
                logEntry.setTimeMs(duration);
                logEntry.setTimestamp(LocalDateTime.now());

                logRepository.save(logEntry);
            }
        }
    }
}
