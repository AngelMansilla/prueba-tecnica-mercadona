package com.mercadona.reporte.infrastructure.controller.dto;

public record SeccionCoberturaDto(
        String nombreSeccion,
        int horasNecesarias,
        int horasAsignadas,
        int horasFaltantes
) {}
