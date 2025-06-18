package com.Makushev.repository;

import com.Makushev.annotation.Cached;
import com.Makushev.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Cached
    Optional<Transaction> findById(Long id);

    @Cached
    List<Transaction> findAll();

}
