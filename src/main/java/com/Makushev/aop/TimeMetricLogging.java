package com.Makushev.aop;

import com.Makushev.dto.ErrorLogDto;
import com.Makushev.kafka.KafkaClientProducer;
import com.Makushev.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeMetricLogging {
    private static final String METRIC_ANNOTATION =
            "com.example.task1.annotation.Metric";

    private static final String KAFKA_ERROR_HEADER_MESSAGE = "METRICS";

    @Value("${app.time.limit-ms}")
    private String timeInMillis;

    @Value("${kafka.producer.topic.client-topic}")
    private String topicName;

    private final KafkaClientProducer<ErrorLogDto> kafkaProducer;
    private final ErrorLogService errorLogService;

    public TimeMetricLogging(
            @Qualifier("timeLimitExceedErrorService") ErrorLogService errorLogService,
            KafkaClientProducer<ErrorLogDto> kafkaProducer) {
        this.errorLogService = errorLogService;
        this.kafkaProducer = kafkaProducer;
    }

    @Around("@annotation(" + METRIC_ANNOTATION + ")")
    public Object timeMeasuringAndErrorLogging(ProceedingJoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;

        if (duration > Long.parseLong(timeInMillis)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            ErrorLogDto errorLogDto = new ErrorLogDto(signature.getName(), null, duration);
            log.warn("Method: ({}) exceed time limit with time: {}ms", signature, duration);

            try {
                kafkaProducer.sendWithErrorCode(topicName, KAFKA_ERROR_HEADER_MESSAGE, errorLogDto);
            } catch (Exception e) {
                log.error("Failed to send metrics to Kafka, saving to DB instead", e);
                errorLogService.saveErrorLog(errorLogDto);
            }
        }

        return result;
    }
}
