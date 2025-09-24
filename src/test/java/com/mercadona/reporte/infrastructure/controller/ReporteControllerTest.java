package com.mercadona.reporte.infrastructure.controller;

import com.mercadona.reporte.application.port.ReporteService;
import com.mercadona.reporte.infrastructure.controller.dto.EstadoTiendaDto;
import com.mercadona.reporte.infrastructure.controller.dto.CoberturaHorasDto;
import com.mercadona.reporte.infrastructure.controller.dto.SeccionEstadoDto;
import com.mercadona.reporte.infrastructure.controller.dto.SeccionCoberturaDto;
import com.mercadona.reporte.infrastructure.controller.dto.TrabajadorAsignadoDto;
import com.mercadona.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
@Import(GlobalExceptionHandler.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    void deberiaObtenerEstadoTienda() throws Exception {
        // Given
        String codigoTienda = "T001";
        
        TrabajadorAsignadoDto trabajador1 = new TrabajadorAsignadoDto("12345678Z", "Juan Pérez", 6);
        TrabajadorAsignadoDto trabajador2 = new TrabajadorAsignadoDto("87654321X", "María García", 2);
        
        SeccionEstadoDto seccionHorno = new SeccionEstadoDto("Horno", List.of(trabajador1, trabajador2));
        
        EstadoTiendaDto estadoEsperado = new EstadoTiendaDto(
            "T001",
            "Tienda Centro",
            "Calle Falsa 123",
            List.of(seccionHorno)
        );
        
        when(reporteService.obtenerEstadoTienda(codigoTienda)).thenReturn(estadoEsperado);

        // When & Then
        mockMvc.perform(get("/api/reportes/tienda/{codigo}/estado", codigoTienda))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoTienda").value("T001"))
                .andExpect(jsonPath("$.nombreTienda").value("Tienda Centro"))
                .andExpect(jsonPath("$.direccion").value("Calle Falsa 123"))
                .andExpect(jsonPath("$.secciones").isArray())
                .andExpect(jsonPath("$.secciones[0].nombreSeccion").value("Horno"))
                .andExpect(jsonPath("$.secciones[0].trabajadores").isArray())
                .andExpect(jsonPath("$.secciones[0].trabajadores[0].dni").value("12345678Z"))
                .andExpect(jsonPath("$.secciones[0].trabajadores[0].nombreTrabajador").value("Juan Pérez"))
                .andExpect(jsonPath("$.secciones[0].trabajadores[0].horasAsignadas").value(6))
                .andExpect(jsonPath("$.secciones[0].trabajadores[1].dni").value("87654321X"))
                .andExpect(jsonPath("$.secciones[0].trabajadores[1].nombreTrabajador").value("María García"))
                .andExpect(jsonPath("$.secciones[0].trabajadores[1].horasAsignadas").value(2));
    }

    @Test
    void deberiaObtenerCoberturaHoras() throws Exception {
        // Given
        String codigoTienda = "T001";
        
        SeccionCoberturaDto pescaderia = new SeccionCoberturaDto("Pescadería", 16, 8, 8);
        SeccionCoberturaDto verduras = new SeccionCoberturaDto("Verduras", 16, 4, 12);
        
        CoberturaHorasDto coberturaEsperada = new CoberturaHorasDto(
            "T001",
            "Tienda Centro",
            "Calle Falsa 123",
            List.of(pescaderia, verduras),
            2,
            20
        );
        
        when(reporteService.obtenerCoberturaHoras(codigoTienda)).thenReturn(coberturaEsperada);

        // When & Then
        mockMvc.perform(get("/api/reportes/tienda/{codigo}/cobertura", codigoTienda))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoTienda").value("T001"))
                .andExpect(jsonPath("$.nombreTienda").value("Tienda Centro"))
                .andExpect(jsonPath("$.direccion").value("Calle Falsa 123"))
                .andExpect(jsonPath("$.seccionesIncompletas").isArray())
                .andExpect(jsonPath("$.seccionesIncompletas[0].nombreSeccion").value("Pescadería"))
                .andExpect(jsonPath("$.seccionesIncompletas[0].horasNecesarias").value(16))
                .andExpect(jsonPath("$.seccionesIncompletas[0].horasAsignadas").value(8))
                .andExpect(jsonPath("$.seccionesIncompletas[0].horasFaltantes").value(8))
                .andExpect(jsonPath("$.seccionesIncompletas[1].nombreSeccion").value("Verduras"))
                .andExpect(jsonPath("$.seccionesIncompletas[1].horasNecesarias").value(16))
                .andExpect(jsonPath("$.seccionesIncompletas[1].horasAsignadas").value(4))
                .andExpect(jsonPath("$.seccionesIncompletas[1].horasFaltantes").value(12))
                .andExpect(jsonPath("$.totalSeccionesIncompletas").value(2))
                .andExpect(jsonPath("$.totalHorasFaltantes").value(20));
    }

    @Test
    void deberiaFallarCuandoTiendaNoExisteEnEstado() throws Exception {
        // Given
        String codigoTiendaInexistente = "T999";
        
        when(reporteService.obtenerEstadoTienda(codigoTiendaInexistente))
            .thenThrow(new IllegalArgumentException("No se encontró la tienda con código: T999"));

        // When & Then
        mockMvc.perform(get("/api/reportes/tienda/{codigo}/estado", codigoTiendaInexistente))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titulo").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró la tienda con código: T999"));
    }

    @Test
    void deberiaFallarCuandoTiendaNoExisteEnCobertura() throws Exception {
        // Given
        String codigoTiendaInexistente = "T999";
        
        when(reporteService.obtenerCoberturaHoras(codigoTiendaInexistente))
            .thenThrow(new IllegalArgumentException("No se encontró la tienda con código: T999"));

        // When & Then
        mockMvc.perform(get("/api/reportes/tienda/{codigo}/cobertura", codigoTiendaInexistente))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titulo").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró la tienda con código: T999"));
    }
}
