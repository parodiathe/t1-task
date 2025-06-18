package com.Makushev.service;

import com.Makushev.dto.AccountDto;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAllAccounts();
    AccountDto getAccountById(Long id);
    AccountDto createAccount(AccountDto accountDto);
    AccountDto updateAccountById(Long id, AccountDto accountDto);
    void deleteAccountById(Long id);
}
