package com.mercadona.trabajador.infrastructure.controller;

import com.mercadona.tienda.domain.Tienda;
import com.mercadona.trabajador.application.port.TrabajadorService;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.trabajador.infrastructure.controller.dto.TrabajadorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrabajadorController.class)
class TrabajadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrabajadorService trabajadorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaCrearTrabajadorCuandoDatosValidos() throws Exception {
        // Given
        TrabajadorDto trabajadorDto = new TrabajadorDto("12345678Z", "Juan Perez", 8, "T001");
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajadorCreado = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        
        when(trabajadorService.crearTrabajador("12345678Z", "Juan Perez", 8, "T001"))
            .thenReturn(trabajadorCreado);
        
        // When & Then
        mockMvc.perform(post("/api/trabajadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trabajadorDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("12345678Z"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.horasDisponibles").value(8))
                .andExpect(jsonPath("$.codigoTienda").value("T001"));
    }

    @Test
    void deberiaFallarAlCrearTrabajadorCuandoDniVacio() throws Exception {
        // Given
        TrabajadorDto trabajadorDto = new TrabajadorDto("", "Juan Perez", 8, "T001");
        
        // When & Then
        mockMvc.perform(post("/api/trabajadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trabajadorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaFallarAlCrearTrabajadorCuandoDniInvalido() throws Exception {
        // Given
        TrabajadorDto trabajadorDto = new TrabajadorDto("12345678", "Juan Perez", 8, "T001");
        
        // When & Then
        mockMvc.perform(post("/api/trabajadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trabajadorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaFallarAlCrearTrabajadorCuandoHorasInvalidas() throws Exception {
        // Given
        TrabajadorDto trabajadorDto = new TrabajadorDto("12345678Z", "Juan Perez", 10, "T001");
        
        // When & Then
        mockMvc.perform(post("/api/trabajadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trabajadorDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaBuscarTrabajadorPorDni() throws Exception {
        // Given
        String dni = "12345678Z";
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        
        when(trabajadorService.buscarPorDni(dni)).thenReturn(Optional.of(trabajador));
        
        // When & Then
        mockMvc.perform(get("/api/trabajadores/{dni}", dni))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678Z"))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.horasDisponibles").value(8));
    }

    @Test
    void deberiaRetornar404CuandoTrabajadorNoExiste() throws Exception {
        // Given
        String dni = "INEXISTENTE";
        
        when(trabajadorService.buscarPorDni(dni)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/trabajadores/{dni}", dni))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaBuscarTrabajadoresPorTienda() throws Exception {
        // Given
        String codigoTienda = "T001";
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador1 = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Trabajador trabajador2 = new Trabajador("87654321X", "Maria Garcia", 6, tienda);
        
        when(trabajadorService.buscarPorTienda(codigoTienda))
            .thenReturn(Arrays.asList(trabajador1, trabajador2));
        
        // When & Then
        mockMvc.perform(get("/api/trabajadores").param("tienda", codigoTienda))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].dni").value("12345678Z"))
                .andExpect(jsonPath("$[1].dni").value("87654321X"));
    }

    @Test
    void deberiaListarTodosTrabajadores() throws Exception {
        // Given
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador1 = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Trabajador trabajador2 = new Trabajador("87654321X", "Maria Garcia", 6, tienda);
        
        when(trabajadorService.buscarPorNombre(""))
            .thenReturn(Arrays.asList(trabajador1, trabajador2));
        
        // When & Then
        mockMvc.perform(get("/api/trabajadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deberiaBuscarTrabajadoresPorNombre() throws Exception {
        // Given
        String nombre = "Juan";
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        
        when(trabajadorService.buscarPorNombre(nombre))
            .thenReturn(Arrays.asList(trabajador));
        
        // When & Then
        mockMvc.perform(get("/api/trabajadores").param("nombre", nombre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dni").value("12345678Z"));
    }
}
