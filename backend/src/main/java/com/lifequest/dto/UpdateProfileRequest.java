package com.lifequest.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 3, max = 50, message = "O nome do personagem deve ter entre 3 e 50 caracteres.")
    String username,
    String avatar
) {}