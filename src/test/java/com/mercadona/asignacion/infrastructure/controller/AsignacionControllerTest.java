package com.mercadona.asignacion.infrastructure.controller;

import com.mercadona.asignacion.application.port.AsignacionService;
import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.asignacion.infrastructure.controller.dto.AsignacionDto;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.trabajador.domain.Trabajador;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AsignacionController.class)
class AsignacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsignacionService asignacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaCrearAsignacionCuandoDatosValidos() throws Exception {
        // Given
        AsignacionDto asignacionDto = new AsignacionDto("12345678Z", "Horno", 4);
        
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        Asignacion asignacionCreada = new Asignacion(trabajador, seccion, 4);
        
        when(asignacionService.crearAsignacion("12345678Z", "Horno", 4))
            .thenReturn(asignacionCreada);
        
        // When & Then
        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dniTrabajador").value("12345678Z"))
                .andExpect(jsonPath("$.nombreTrabajador").value("Juan Perez"))
                .andExpect(jsonPath("$.nombreSeccion").value("Horno"))
                .andExpect(jsonPath("$.horasAsignadas").value(4))
                .andExpect(jsonPath("$.codigoTienda").value("T001"));
    }

    @Test
    void deberiaFallarAlCrearAsignacionCuandoDniVacio() throws Exception {
        // Given
        AsignacionDto asignacionDto = new AsignacionDto("", "Horno", 4);
        
        // When & Then
        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaFallarAlCrearAsignacionCuandoHorasInvalidas() throws Exception {
        // Given
        AsignacionDto asignacionDto = new AsignacionDto("12345678Z", "Horno", 10);
        
        // When & Then
        mockMvc.perform(post("/api/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaBuscarAsignacionesPorTrabajador() throws Exception {
        // Given
        String dni = "12345678Z";
        
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        Asignacion asignacion = new Asignacion(trabajador, seccion, 4);
        
        when(asignacionService.buscarAsignacionesPorTrabajador(dni))
            .thenReturn(Arrays.asList(asignacion));
        
        // When & Then
        mockMvc.perform(get("/api/asignaciones/trabajador/{dni}", dni))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dniTrabajador").value("12345678Z"))
                .andExpect(jsonPath("$[0].nombreSeccion").value("Horno"));
    }

    @Test
    void deberiaBuscarAsignacionesPorSeccion() throws Exception {
        // Given
        String nombreSeccion = "Horno";
        
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        Asignacion asignacion = new Asignacion(trabajador, seccion, 4);
        
        when(asignacionService.buscarAsignacionesPorSeccion(nombreSeccion))
            .thenReturn(Arrays.asList(asignacion));
        
        // When & Then
        mockMvc.perform(get("/api/asignaciones/seccion/{nombreSeccion}", nombreSeccion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dniTrabajador").value("12345678Z"))
                .andExpect(jsonPath("$[0].nombreSeccion").value("Horno"));
    }

    @Test
    void deberiaBuscarAsignacionesPorTienda() throws Exception {
        // Given
        String codigoTienda = "T001";
        
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        Asignacion asignacion = new Asignacion(trabajador, seccion, 4);
        
        when(asignacionService.buscarAsignacionesPorTienda(codigoTienda))
            .thenReturn(Arrays.asList(asignacion));
        
        // When & Then
        mockMvc.perform(get("/api/asignaciones/tienda/{codigoTienda}", codigoTienda))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dniTrabajador").value("12345678Z"));
    }

    @Test
    void deberiaEliminarAsignacion() throws Exception {
        // Given
        String dni = "12345678Z";
        String seccion = "Horno";
        
        // When & Then
        mockMvc.perform(delete("/api/asignaciones/trabajador/{dni}/seccion/{seccion}", dni, seccion))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaCalcularHorasAsignadasPorTienda() throws Exception {
        // Given
        String codigoTienda = "T001";
        
        when(asignacionService.calcularHorasTotalesAsignadasPorTienda(codigoTienda))
            .thenReturn(12);
        
        // When & Then
        mockMvc.perform(get("/api/asignaciones/tienda/{codigoTienda}/horas", codigoTienda))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoTienda").value("T001"))
                .andExpect(jsonPath("$.horasTotales").value(12));
    }

    @Test
    void deberiaCalcularHorasAsignadasPorSeccion() throws Exception {
        // Given
        String nombreSeccion = "Horno";
        
        when(asignacionService.calcularHorasTotalesAsignadasPorSeccion(nombreSeccion))
            .thenReturn(8);
        
        // When & Then
        mockMvc.perform(get("/api/asignaciones/seccion/{nombreSeccion}/horas", nombreSeccion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreSeccion").value("Horno"))
                .andExpect(jsonPath("$.horasTotales").value(8));
    }
}
