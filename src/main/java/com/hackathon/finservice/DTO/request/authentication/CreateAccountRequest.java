package com.hackathon.finservice.DTO.request.authentication;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(@NotBlank String accountNumber,
                                   @NotBlank String accountType) {}
