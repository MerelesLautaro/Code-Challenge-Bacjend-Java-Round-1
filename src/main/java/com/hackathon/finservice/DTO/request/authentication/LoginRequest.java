package com.hackathon.finservice.DTO.request.authentication;

import com.hackathon.finservice.Util.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank @Email String identifier,
                           @Password String password) {
}
