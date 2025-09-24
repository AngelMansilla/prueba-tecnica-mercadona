package com.mercadona.reporte.application.port;

import com.mercadona.reporte.infrastructure.controller.dto.EstadoTiendaDto;
import com.mercadona.reporte.infrastructure.controller.dto.CoberturaHorasDto;

public interface ReporteService {

    EstadoTiendaDto obtenerEstadoTienda(String codigoTienda);

    CoberturaHorasDto obtenerCoberturaHoras(String codigoTienda);
}
