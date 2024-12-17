package com.hackathon.finservice.Controllers.transaction;

import com.hackathon.finservice.DTO.ReducedGenericResponse;
import com.hackathon.finservice.DTO.request.authentication.CreateAccountRequest;
import com.hackathon.finservice.DTO.request.transaction.TransactionRequest;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Service.customer.AccountService;
import com.hackathon.finservice.Service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<String> createNewAccount(@Valid @RequestBody CreateAccountRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        accountService.createNewAccount(user, request.accountType() , request.accountNumber());

        return ResponseEntity.ok("New account added successfully for user");
    }

    @PostMapping("/deposit")
    public ResponseEntity<ReducedGenericResponse> depositMoney(@Valid @RequestBody TransactionRequest transactionRequest) {
        transactionService.depositMoney(transactionRequest);
        return ResponseEntity.ok(getGenericResponse("Cash deposited successfully"));
    }

    private ReducedGenericResponse getGenericResponse(String message) {
        return new ReducedGenericResponse(message);
    }
}