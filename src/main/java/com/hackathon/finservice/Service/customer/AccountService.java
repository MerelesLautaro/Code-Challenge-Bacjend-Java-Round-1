package com.hackathon.finservice.Service.customer;

import com.hackathon.finservice.DTO.response.dashboard.AccountDetailResponse;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;

import java.util.List;

public interface AccountService {
    List<Account> createAccount(User user);
    void createNewAccount(User user, AccountType accountType, String accountNumber);
    AccountDetailResponse getLoggedInUserAccount();
    AccountDetailResponse getAccountByIndex(User user, String accountIndex);
    Account getUserAccount();
}

