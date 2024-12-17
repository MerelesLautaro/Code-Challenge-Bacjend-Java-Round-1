package com.hackathon.finservice.Service.transaction;

import com.hackathon.finservice.DTO.request.transaction.TransactionRequest;
import com.hackathon.finservice.DTO.request.transaction.TransferRequest;
import com.hackathon.finservice.DTO.response.transaction.TransactionDetail;

import java.util.List;

public interface TransactionService {
    void depositMoney(TransactionRequest transactionRequest);
    void withdrawMoney(TransactionRequest transactionRequest);
    void transferMoney(TransferRequest transferRequest);
    List<TransactionDetail> getTransactions();
}
