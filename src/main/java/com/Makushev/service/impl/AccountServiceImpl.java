package com.Makushev.service.impl;

import com.Makushev.annotation.LoggingException;
import com.Makushev.dto.AccountDto;
import com.Makushev.enums.AccountType;
import com.Makushev.mapper.AccountMapper;
import com.Makushev.model.Account;
import com.Makushev.model.Client;
import com.Makushev.repository.AccountRepository;
import com.Makushev.repository.ClientRepository;
import com.Makushev.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@LoggingException
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;

    @Override
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Account not found with id: " + id)
        );
        return accountMapper.toDto(account);
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Client client = clientRepository.findById(accountDto.clientId()).orElseThrow(
                () -> new EntityNotFoundException("Client not found with id: " + accountDto.clientId())
        );

        Account account = accountMapper.toEntity(accountDto);
        account.setClient(client);

        Account createdAccount = accountRepository.save(account);
        log.info("Account with id: {} was successfully created", createdAccount.getId());
        return accountMapper.toDto(createdAccount);
    }

    @Override
    public AccountDto updateAccountById(Long id, AccountDto accountDto) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Account not found with id: " + id)
        );

        if(accountDto.clientId() != null){
            Client client = clientRepository.findById(accountDto.clientId()).orElseThrow(
                    () -> new EntityNotFoundException("Client not found with id: " + accountDto.clientId())
            );
            account.setClient(client);
        }

        Optional.ofNullable(accountDto.balance()).ifPresent(account::setBalance);
        Optional.ofNullable(accountDto.accountType()).ifPresent(
                type -> account.setAccountType(AccountType.valueOf(type))
        );

        Account updatedAccount = accountRepository.save(account);

        log.info("Updated account with id: {}", account.getId());
        return accountMapper.toDto(updatedAccount);
    }

    @Override
    public void deleteAccountById(Long id) {
        if(!accountRepository.existsById(id)){
            throw new EntityNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
        log.info("Deleted account with id: {}", id);
    }
}
