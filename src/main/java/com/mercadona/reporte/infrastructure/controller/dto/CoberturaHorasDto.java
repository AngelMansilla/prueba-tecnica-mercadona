package com.mercadona.reporte.infrastructure.controller.dto;

import java.util.List;

public record CoberturaHorasDto(
        String codigoTienda,
        String nombreTienda,
        String direccion,
        List<SeccionCoberturaDto> seccionesIncompletas,
        int totalSeccionesIncompletas,
        int totalHorasFaltantes
) {}
