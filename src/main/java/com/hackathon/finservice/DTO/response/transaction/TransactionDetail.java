package com.hackathon.finservice.DTO.response.transaction;

import java.math.BigDecimal;

public record TransactionDetail(Long id, BigDecimal amount, String transactionType,
                                String transactionStatus, long transactionDate,
                                String sourceAccountNumber, String targetAccountNumber) {
}
