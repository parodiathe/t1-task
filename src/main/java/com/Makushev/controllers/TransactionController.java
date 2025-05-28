package com.Makushev.controllers;

import com.Makushev.exception.TransactionException;
import com.Makushev.model.Transaction;
import com.Makushev.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody @Valid Transaction transaction) {
        return ResponseEntity.ok(transactionRepository.save(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) throws TransactionException {
        return ResponseEntity.ok(
                transactionRepository.findById(id)
                        .orElseThrow(() -> new TransactionException("Transaction not found with id: " + id))
        );
    }

}
