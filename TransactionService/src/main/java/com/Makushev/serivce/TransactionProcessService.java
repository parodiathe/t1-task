package com.Makushev.serivce;

import com.Makushev.model.dto.TransactionDto;

public interface TransactionProcessService {
    void processTransaction(TransactionDto transactionDto);
}