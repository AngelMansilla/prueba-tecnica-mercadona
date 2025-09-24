package com.mercadona.asignacion.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;

public record AsignacionDto(
    @NotBlank(message = "El DNI del trabajador es obligatorio")
    String dniTrabajador,
    
    @NotBlank(message = "El nombre de la sección es obligatorio")
    String nombreSeccion,
    
    @NotNull(message = "Las horas asignadas son obligatorias")
    @Positive(message = "Las horas asignadas deben ser positivas")
    @Max(value = 8, message = "Las horas asignadas no pueden exceder 8")
    Integer horasAsignadas
) {
}
