package com.Makushev.mapper;

import com.Makushev.dto.AccountDto;
import com.Makushev.enums.AccountStatus;
import com.Makushev.enums.AccountType;
import com.Makushev.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports= {AccountType.class, AccountStatus.class})
public interface AccountMapper {

    @Mapping(target="clientId", expression = "java(account.getClient().getId())")
    @Mapping(target = "accountType", expression = "java(account.getAccountType().toString())")
    @Mapping(target = "accountStatus", expression = "java(account.getAccountStatus().toString())")
    AccountDto toDto(Account account);

    @Mapping(target = "id", ignore=true)
    @Mapping(target = "accountType", expression = "java(AccountType.valueOf(accountDto.accountType()))")
    @Mapping(target = "accountStatus", expression = "java(AccountStatus.valueOf(accountDto.accountStatus()))")
    Account toEntity(AccountDto accountDto);
}
