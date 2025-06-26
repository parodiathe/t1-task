package com.Makushev.serivce.impl;

import com.Makushev.model.TransactionResult;
import com.Makushev.model.dto.TransactionDto;
import com.Makushev.model.enums.TransactionStatus;
import com.Makushev.serivce.TransactionProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionProcessServiceImpl implements TransactionProcessService {

    private final KafkaTemplate<String, TransactionResult> kafkaTemplate;

    @Value("${kafka.producer.topic.transactions-result-topic}")
    private String transactionsResultTopic;

    @Value("${transaction.limits.max-count}")
    private int maxTransactionCount;

    @Value("${transaction.limits.time-window}")
    private long timeWindowSeconds;

    private final Map<Long, Queue<TransactionDto>> transactionStore = new ConcurrentHashMap<>();

    public void processTransaction(TransactionDto transactionDto) {
        if (checkTransactionLimit(transactionDto.clientId(), transactionDto)) {
            blockTransactions(transactionDto.clientId());
            return;
        }

        if (transactionDto.transactionAmount().compareTo(transactionDto.accountBalance()) > 0) {
            sendRejectedResult(transactionDto, "Insufficient funds");
            return;
        }

        addTransactionToStore(transactionDto);
        sendAcceptedResult(transactionDto);
    }

    private boolean checkTransactionLimit(Long clientId, TransactionDto currentTransaction) {
        Queue<TransactionDto> clientTransactions = transactionStore.getOrDefault(clientId, new LinkedList<>());

        LocalDateTime windowStart = LocalDateTime.now().minusSeconds(timeWindowSeconds);
        while (!clientTransactions.isEmpty() &&
                clientTransactions.peek().timestamp().isBefore(windowStart)) {
            clientTransactions.poll();
        }

        System.out.println(clientTransactions.size() >= maxTransactionCount);
        System.out.println(currentTransaction.timestamp().isAfter(windowStart));

        return clientTransactions.size() >= maxTransactionCount &&
                currentTransaction.timestamp().isAfter(windowStart);
    }

    private void blockTransactions(Long clientId) {
        Queue<TransactionDto> clientTransactions = transactionStore.get(clientId);
        if (clientTransactions == null) return;

        LocalDateTime windowStart = LocalDateTime.now().minusSeconds(timeWindowSeconds);

        clientTransactions.stream()
                .filter(tx -> tx.timestamp().isAfter(windowStart))
                .forEach(this::sendBlockedResult);
    }

    private void addTransactionToStore(TransactionDto transactionDto) {
        transactionStore.compute(transactionDto.clientId(), (key, queue) -> {
            if (queue == null) {
                queue = new LinkedList<>();
            }
            queue.add(transactionDto);
            return queue;
        });
    }

    private void sendBlockedResult(TransactionDto dto) {
        TransactionResult result = new TransactionResult(
                dto.accountId(),
                dto.transactionId(),
                TransactionStatus.BLOCKED,
                "Transaction blocked: limit exceeded"
        );
        kafkaTemplate.send(transactionsResultTopic, result);
    }

    private void sendRejectedResult(TransactionDto dto, String reason) {
        TransactionResult result = new TransactionResult(
                dto.accountId(),
                dto.transactionId(),
                TransactionStatus.REJECTED,
                reason
        );
        kafkaTemplate.send(transactionsResultTopic, result);
    }

    private void sendAcceptedResult(TransactionDto dto) {
        TransactionResult result = new TransactionResult(
                dto.accountId(),
                dto.transactionId(),
                TransactionStatus.ACCEPTED,
                "Transaction accepted"
        );
        kafkaTemplate.send(transactionsResultTopic, result);
    }
}