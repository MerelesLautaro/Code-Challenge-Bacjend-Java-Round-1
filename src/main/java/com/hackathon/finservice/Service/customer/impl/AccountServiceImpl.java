package com.hackathon.finservice.Service.customer.impl;

import com.hackathon.finservice.DTO.response.dashboard.AccountDetailResponse;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Exception.ApiException;
import com.hackathon.finservice.Repositories.AccountRepository;
import com.hackathon.finservice.Service.customer.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public List<Account> createAccount(User user) {
        String accountId = UUID.randomUUID().toString();

        Account account = Account.builder()
                .accountId(accountId)
                .balance(BigDecimal.ZERO)
                .accountType(AccountType.Main)
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);

        return List.of(savedAccount);
    }

    @Override
    @Transactional
    public void createNewAccount(User user, String accountType, String accountNumber) {
        Account mainAccount = accountRepository.findByAccountIdAndUser(accountNumber, user)
                .orElseThrow(() -> new ApiException("Main account not found or does not belong to the user", HttpStatus.BAD_REQUEST));

        if (!mainAccount.getAccountType().equals(AccountType.Main)) {
            throw new ApiException("The referenced account is not of type Main", HttpStatus.BAD_REQUEST);
        }

        String accountId = UUID.randomUUID().toString();
        Account newAccount = Account.builder()
                .accountId(accountId)
                .balance(BigDecimal.ZERO)
                .accountType(AccountType.valueOf(accountType))
                .user(user)
                .build();

        accountRepository.save(newAccount);
    }

    @Override
    @Transactional
    public AccountDetailResponse getLoggedInUserAccount() {
        Account account = getUserAccount();
        return new AccountDetailResponse(account.getAccountId(), account.getBalance().stripTrailingZeros(), account.getAccountType());
    }

    @Override
    public AccountDetailResponse getAccountByIndex(User user, String accountIndex) {
        String indexPart = accountIndex.replaceAll("[^0-9]", "");
        String accountTypePart = accountIndex.replaceAll("[0-9]", "");

        int index;
        try {
            index = Integer.parseInt(indexPart);
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid account index format", HttpStatus.BAD_REQUEST);
        }

        List<Account> accounts = user.getAccounts();

        if (!accountTypePart.isEmpty()) {
            accounts = accounts.stream()
                    .filter(account -> account.getAccountType().name().equalsIgnoreCase(accountTypePart))
                    .toList();
        }

        if (index < 0 || index >= accounts.size()) {
            throw new ApiException("Account index out of bounds", HttpStatus.NOT_FOUND);
        }

        Account account = accounts.get(index);

        return new AccountDetailResponse(
                account.getAccountId(),
                account.getBalance().stripTrailingZeros(),
                account.getAccountType()
        );
    }


    @Override
    public Account getUserAccount() {
        User user = getLoggedInUser();

        return user.getAccounts().stream()
                .filter(account -> account.getAccountType() == AccountType.Main)
                .findFirst()
                .orElseThrow(() -> new ApiException("Main account not found", HttpStatus.NOT_FOUND));
    }

    private User validateUserPasswordAndGetUser(String password) {
        User user = getLoggedInUser();

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new ApiException("Invalid password", HttpStatus.BAD_REQUEST);
        }

        return user;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
