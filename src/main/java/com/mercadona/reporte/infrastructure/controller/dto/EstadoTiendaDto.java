package com.mercadona.reporte.infrastructure.controller.dto;

import java.util.List;

public record EstadoTiendaDto(
        String codigoTienda,
        String nombreTienda,
        List<SeccionEstadoDto> secciones
) {}
