package com.hackathon.finservice.Service.transaction.impl;

import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Service.transaction.InterestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;


@Service
@Slf4j
public class InterestServiceImpl implements InterestService {

    private final AccountRepository accountRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks;

    public InterestServiceImpl(AccountRepository accountRepository, ThreadPoolTaskScheduler taskScheduler){
        this.accountRepository = accountRepository;
        this.taskScheduler = taskScheduler;
        this.scheduledTasks = new HashMap<>();
    }

    @Override
    public void startInterestTask(User user) {
        Optional<Account> accountInterest = accountRepository.findByUserAndAccountType(user, AccountType.Invest);

        if (accountInterest.isPresent()) {
            Account investAccount = accountInterest.get();

            // Solo iniciamos la tarea si el saldo es mayor a 0
            if (investAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                Runnable task = () -> {
                    try {
                        this.applyInvestmentInterest(user);
                    } catch (RuntimeException e) {
                        log.warn("Stopping interest task due to an error", e);
                        cancelInterestTask(user);
                    }
                };

                log.info("Interest task started, waiting for first 10-second cycle...");
                scheduleInterestTask(user, task, 10);
            } else {
                log.info("Account balance is 0, not starting interest task for user: {}", user.getUsername());
            }
        } else {
            log.warn("No investment account found for user: {}", user.getUsername());
        }
    }

    public void scheduleInterestTask(User user, Runnable task, long delayInSeconds) {
        Long userId = user.getId();
        ScheduledFuture<?> existingTask = scheduledTasks.get(userId);

        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(true);
            scheduledTasks.remove(userId);
        }

        ScheduledFuture<?> futureTask = taskScheduler.scheduleWithFixedDelay(
                task, Instant.now(), Duration.ofSeconds(delayInSeconds));

        scheduledTasks.put(userId, futureTask);
    }

    public void cancelInterestTask(User user) {
        Long userId = user.getId();
        ScheduledFuture<?> existingTask = scheduledTasks.get(userId);
        if (existingTask != null) {
            existingTask.cancel(true);
            scheduledTasks.remove(userId);
        }
    }

    private boolean firstExecution = true;

    public void applyInvestmentInterest(User user) {
        Optional<Account> accountInterest = accountRepository.findByUserAndAccountType(user, AccountType.Invest);

        if (accountInterest.isPresent()) {
            Account investAccount = accountInterest.get();

            if (firstExecution) {
                log.info("Waiting 10 seconds before applying interest for the first time.");
                firstExecution = false;
                return;
            }

            if (investAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal currentBalance = investAccount.getBalance();
                BigDecimal interest = currentBalance.multiply(BigDecimal.valueOf(0.10));
                BigDecimal newBalance = currentBalance.add(interest);

                investAccount.setBalance(newBalance);
                accountRepository.save(investAccount);

                log.info("Applied 10% interest to account: {}. New balance: {}", investAccount.getAccountId(), newBalance);
            }
        } else {
            log.warn("No investment account found for user: {}", user.getUsername());
        }
    }
}