package com.hackathon.finservice.Service.customer.impl;

import com.hackathon.finservice.DTO.response.dashboard.UserDetailsResponse;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Exception.ApiException;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Service.customer.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ApiException("User not found for the given identifier: " + username,
                                new UsernameNotFoundException(username), HttpStatus.BAD_REQUEST));
    }

    @Override
    public UserDetailsResponse getLoggedInUserDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ApiException("Authentication is missing", HttpStatus.FORBIDDEN);
        }

        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new ApiException("User not found in the security context", HttpStatus.FORBIDDEN);
        }

        if (user.getAccounts() == null || user.getAccounts().isEmpty()) {
            throw new ApiException("User has no accounts", HttpStatus.NOT_FOUND);
        }

        Account mainAccount = user.getAccounts().stream()
                .filter(account -> account.getAccountType() == AccountType.Main)
                .findFirst()
                .orElseThrow(() -> new ApiException("No main account found for the user",
                        HttpStatus.NOT_FOUND));

        return new UserDetailsResponse(
                user.getName(),
                user.getEmail(),
                mainAccount.getAccountId(),
                mainAccount.getAccountType().name(),
                user.getPassword()
        );
    }
}