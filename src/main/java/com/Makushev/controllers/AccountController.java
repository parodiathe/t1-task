package com.Makushev.controllers;

import com.Makushev.annotations.Cached;
import com.Makushev.annotations.LogDataSourceError;
import com.Makushev.annotations.Metric;
import com.Makushev.exception.AccountException;
import com.Makushev.model.Account;
import com.Makushev.repository.AccountRepository;
import jakarta.validation.Valid;
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
    public Account createAccount(@RequestBody @Valid Account account) {
        return accountRepository.save(account);
    }

    @GetMapping("/{id}")
    @LogDataSourceError
    public Account getAccountById(@PathVariable Long id) throws AccountException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account not found with id: " + id));
    }

    @GetMapping
    @Metric
    @Cached
    @LogDataSourceError
    public List<Account> getAllAccounts() {
        System.out.println("Import from DB");
        return accountRepository.findAll();
    }

}
