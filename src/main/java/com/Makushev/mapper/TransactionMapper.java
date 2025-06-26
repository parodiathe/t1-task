package com.Makushev.mapper;

import com.Makushev.dto.TransactionDto;
import com.Makushev.enums.TransactionStatus;
import com.Makushev.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports={TransactionStatus.class})
public interface TransactionMapper {
    @Mapping(target="id", ignore=true)
    @Mapping(target="time", ignore=true)
    @Mapping(target = "status", ignore=true)
    Transaction toEntity(TransactionDto dto);

    @Mapping(target="accountId", expression = "java(entity.getAccount().getId())")
    @Mapping(target = "status", expression = "java(entity.getStatus().toString())")
    TransactionDto toDto(Transaction entity);
}
