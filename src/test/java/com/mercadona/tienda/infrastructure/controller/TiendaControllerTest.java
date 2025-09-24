package com.mercadona.tienda.infrastructure.controller;

import com.mercadona.tienda.application.port.TiendaService;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.controller.dto.TiendaDto;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TiendaController.class)
class TiendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TiendaService tiendaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaCrearTiendaCuandoDatosValidos() throws Exception {
        // Given
        TiendaDto tiendaDto = new TiendaDto("T001", "Tienda Centro");
        Tienda tiendaCreada = new Tienda("T001", "Tienda Centro");
        
        when(tiendaService.crearTienda("T001", "Tienda Centro")).thenReturn(tiendaCreada);
        
        // When & Then
        mockMvc.perform(post("/api/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("T001"))
                .andExpect(jsonPath("$.nombre").value("Tienda Centro"));
    }

    @Test
    void deberiaFallarAlCrearTiendaCuandoCodigoVacio() throws Exception {
        // Given
        TiendaDto tiendaDto = new TiendaDto("", "Tienda Centro");
        
        // When & Then
        mockMvc.perform(post("/api/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaFallarAlCrearTiendaCuandoNombreVacio() throws Exception {
        // Given
        TiendaDto tiendaDto = new TiendaDto("T001", "");
        
        // When & Then
        mockMvc.perform(post("/api/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaBuscarTiendaPorCodigo() throws Exception {
        // Given
        String codigo = "T001";
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        
        when(tiendaService.buscarPorCodigo(codigo)).thenReturn(Optional.of(tienda));
        
        // When & Then
        mockMvc.perform(get("/api/tiendas/{codigo}", codigo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("T001"))
                .andExpect(jsonPath("$.nombre").value("Tienda Centro"));
    }

    @Test
    void deberiaRetornar404CuandoTiendaNoExiste() throws Exception {
        // Given
        String codigo = "INEXISTENTE";
        
        when(tiendaService.buscarPorCodigo(codigo)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/tiendas/{codigo}", codigo))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaVerificarExistenciaDeTiendaUsandoBuscarTienda() throws Exception {
        // Given
        String codigo = "T001";
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        
        when(tiendaService.buscarPorCodigo(codigo)).thenReturn(Optional.of(tienda));
        
        // When & Then - 200 significa que existe
        mockMvc.perform(get("/api/tiendas/{codigo}", codigo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("T001"))
                .andExpect(jsonPath("$.nombre").value("Tienda Centro"));
    }

    @Test
    void deberiaListarTodasLasTiendas() throws Exception {
        // Given
        Tienda tienda1 = new Tienda("T001", "Tienda Centro");
        Tienda tienda2 = new Tienda("T002", "Tienda Norte");
        
        when(tiendaService.obtenerTodasLasTiendas()).thenReturn(Arrays.asList(tienda1, tienda2));
        
        // When & Then
        mockMvc.perform(get("/api/tiendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("T001"))
                .andExpect(jsonPath("$[1].codigo").value("T002"));
    }

    @Test
    void deberiaBuscarTiendasPorNombre() throws Exception {
        // Given
        String nombre = "Centro";
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        
        when(tiendaService.buscarPorNombre(nombre)).thenReturn(Arrays.asList(tienda));
        
        // When & Then
        mockMvc.perform(get("/api/tiendas").param("nombre", nombre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("T001"));
    }

    @Test
    void deberiaActualizarTiendaCuandoDatosValidos() throws Exception {
        // Given
        String codigo = "T001";
        TiendaDto tiendaDto = new TiendaDto("T001", "Tienda Centro Actualizada");
        Tienda tiendaActualizada = new Tienda("T001", "Tienda Centro Actualizada");
        
        when(tiendaService.actualizarTienda(codigo, "Tienda Centro Actualizada")).thenReturn(tiendaActualizada);
        
        // When & Then
        mockMvc.perform(put("/api/tiendas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("T001"))
                .andExpect(jsonPath("$.nombre").value("Tienda Centro Actualizada"));
    }

    @Test
    void deberiaFallarActualizarTiendaCuandoNombreVacio() throws Exception {
        // Given
        String codigo = "T001";
        TiendaDto tiendaDto = new TiendaDto("T001", "");
        
        // When & Then
        mockMvc.perform(put("/api/tiendas/{codigo}", codigo)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaEliminarTiendaCuandoExiste() throws Exception {
        // Given
        String codigo = "T001";
        
        doNothing().when(tiendaService).eliminarTienda(codigo);
        
        // When & Then
        mockMvc.perform(delete("/api/tiendas/{codigo}", codigo))
                .andExpect(status().isNoContent());
    }
}
