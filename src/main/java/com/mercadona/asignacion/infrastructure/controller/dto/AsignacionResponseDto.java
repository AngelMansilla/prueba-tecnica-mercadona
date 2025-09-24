package com.mercadona.asignacion.infrastructure.controller.dto;

public record AsignacionResponseDto(
    String dniTrabajador,
    String nombreTrabajador,
    String nombreSeccion,
    Integer horasAsignadas,
    String codigoTienda
) {
}
