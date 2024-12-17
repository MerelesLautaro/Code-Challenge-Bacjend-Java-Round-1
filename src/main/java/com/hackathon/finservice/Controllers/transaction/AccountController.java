package com.hackathon.finservice.Controllers.transaction;

import com.hackathon.finservice.DTO.request.authentication.CreateAccountRequest;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Service.customer.AccountService;
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

    @PostMapping("/create")
    public ResponseEntity<String> createNewAccount(@RequestBody CreateAccountRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        accountService.createNewAccount(user, request.accountType() , request.accountNumber());

        return ResponseEntity.ok("New account added successfully for user");
    }
}
