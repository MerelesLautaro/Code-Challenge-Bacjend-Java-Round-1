package com.hackathon.finservice.Service.customer;

import com.hackathon.finservice.DTO.response.dashboard.UserDetailsResponse;

public interface UserService {
    UserDetailsResponse getLoggedInUserDetails();
}
