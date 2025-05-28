package com.Makushev.annotations;

import com.Makushev.model.DataSourceErrorLog;
import com.Makushev.repository.DataSourceErrorLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Autowired
    public LogDataSourceErrorAspect(DataSourceErrorLogRepository dataSourceErrorLogRepository) {
        this.dataSourceErrorLogRepository = dataSourceErrorLogRepository;
    }

    @Around("@annotation(com.Makushev.annotations.LogDataSourceError)")
    public Object logError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            DataSourceErrorLog logEntry = new DataSourceErrorLog();
            logEntry.setMessage(e.getMessage());
            logEntry.setSignature(joinPoint.getSignature().toShortString());
            logEntry.setExceptionText(e.toString());
            logEntry.setTimestamp(LocalDateTime.now());

            dataSourceErrorLogRepository.save(logEntry);

            throw e;
        }
    }
}
