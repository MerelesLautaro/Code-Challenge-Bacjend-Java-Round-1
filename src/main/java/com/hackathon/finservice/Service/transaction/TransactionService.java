package com.hackathon.finservice.Service.transaction;

import com.hackathon.finservice.DTO.request.transaction.TransactionRequest;

public interface TransactionService {
    void depositMoney(TransactionRequest transactionRequest);
}
