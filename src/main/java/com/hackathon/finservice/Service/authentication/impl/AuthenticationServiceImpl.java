package com.hackathon.finservice.Service.authentication.impl;

import com.hackathon.finservice.DTO.request.authentication.LoginRequest;
import com.hackathon.finservice.DTO.request.authentication.UserRegisterRequest;
import com.hackathon.finservice.DTO.response.dashboard.UserDetailsResponse;
import com.hackathon.finservice.Entities.Account;
import com.hackathon.finservice.Entities.AccountType;
import com.hackathon.finservice.Entities.Token;
import com.hackathon.finservice.Entities.User;
import com.hackathon.finservice.Exception.ApiException;
import com.hackathon.finservice.Repositories.UserRepository;
import com.hackathon.finservice.Security.JWTBlacklistManager;
import com.hackathon.finservice.Service.authentication.AuthenticationService;
import com.hackathon.finservice.Service.authentication.TokenService;
import com.hackathon.finservice.Service.customer.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;
    private final JWTBlacklistManager jwtBlacklistManager;


    @Override
    @Transactional
    public UserDetailsResponse registerUser(UserRegisterRequest userRegisterRequest) {
        validateEmail(userRegisterRequest);

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());

        User user = createUserAndAccount(userRegisterRequest, encodedPassword);

        Account mainAccount = user.getAccounts().stream()
                .filter(account -> account.getAccountType() == AccountType.Main)
                .findFirst()
                .orElseThrow(() -> new ApiException("Main account not created for user",
                        HttpStatus.INTERNAL_SERVER_ERROR));

        return createUserResponse(
                userRegisterRequest,
                encodedPassword,
                mainAccount.getAccountId(),
                mainAccount.getAccountType().name()
        );
    }

    @Override
    public Token login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.identifier(), loginRequest.password()));

        User userDetails = (User) authentication.getPrincipal();

        String token = tokenService.generateToken(userDetails);

        return new Token(token);
    }

    @Override
    public void logout(String token) {
        jwtBlacklistManager.addTokenToBlackList(token);
    }


    private void validateEmail(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmailIgnoreCase(userRegisterRequest.email())) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private UserDetailsResponse createUserResponse(UserRegisterRequest userRegisterRequest,
                                                   String encodedPassword,
                                                   String accountNumber,
                                                   String accountType) {

        return new UserDetailsResponse(
                userRegisterRequest.name(),
                userRegisterRequest.email(),
                accountNumber,
                accountType,
                encodedPassword
        );
    }

    private User createUserAndAccount(UserRegisterRequest userRegisterRequest, String encodedPassword) {


        User user = User.builder()
                .name(userRegisterRequest.name())
                .email(userRegisterRequest.email())
                .password(encodedPassword)
                .build();

        user = userRepository.save(user);
        user.setAccounts(accountService.createAccount(user));
        return user;
    }
}
