package com.Makushev.service;

import com.Makushev.dto.ProcessedTransactionDto;
import com.Makushev.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> getAllTransactions();
    TransactionDto getTransactionById(Long id);
    ProcessedTransactionDto createTransaction(TransactionDto transactionDto);
    TransactionDto updateTransactionById(Long id, TransactionDto transactionDto);
    void deleteTransactionById(Long id);
}
