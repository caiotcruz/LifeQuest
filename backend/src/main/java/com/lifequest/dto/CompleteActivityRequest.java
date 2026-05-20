package com.lifequest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CompleteActivityRequest(
    @NotNull(message = "ID da atividade obrigatório")
    Long activityId,

    LocalDate completedDate,        // null = hoje

    @Min(value = 1, message = "Duração deve ser positiva")
    Integer durationMinutes,

    String notes
) {}