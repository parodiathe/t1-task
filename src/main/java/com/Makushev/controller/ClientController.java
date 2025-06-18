package com.Makushev.controller;

import com.Makushev.dto.ClientDto;
import com.Makushev.service.ClientService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ClientDto getClient(@PathVariable("id") Long clientId) {
        log.info("Getting client with id: {}", clientId);
        return clientService.getClientById(clientId);
    }

    @PostMapping
    public ClientDto register(@RequestBody ClientDto clientDto) {
        log.info("Registering client: {}", clientDto);
        return clientService.registerClient(clientDto);
    }

    @PutMapping("/{id}")
    public ClientDto update(@PathVariable("id") long clientId, @RequestBody ClientDto clientDto) {
        log.info("Updating client with id: {}", clientId);
        return clientService.updateClientById(clientId, clientDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long clientId){
        log.info("Deleting client with id: {}", clientId);
        clientService.deleteClientById(clientId);
    }
}
