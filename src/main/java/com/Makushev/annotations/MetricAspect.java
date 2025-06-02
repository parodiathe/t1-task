package com.Makushev.annotations;

import com.Makushev.kafka.KafkaSender;
import com.Makushev.model.TimeLimitExceedLog;
import com.Makushev.repository.TimeLimitExceedLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class MetricAspect {

    private final TimeLimitExceedLogRepository logRepository;
    private final KafkaSender kafkaSender;
    private final Logger logger = LoggerFactory.getLogger(MetricAspect.class);

    private final long limitTime;

    public MetricAspect(TimeLimitExceedLogRepository logRepository,
                        KafkaSender kafkaSender,
                        @Value("${metric.time-limit-ms}") long limitTime) {
        this.logRepository = logRepository;
        this.kafkaSender = kafkaSender;
        this.limitTime = limitTime;
    }

    @Around("@annotation(com.Makushev.annotations.Metric)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Аспект измеряет метод: {}", joinPoint.getSignature().toShortString());

        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Метод {} выполнился за {} мс", joinPoint.getSignature().toShortString(), duration);

            if (duration > limitTime) {
                String message = String.format("Превышен лимит времени выполнения: %d мс (лимит: %d мс)", duration, limitTime);
                String key = joinPoint.getSignature().toShortString();

                try {
                    kafkaSender.send("t1_demo_metrics", key, message, "METRICS");
                    logger.info("Сообщение о превышении времени отправлено в Kafka.");
                } catch (Exception e) {
                    logger.error("Ошибка отправки в Kafka, сохраняем в БД.", e);
                    saveToDatabase(joinPoint, duration);
                }
            }
        }
    }

    private void saveToDatabase(ProceedingJoinPoint joinPoint, long duration) {
        TimeLimitExceedLog logEntry = new TimeLimitExceedLog();
        logEntry.setSignature(joinPoint.getSignature().toShortString());
        logEntry.setTimeMs(duration);
        logEntry.setTimestamp(LocalDateTime.now());

        logRepository.save(logEntry);
    }
}