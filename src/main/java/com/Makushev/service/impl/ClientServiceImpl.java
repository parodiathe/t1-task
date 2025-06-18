package com.Makushev.service.impl;

import com.Makushev.annotation.LoggingException;
import com.Makushev.annotation.Metric;
import com.Makushev.dto.ClientDto;
import com.Makushev.mapper.ClientMapper;
import com.Makushev.model.Client;
import com.Makushev.repository.ClientRepository;
import com.Makushev.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@LoggingException
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientMapper clientMapper;
    private final ClientRepository clientRepository;

    @Metric
    @Override
    public ClientDto registerClient(ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        Client createdClient = clientRepository.save(client);
        log.info("Client with name: {} was successfully registered", createdClient.getName());
        return clientMapper.toDto(createdClient);
    }

    @Metric
    @Override
    public ClientDto getClientById(Long clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new EntityNotFoundException("Client not found with id: " + clientId)
        );
        return clientMapper.toDto(client);
    }

    @Metric
    @Override
    public ClientDto updateClientById(Long clientId, ClientDto clientDto) {
        Client client = clientRepository.findById(clientId).orElseThrow(
                () -> new EntityNotFoundException("Client not found with id: " + clientId)
        );

        Optional.ofNullable(clientDto.firstName()).ifPresent(client::setName);
        Optional.ofNullable(clientDto.middleName()).ifPresent(client::setPatronymic);
        Optional.ofNullable(clientDto.lastName()).ifPresent(client::setSurname);
        Optional.ofNullable(clientDto.clientId()).ifPresent(client::setClientId);

        Client updatedClient = clientRepository.save(client);
        log.info("Client with id: {} was updated successfully", clientId);
        return clientMapper.toDto(updatedClient);
    }

    @Metric
    @Override
    public void deleteClientById(Long clientId) {
        if(!clientRepository.existsById(clientId)){
            throw new EntityNotFoundException("Client not found with id: " + clientId);
        }
        clientRepository.deleteById(clientId);
        log.info("Client with id: {} was successfully deleted", clientId);
    }
}
