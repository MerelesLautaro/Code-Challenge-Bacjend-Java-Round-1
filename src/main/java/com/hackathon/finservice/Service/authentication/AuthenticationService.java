package com.hackathon.finservice.Service.authentication;

import com.hackathon.finservice.DTO.request.authentication.LoginRequest;
import com.hackathon.finservice.DTO.request.authentication.UserRegisterRequest;
import com.hackathon.finservice.DTO.response.dashboard.UserDetailsResponse;
import com.hackathon.finservice.Entities.Token;

public interface AuthenticationService {
    UserDetailsResponse registerUser(UserRegisterRequest userRegisterRequest);
    Token login(LoginRequest loginRequest);
    void logout(String token);
}
