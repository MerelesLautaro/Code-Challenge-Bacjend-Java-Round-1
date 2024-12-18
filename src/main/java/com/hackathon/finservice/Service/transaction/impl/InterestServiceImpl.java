package com.hackathon.finservice.Service.transaction.impl;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Service.transaction.InterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class InterestServiceImpl implements InterestService {

    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void applyInvestmentInterest() {
        List<Account> investAccounts = accountRepository.findByAccountType(AccountType.Invest);

        for (Account investAccount : investAccounts) {
            if (investAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal currentBalance = investAccount.getBalance();
                BigDecimal interest = currentBalance.multiply(BigDecimal.valueOf(0.10));
                BigDecimal newBalance = currentBalance.add(interest);

                investAccount.setBalance(newBalance);

                accountRepository.save(investAccount);

                log.info("Applied 10% interest to account: {}. New balance: {}", investAccount.getAccountId(), newBalance);
            }
        }
    }
}
