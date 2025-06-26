package com.Makushev.controller;

import com.Makushev.dto.AccountDto;
import com.Makushev.service.AccountService;
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
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountDto> getAllAccounts(){
        log.info("Getting all accounts");
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable("id") Long id) {
        log.info("Getting account with id: {}", id);
        return accountService.getAccountById(id);
    }

    @PostMapping
    public AccountDto create(@RequestBody AccountDto accountDto) {
        log.info("Registering client: {}", accountDto);
        return accountService.createAccount(accountDto);
    }

    @PutMapping("/{id}")
    public AccountDto update(@PathVariable("id") Long id, @RequestBody AccountDto accountDto) {
        log.info("Updating account with id: {}", id);
        return accountService.updateAccountById(id, accountDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        log.info("Deleting account with id: {}", id);
        accountService.deleteAccountById(id);
    }

}
