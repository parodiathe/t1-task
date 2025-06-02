package com.Makushev.annotations;

import com.Makushev.kafka.KafkaSender;
import com.Makushev.model.DataSourceErrorLog;
import com.Makushev.repository.DataSourceErrorLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;
    private final KafkaSender kafkaSender;
    private final Logger logger = LoggerFactory.getLogger(LogDataSourceErrorAspect.class);

    public LogDataSourceErrorAspect(DataSourceErrorLogRepository dataSourceErrorLogRepository,
                                    KafkaSender kafkaSender) {
        this.dataSourceErrorLogRepository = dataSourceErrorLogRepository;
        this.kafkaSender = kafkaSender;
    }

    @Around("@annotation(com.Makushev.annotations.LogDataSourceError)")
    public Object logError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            logger.error("Перехвачено исключение в аспекте: {}", e.getMessage());

            String topic = "t1_demo_metrics";
            String key = joinPoint.getSignature().toShortString();
            String message = e.getMessage();

            try {
                kafkaSender.send(topic, key, message, "DATA_SOURCE");
                logger.info("Сообщение об ошибке источника данных отправлено в Kafka.");
            } catch (Exception ex) {
                logger.error("Ошибка отправки в Kafka, сохраняем в БД.");
                saveErrorToDatabase(joinPoint, e);
            }

            throw e;
        }
    }

    private void saveErrorToDatabase(ProceedingJoinPoint joinPoint, Exception e) {
        DataSourceErrorLog logEntry = new DataSourceErrorLog();
        logEntry.setMessage(e.getMessage());
        logEntry.setSignature(joinPoint.getSignature().toShortString());
        logEntry.setExceptionText(e.toString());
        logEntry.setTimestamp(LocalDateTime.now());

        dataSourceErrorLogRepository.save(logEntry);
    }
}