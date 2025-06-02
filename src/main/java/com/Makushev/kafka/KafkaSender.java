package com.Makushev.kafka;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;


@Component
public class KafkaSender {

    private final KafkaProducer<String, String> kafkaProducer;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public KafkaSender(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PreDestroy
    private void closeProducer() {
        kafkaProducer.close();
    }

    public void send(String topic, String key, String value, String errorType) throws ExecutionException, InterruptedException, ExecutionException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        record.headers().add("errorType", errorType.getBytes());

        kafkaProducer.send(record).get();
    }

    private void producerSend(ProducerRecord<String, String> record) {
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Сообщение успешно доставлено в топик {}, партиция {}, offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                log.error("Ошибка доставки сообщения в Kafka: {}", exception.getMessage(), exception);
            }
        });
    }
}