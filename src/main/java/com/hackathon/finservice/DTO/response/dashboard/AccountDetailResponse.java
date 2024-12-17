package com.hackathon.finservice.DTO.response.dashboard;

import com.hackathon.finservice.Entities.AccountType;

import java.math.BigDecimal;

public record AccountDetailResponse(String accountNumber, BigDecimal balance, AccountType accountType) {
}
