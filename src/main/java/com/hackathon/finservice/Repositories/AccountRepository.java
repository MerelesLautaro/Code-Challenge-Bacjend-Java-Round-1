package com.hackathon.finservice.Repositories;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(String accountId);
    Optional<Account> findByAccountIdAndUser(String mainAccountId, User user);
    boolean existsByUserAndAccountType(User user, AccountType accountType);
}
