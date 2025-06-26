package com.Makushev.controller;

import com.Makushev.dto.ProcessedTransactionDto;
import com.Makushev.dto.TransactionDto;
import com.Makushev.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> getAllTransactions(){
        log.info("Getting all transactions");
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public TransactionDto get(@PathVariable("id") Long id){
        log.info("Getting transaction with id: {}", id);
        return transactionService.getTransactionById(id);
    }

    @PostMapping
    public ProcessedTransactionDto save(@RequestBody TransactionDto dto) {
        log.info("Making transaction: {}", dto);
        return transactionService.createTransaction(dto);
    }

    @PutMapping("/{id}")
    public TransactionDto update(@PathVariable("id") long id, @RequestBody TransactionDto dto) {
        log.info("Updating transaction with id: {}", id);
        return transactionService.updateTransactionById(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        log.info("Deleting transaction with id: {}", id);
        transactionService.deleteTransactionById(id);
    }

}
