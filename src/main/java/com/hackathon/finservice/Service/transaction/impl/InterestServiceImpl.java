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


    public void startInterestTask(User user) {
        Runnable task = () -> {
            try {
                this.applyInvestmentInterest(user);
            } catch (RuntimeException e) {
                log.warn("Stopping interest task due to an error", e);
                cancelInterestTask(user.getId());
            }
        };

        scheduleInterestTask(user, task, 10);
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

    public void cancelInterestTask(Long userId) {
        if (scheduledTasks.get(userId) != null) {
            scheduledTasks.get(userId).cancel(true);
            scheduledTasks.remove(userId);
        }
    }

    @Transactional
    public void applyInvestmentInterest(User user) {
        Optional<Account> accountInterest = accountRepository.findByUserAndAccountType(user, AccountType.Invest);

        if (accountInterest.isPresent()) {
            Account investAccount = accountInterest.get();
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