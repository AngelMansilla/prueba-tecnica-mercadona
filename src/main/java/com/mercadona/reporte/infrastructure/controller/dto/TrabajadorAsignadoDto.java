package com.mercadona.reporte.infrastructure.controller.dto;

public record TrabajadorAsignadoDto(
        String dni,
        String nombreTrabajador,
        int horasAsignadas
) {}
