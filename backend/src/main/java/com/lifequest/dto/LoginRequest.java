package com.lifequest.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Email ou username obrigatório")
    String emailOrUsername,

    @NotBlank(message = "Senha obrigatória")
    String password
) {}