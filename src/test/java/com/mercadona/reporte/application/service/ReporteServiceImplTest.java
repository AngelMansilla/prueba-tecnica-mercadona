package com.mercadona.reporte.application.service;

import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.asignacion.infrastructure.repository.AsignacionRepository;
import com.mercadona.reporte.application.port.ReporteService;
import com.mercadona.reporte.infrastructure.controller.dto.EstadoTiendaDto;
import com.mercadona.reporte.infrastructure.controller.dto.CoberturaHorasDto;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.external.port.ExternalStoreService;
import com.mercadona.external.dto.ExternalStoreDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private TiendaRepository tiendaRepository;
    
    @Mock
    private AsignacionRepository asignacionRepository;
    
    @Mock
    private ExternalStoreService externalStoreService;

    private ReporteService reporteService;

    private Tienda tienda;
    private Seccion seccionHorno;
    private Seccion seccionPescaderia;
    private Trabajador trabajador1;
    private Trabajador trabajador2;

    @BeforeEach
    void setUp() {
        reporteService = new ReporteServiceImpl(tiendaRepository, asignacionRepository, externalStoreService);
        
        // Datos de prueba
        tienda = new Tienda("T001", "Tienda Centro");
        seccionHorno = new Seccion("Horno", 8);
        seccionPescaderia = new Seccion("Pescadería", 16);
        trabajador1 = new Trabajador("12345678Z", "Juan Pérez", 8, tienda);
        trabajador2 = new Trabajador("87654321X", "María García", 8, tienda);
    }

    @Test
    void deberiaObtenerEstadoTiendaConTrabajadoresPorSeccion() {
        // Given
        String codigoTienda = "T001";
        
        Asignacion asignacion1 = new Asignacion(trabajador1, seccionHorno, 6);
        Asignacion asignacion2 = new Asignacion(trabajador2, seccionHorno, 2);
        Asignacion asignacion3 = new Asignacion(trabajador1, seccionPescaderia, 2);
        
        List<Asignacion> asignaciones = Arrays.asList(asignacion1, asignacion2, asignacion3);
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tienda));
        when(asignacionRepository.findByCodigoTienda(codigoTienda)).thenReturn(asignaciones);
        when(externalStoreService.buscarTiendaPorNombre("Tienda Centro"))
            .thenReturn(Optional.of(new ExternalStoreDto(1L, "Tienda Centro", "Calle Falsa 123", "Madrid")));
        
        // When
        EstadoTiendaDto resultado = reporteService.obtenerEstadoTienda(codigoTienda);
        
        // Then
        assertNotNull(resultado);
        assertEquals("T001", resultado.codigoTienda());
        assertEquals("Tienda Centro", resultado.nombreTienda());
        assertEquals("Calle Falsa 123", resultado.direccion());
        assertEquals(2, resultado.secciones().size());
        
        // Verificar sección Horno - debe mostrar trabajadores
        var seccionHorno = resultado.secciones().stream()
            .filter(s -> s.nombreSeccion().equals("Horno"))
            .findFirst().orElseThrow();
        assertEquals("Horno", seccionHorno.nombreSeccion());
        assertEquals(2, seccionHorno.trabajadores().size());
        
        // Verificar trabajadores en Horno
        var trabajadoresHorno = seccionHorno.trabajadores();
        assertTrue(trabajadoresHorno.stream().anyMatch(t -> 
            t.dni().equals("12345678Z") && t.nombreTrabajador().equals("Juan Pérez") && t.horasAsignadas() == 6));
        assertTrue(trabajadoresHorno.stream().anyMatch(t -> 
            t.dni().equals("87654321X") && t.nombreTrabajador().equals("María García") && t.horasAsignadas() == 2));
        
        // Verificar sección Pescadería - debe mostrar trabajador
        var seccionPescaderia = resultado.secciones().stream()
            .filter(s -> s.nombreSeccion().equals("Pescadería"))
            .findFirst().orElseThrow();
        assertEquals("Pescadería", seccionPescaderia.nombreSeccion());
        assertEquals(1, seccionPescaderia.trabajadores().size());
        assertEquals("12345678Z", seccionPescaderia.trabajadores().get(0).dni());
        assertEquals(2, seccionPescaderia.trabajadores().get(0).horasAsignadas());
    }

    @Test
    void deberiaObtenerCoberturaHorasSoloSeccionesIncompletas() {
        // Given
        String codigoTienda = "T001";
        
        Asignacion asignacion1 = new Asignacion(trabajador1, seccionHorno, 8); // Horno completa (8/8)
        Asignacion asignacion2 = new Asignacion(trabajador2, seccionPescaderia, 8); // Pescadería incompleta (8/16)
        
        List<Asignacion> asignaciones = Arrays.asList(asignacion1, asignacion2);
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tienda));
        when(asignacionRepository.findByCodigoTienda(codigoTienda)).thenReturn(asignaciones);
        when(externalStoreService.buscarTiendaPorNombre("Tienda Centro"))
            .thenReturn(Optional.of(new ExternalStoreDto(1L, "Tienda Centro", "Calle Falsa 123", "Madrid")));
        
        // When
        CoberturaHorasDto resultado = reporteService.obtenerCoberturaHoras(codigoTienda);
        
        // Then
        assertNotNull(resultado);
        assertEquals("T001", resultado.codigoTienda());
        assertEquals("Tienda Centro", resultado.nombreTienda());
        assertEquals("Calle Falsa 123", resultado.direccion());
    
        assertEquals(4, resultado.seccionesIncompletas().size()); // Pescadería, Cajas, Verduras, Droguería
        
        assertTrue(resultado.seccionesIncompletas().stream().anyMatch(s -> s.nombreSeccion().equals("Pescadería")));
        assertTrue(resultado.seccionesIncompletas().stream().anyMatch(s -> s.nombreSeccion().equals("Cajas")));
        assertTrue(resultado.seccionesIncompletas().stream().anyMatch(s -> s.nombreSeccion().equals("Verduras")));
        assertTrue(resultado.seccionesIncompletas().stream().anyMatch(s -> s.nombreSeccion().equals("Droguería")));
        
        // Verificar Pescadería (tiene asignaciones pero incompleta)
        var pescaderia = resultado.seccionesIncompletas().stream()
            .filter(s -> s.nombreSeccion().equals("Pescadería"))
            .findFirst().orElseThrow();
        assertEquals(16, pescaderia.horasNecesarias());
        assertEquals(8, pescaderia.horasAsignadas());
        assertEquals(8, pescaderia.horasFaltantes());
        
        // Verificar Verduras (sin asignaciones)
        var verduras = resultado.seccionesIncompletas().stream()
            .filter(s -> s.nombreSeccion().equals("Verduras"))
            .findFirst().orElseThrow();
        assertEquals(16, verduras.horasNecesarias());
        assertEquals(0, verduras.horasAsignadas());
        assertEquals(16, verduras.horasFaltantes());
        
        // Verificar totales
        assertEquals(4, resultado.totalSeccionesIncompletas());
        assertEquals(56, resultado.totalHorasFaltantes()); 
    }

    @Test
    void deberiaFallarCuandoTiendaNoExisteEnEstado() {
        // Given
        String codigoTienda = "INEXISTENTE";
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reporteService.obtenerEstadoTienda(codigoTienda)
        );
        
        assertEquals("No se encontró la tienda con código: INEXISTENTE", exception.getMessage());
    }
    
    @Test
    void deberiaFallarCuandoTiendaNoExisteEnCobertura() {
        // Given
        String codigoTienda = "INEXISTENTE";
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reporteService.obtenerCoberturaHoras(codigoTienda)
        );
        
        assertEquals("No se encontró la tienda con código: INEXISTENTE", exception.getMessage());
    }
}
