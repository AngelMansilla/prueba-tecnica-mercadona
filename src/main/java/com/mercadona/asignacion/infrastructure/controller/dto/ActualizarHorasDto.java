package com.mercadona.asignacion.infrastructure.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ActualizarHorasDto(
        @Min(value = 1, message = "Las horas asignadas deben ser al menos 1")
        @Max(value = 8, message = "Las horas asignadas no pueden superar 8")
        int nuevasHoras
) {}
