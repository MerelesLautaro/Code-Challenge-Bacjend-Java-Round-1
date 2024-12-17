package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    long countBySourceAccountAndTargetAccountAndTransactionDateAfter(
            Account sourceAccount, Account targetAccount, Instant timestamp);
    List<Transaction> findBySourceAccount(Account sourceAccount);
}
