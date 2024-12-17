package com.hackathon.finservice.DTO.request.authentication;

import com.hackathon.finservice.Util.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(@NotBlank String name,
                                  @Password String password,
                                  @NotBlank @Email String email) {
}
