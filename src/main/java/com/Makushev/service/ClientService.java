package com.Makushev.service;

import com.Makushev.dto.ClientDto;

public interface ClientService {
    ClientDto registerClient(ClientDto clientDto);
    ClientDto getClientById(Long clientId);
    ClientDto updateClientById(Long clientId, ClientDto client);
    void deleteClientById(Long clientId);
}
