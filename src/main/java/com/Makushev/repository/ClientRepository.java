package com.Makushev.repository;

import com.Makushev.annotation.Cached;
import com.Makushev.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Cached
    Optional<Client> findById(Long id);
}
