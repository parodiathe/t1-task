package com.Makushev.aop;


import com.Makushev.dto.ErrorLogDto;
import com.Makushev.kafka.KafkaClientProducer;
import com.Makushev.service.ErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class ErrorLogging {
    private static final String LOGGING_ANNOTATION =
            "com.example.task1.annotation.LoggingException";

    private static final String KAFKA_ERROR_HEADER_MESSAGE = "DATA_SOURCE";

    @Value("${kafka.producer.topic.client-topic}")
    private String topicName;

    private final ErrorLogService errorLogService;
    private final KafkaClientProducer<ErrorLogDto> kafkaClientProducer;

    public ErrorLogging(@Qualifier("dataSourceErrorService") ErrorLogService errorLogService, KafkaClientProducer<ErrorLogDto> kafkaClientProducer){
        this.errorLogService = errorLogService;
        this.kafkaClientProducer = kafkaClientProducer;
    }

    @AfterThrowing(
            pointcut="@within(" + LOGGING_ANNOTATION + ") || @annotation(" + LOGGING_ANNOTATION + ")",
            throwing="ex"
    )
    public void logServiceError(JoinPoint joinPoint, Exception ex){
        try{
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            ErrorLogDto errorLogDto = new ErrorLogDto(signature.getName(), ex, null);
            try{
                kafkaClientProducer.sendWithErrorCode(topicName, KAFKA_ERROR_HEADER_MESSAGE, errorLogDto);
            }catch(Exception e){
                log.error("Failed to send metrics to Kafka, saving to DB instead", e);
                errorLogService.saveErrorLog(errorLogDto);
            }
        }catch(Exception e){
            log.error("Error in saving CRUD error log in DB");
        }
    }

}
