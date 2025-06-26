package com.Makushev.kafka;

import com.Makushev.model.dto.TransactionDto;
import com.Makushev.serivce.TransactionProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionAcceptConsumer {

    private final TransactionProcessService processorService;

    @KafkaListener(
            topics = "${kafka.consumer.topic.transactions-accept}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenTransactionAccept(
            @Payload TransactionDto transaction,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("Received transaction for processing: {}", transaction);
        try {
            processorService.processTransaction(transaction);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
        }
    }
}