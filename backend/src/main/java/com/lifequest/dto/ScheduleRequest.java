package com.lifequest.dto;

import com.lifequest.enums.RecurrenceType;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record ScheduleRequest(
    @NotNull(message = "ID da atividade é obrigatório")
    Long activityId,

    @NotNull(message = "Tipo de recorrência é obrigatório")
    RecurrenceType recurrenceType,

    Set<DayOfWeek> daysOfWeek,
    Set<Integer> daysOfMonth,

    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,
    
    LocalDate endDate
) {}