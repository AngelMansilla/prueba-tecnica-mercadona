package com.mercadona.trabajador.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record TrabajadorDto(
    @NotBlank(message = "El DNI/NIE es obligatorio")
    @Size(min = 9, max = 9, message = "El DNI/NIE debe tener exactamente 9 caracteres")
    String dni,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String nombre,
    
    @NotNull(message = "Las horas disponibles son obligatorias")
    @Positive(message = "Las horas disponibles deben ser positivas")
    @Max(value = 8, message = "Las horas disponibles no pueden exceder 8")
    Integer horasDisponibles,
    
    @NotBlank(message = "El código de tienda es obligatorio")
    String codigoTienda
) {
}
