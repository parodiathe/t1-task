package com.Makushev.kafka;

import com.Makushev.dto.ProcessedTransactionDto;
import com.Makushev.dto.TransactionDto;
import com.Makushev.enums.AccountStatus;
import com.Makushev.enums.TransactionStatus;
import com.Makushev.model.Account;
import com.Makushev.repository.AccountRepository;
import com.Makushev.repository.TransactionRepository;
import com.Makushev.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.Makushev.enums.TransactionStatus.ACCEPTED;


@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientConsumer {

    @Value("${kafka.producer.topic.transactions-accept}")
    private String transactionsAcceptTopic;

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private final TransactionService transactionService;
    private final KafkaTemplate<String, ProcessedTransactionDto> kafkaTemplate;

    @KafkaListener(id = "${kafka.consumer.group-id}",
            topics = {"${kafka.consumer.topic.transactions-topic}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload TransactionDto message,
                         @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            ProcessedTransactionDto processedTransaction = transactionService.createTransaction(message);
            kafkaTemplate.send(transactionsAcceptTopic, processedTransaction);
            ack.acknowledge();
            log.info("Transaction processed successfully: {}", processedTransaction);
        } catch (EntityNotFoundException e) {
            log.error("Account not found: {}", e.getMessage());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.consumer.topic.transactions-result-topic}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransactionResult(@Payload TransactionDto result) {
        log.info("Received transaction result: {}", result);

        switch (result.status()) {
            case "ACCEPTED" -> handleAccepted(result);
            case "BLOCKED" -> handleBlocked(result);
            case "REJECTED" -> handleRejected(result);
            default -> log.warn("Unknown status: {}", result.status());
        }
    }

    @Transactional
    private void handleAccepted(TransactionDto result) {
        transactionRepository.findById(result.transactionId())
                .ifPresent(transaction -> {
                    transaction.setStatus(ACCEPTED);
                    transactionRepository.save(transaction);
                    log.info("Transaction {} accepted", result.transactionId());
                });
    }

    @Transactional
    private void handleBlocked(TransactionDto result) {
        transactionRepository.findById(result.transactionId())
                .ifPresent(transaction -> {
                    transaction.setStatus(TransactionStatus.BLOCKED);
                    transactionRepository.save(transaction);

                    Account account = transaction.getAccount();
                    account.setAccountStatus(AccountStatus.BLOCKED);

                    BigDecimal newFrozenAmount = account.getFrozenAmount() != null
                            ? account.getFrozenAmount().add(transaction.getAmount())
                            : transaction.getAmount();

                    account.setFrozenAmount(newFrozenAmount);
                    accountRepository.save(account);

                    log.info("Transaction {} blocked. Account {} frozen. Frozen amount: {}",
                            result.transactionId(), account.getId(), newFrozenAmount);
                });
    }

    @Transactional
    private void handleRejected(TransactionDto result) {
        transactionRepository.findById(result.transactionId())
                .ifPresent(transaction -> {
                    transaction.setStatus(TransactionStatus.REJECTED);
                    transactionRepository.save(transaction);

                    Account account = transaction.getAccount();
                    account.setBalance(account.getBalance().add(transaction.getAmount()));
                    accountRepository.save(account);

                    log.info("Transaction {} rejected. Balance returned for account {}",
                            result.transactionId(), account.getId());
                });
    }
}
