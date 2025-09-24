package com.mercadona.tienda.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TiendaDto(
    @NotBlank(message = "El código de tienda es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    String codigo,
    
    @NotBlank(message = "El nombre de tienda es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String nombre
) {
}
