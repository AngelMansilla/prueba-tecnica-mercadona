package com.mercadona.shared.infrastructure.controller.dto;

import java.time.LocalDateTime;

public record EliminacionResponseDto(
        String mensaje,
        String entidad,
        String identificador,
        LocalDateTime timestamp
) {
    public static EliminacionResponseDto crear(String entidad, String identificador) {
        String mensaje = String.format("%s con identificador '%s' eliminado exitosamente", entidad, identificador);
        return new EliminacionResponseDto(mensaje, entidad, identificador, LocalDateTime.now());
    }
}
