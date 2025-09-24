package com.mercadona.reporte.infrastructure.controller.dto;

import java.util.List;

public record SeccionEstadoDto(
        String nombreSeccion,
        List<TrabajadorAsignadoDto> trabajadores
) {}
