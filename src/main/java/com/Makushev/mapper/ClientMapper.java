package com.Makushev.mapper;

import com.Makushev.dto.ClientDto;
import com.Makushev.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDto toDto(Client client);

    @Mapping(target="id", ignore=true)
    Client toEntity(ClientDto clientDto);
}
