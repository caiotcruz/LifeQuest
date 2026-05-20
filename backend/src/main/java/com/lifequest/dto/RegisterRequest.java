package com.lifequest.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
    @NotBlank(message = "Username obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username só pode ter letras, números e underscore")
    String username,

    @NotBlank(message = "Email obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String password
) {}