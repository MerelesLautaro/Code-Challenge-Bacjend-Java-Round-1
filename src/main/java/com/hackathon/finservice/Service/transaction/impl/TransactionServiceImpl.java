package com.hackathon.finservice.Service.transaction.impl;

import com.hackathon.finservice.DTO.request.transaction.TransactionRequest;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.Transaction;
import com.hackathon.finservice.Entities.TransactionStatus;
import com.hackathon.finservice.Entities.TransactionType;
import com.hackathon.finservice.Exception.ApiException;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Repositories.TransactionRepository;
import com.hackathon.finservice.Service.customer.AccountService;
import com.hackathon.finservice.Service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void depositMoney(TransactionRequest transactionRequest) {
        Account account = getAccount();

        BigDecimal depositAmount = transactionRequest.amount();

        BigDecimal commission = BigDecimal.ZERO;
        if (depositAmount.compareTo(BigDecimal.valueOf(50000)) > 0) {
            commission = depositAmount.multiply(BigDecimal.valueOf(0.02));
        }
        BigDecimal finalAmount = depositAmount.subtract(commission);

        account.setBalance(account.getBalance().add(finalAmount));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .sourceAccount(account)
                .transactionDate(Instant.now())
                .amount(depositAmount)
                .transactionType(TransactionType.CASH_DEPOSIT)
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

    }

    private Account getAccount() {
        return accountService.getUserAccount();
    }
}
