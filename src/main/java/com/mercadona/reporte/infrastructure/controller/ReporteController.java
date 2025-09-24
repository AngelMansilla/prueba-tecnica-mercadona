package com.mercadona.reporte.infrastructure.controller;

import com.mercadona.reporte.application.port.ReporteService;
import com.mercadona.reporte.infrastructure.controller.dto.EstadoTiendaDto;
import com.mercadona.reporte.infrastructure.controller.dto.CoberturaHorasDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/tienda/{codigo}/estado")
    public ResponseEntity<EstadoTiendaDto> obtenerEstadoTienda(@PathVariable String codigo) {
        EstadoTiendaDto estado = reporteService.obtenerEstadoTienda(codigo);
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/tienda/{codigo}/cobertura")
    public ResponseEntity<CoberturaHorasDto> obtenerCoberturaHoras(@PathVariable String codigo) {
        CoberturaHorasDto cobertura = reporteService.obtenerCoberturaHoras(codigo);
        return ResponseEntity.ok(cobertura);
    }
}
