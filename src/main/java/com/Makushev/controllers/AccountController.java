package com.Makushev.controllers;

import com.Makushev.annotations.LogDataSourceError;
import com.Makushev.exception.AccountException;
import com.Makushev.model.Account;
import com.Makushev.repository.AccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private AccountRepository accountRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody @Valid Account account) {
        return ResponseEntity.ok(accountRepository.save(account));
    }

    @GetMapping("/{id}")
    @LogDataSourceError
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) throws AccountException {
        return ResponseEntity.ok(
                accountRepository.findById(id)
                        .orElseThrow(() -> new AccountException("Account not found with id: " + id))
        );
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountRepository.findAll());
    }

}
