package com.hackathon.finservice.DTO.request.authentication;

import com.hackathon.finservice.Entities.AccountType;

public record CreateAccountRequest(String accountNumber, AccountType accountType) {}
