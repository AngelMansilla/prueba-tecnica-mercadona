package com.mercadona.asignacion.application.service;

import com.mercadona.asignacion.application.port.AsignacionService;
import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.asignacion.infrastructure.repository.AsignacionRepository;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.trabajador.infrastructure.repository.TrabajadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;


    @Mock
    private com.mercadona.tienda.infrastructure.repository.SeccionRepository seccionRepository;

    private AsignacionService asignacionService;

    private Tienda tiendaMock;
    private Trabajador trabajadorMock;
    private Seccion seccionMock;

    @BeforeEach
    void setUp() {
        asignacionService = new AsignacionServiceImpl(asignacionRepository, trabajadorRepository, seccionRepository);
        
        tiendaMock = new Tienda("T001", "Tienda Centro");
        trabajadorMock = new Trabajador("12345678Z", "Juan Perez", 8, tiendaMock);
        seccionMock = new Seccion("Horno", 8);
    }

    @Test
    void deberiaCrearAsignacionCuandoDatosValidos() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        int horasAsignadas = 4;
        
        Asignacion asignacionEsperada = new Asignacion(trabajadorMock, seccionMock, horasAsignadas);
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.existsByTrabajadorAndSeccion(trabajadorMock, seccionMock)).thenReturn(false);
        when(asignacionRepository.save(any(Asignacion.class))).thenReturn(asignacionEsperada);
        
        // When
        Asignacion resultado = asignacionService.crearAsignacion(dniTrabajador, nombreSeccion, horasAsignadas);
        
        // Then
        assertNotNull(resultado);
        assertEquals(trabajadorMock.getDni(), resultado.getTrabajador().getDni());
        assertEquals(seccionMock.getNombre(), resultado.getSeccion().getNombre());
        assertEquals(horasAsignadas, resultado.getHorasAsignadas());
        
        verify(trabajadorRepository).findByDni(dniTrabajador);
        verify(asignacionRepository).existsByTrabajadorAndSeccion(trabajadorMock, seccionMock);
        verify(asignacionRepository).save(any(Asignacion.class));
    }

    @Test
    void deberiaFallarAlCrearAsignacionCuandoTrabajadorNoExiste() {
        // Given
        String dniTrabajador = "INEXISTENTE";
        String nombreSeccion = "Horno";
        int horasAsignadas = 4;
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> asignacionService.crearAsignacion(dniTrabajador, nombreSeccion, horasAsignadas)
        );
        
        assertEquals("No existe un trabajador con el DNI: INEXISTENTE", exception.getMessage());
        verify(trabajadorRepository).findByDni(dniTrabajador);
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void deberiaFallarAlCrearAsignacionCuandoSeccionNoExiste() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "INEXISTENTE";
        int horasAsignadas = 4;
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> asignacionService.crearAsignacion(dniTrabajador, nombreSeccion, horasAsignadas)
        );
        
        assertEquals("No existe una sección con el nombre: INEXISTENTE", exception.getMessage());
        verify(trabajadorRepository).findByDni(dniTrabajador);
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void deberiaFallarAlCrearAsignacionCuandoYaExiste() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        int horasAsignadas = 4;
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.existsByTrabajadorAndSeccion(trabajadorMock, seccionMock)).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> asignacionService.crearAsignacion(dniTrabajador, nombreSeccion, horasAsignadas)
        );
        
        assertEquals("Ya existe una asignación para el trabajador 12345678Z en la sección Horno", exception.getMessage());
        verify(asignacionRepository).existsByTrabajadorAndSeccion(trabajadorMock, seccionMock);
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void deberiaFallarAlCrearAsignacionCuandoHorasExcedenDisponibles() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        int horasAsignadas = 10; // Más de las 8 horas disponibles del trabajador
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.existsByTrabajadorAndSeccion(trabajadorMock, seccionMock)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> asignacionService.crearAsignacion(dniTrabajador, nombreSeccion, horasAsignadas)
        );
        
        assertTrue(exception.getMessage().contains("Las horas asignadas (10) no pueden superar las horas disponibles del trabajador (8)"));
        verify(asignacionRepository, never()).save(any());
    }

    @Test
    void deberiaBuscarAsignacion() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        
        Asignacion asignacionEsperada = new Asignacion(trabajadorMock, seccionMock, 4);
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.findByTrabajadorAndSeccion(trabajadorMock, seccionMock))
            .thenReturn(Optional.of(asignacionEsperada));
        
        // When
        Optional<Asignacion> resultado = asignacionService.buscarAsignacion(dniTrabajador, nombreSeccion);
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals(asignacionEsperada, resultado.get());
    }

    @Test
    void deberiaBuscarAsignacionesPorTrabajador() {
        // Given
        String dniTrabajador = "12345678Z";
        List<Asignacion> asignacionesEsperadas = Arrays.asList(
            new Asignacion(trabajadorMock, seccionMock, 4)
        );
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(asignacionRepository.findByTrabajador(trabajadorMock)).thenReturn(asignacionesEsperadas);
        
        // When
        List<Asignacion> resultado = asignacionService.buscarAsignacionesPorTrabajador(dniTrabajador);
        
        // Then
        assertEquals(1, resultado.size());
        assertEquals(asignacionesEsperadas, resultado);
    }

    @Test
    void deberiaBuscarAsignacionesPorSeccion() {
        // Given
        String nombreSeccion = "Horno";
        List<Asignacion> asignacionesEsperadas = Arrays.asList(
            new Asignacion(trabajadorMock, seccionMock, 4)
        );
        
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.findBySeccion(seccionMock)).thenReturn(asignacionesEsperadas);
        
        // When
        List<Asignacion> resultado = asignacionService.buscarAsignacionesPorSeccion(nombreSeccion);
        
        // Then
        assertEquals(1, resultado.size());
        assertEquals(asignacionesEsperadas, resultado);
    }

    @Test
    void deberiaBuscarAsignacionesPorTienda() {
        // Given
        String codigoTienda = "T001";
        List<Asignacion> asignacionesEsperadas = Arrays.asList(
            new Asignacion(trabajadorMock, seccionMock, 4)
        );
        
        when(asignacionRepository.findByCodigoTienda(codigoTienda)).thenReturn(asignacionesEsperadas);
        
        // When
        List<Asignacion> resultado = asignacionService.buscarAsignacionesPorTienda(codigoTienda);
        
        // Then
        assertEquals(1, resultado.size());
        assertEquals(asignacionesEsperadas, resultado);
    }

    @Test
    void deberiaCalcularHorasTotalesAsignadasPorTienda() {
        // Given
        String codigoTienda = "T001";
        Integer horasEsperadas = 12;
        
        when(asignacionRepository.sumHorasAsignadasByCodigoTienda(codigoTienda)).thenReturn(horasEsperadas);
        
        // When
        Integer resultado = asignacionService.calcularHorasTotalesAsignadasPorTienda(codigoTienda);
        
        // Then
        assertEquals(horasEsperadas, resultado);
        verify(asignacionRepository).sumHorasAsignadasByCodigoTienda(codigoTienda);
    }

    @Test
    void deberiaCalcularHorasTotalesAsignadasPorSeccion() {
        // Given
        String nombreSeccion = "Horno";
        Integer horasEsperadas = 8;
        
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.sumHorasAsignadasBySeccion(seccionMock)).thenReturn(horasEsperadas);
        
        // When
        Integer resultado = asignacionService.calcularHorasTotalesAsignadasPorSeccion(nombreSeccion);
        
        // Then
        assertEquals(horasEsperadas, resultado);
        verify(asignacionRepository).sumHorasAsignadasBySeccion(seccionMock);
    }

    @Test
    void deberiaEliminarAsignacion() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        
        Asignacion asignacionExistente = new Asignacion(trabajadorMock, seccionMock, 4);
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.findByTrabajadorAndSeccion(trabajadorMock, seccionMock))
            .thenReturn(Optional.of(asignacionExistente));
        
        // When
        asignacionService.eliminarAsignacion(dniTrabajador, nombreSeccion);
        
        // Then
        verify(asignacionRepository).deleteById(asignacionExistente.getId());
    }

    @Test
    void deberiaFallarAlEliminarAsignacionInexistente() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.findByTrabajadorAndSeccion(trabajadorMock, seccionMock))
            .thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> asignacionService.eliminarAsignacion(dniTrabajador, nombreSeccion)
        );
        
        assertEquals("No existe una asignación para el trabajador 12345678Z en la sección Horno", exception.getMessage());
        verify(asignacionRepository, never()).deleteById(any());
    }

    @Test
    void deberiaBuscarAsignacionesConHorasMinimas() {
        // Given
        int horasMinimas = 6;
        List<Asignacion> asignacionesEsperadas = Arrays.asList(
            new Asignacion(trabajadorMock, seccionMock, 8)
        );
        
        when(asignacionRepository.findByHorasAsignadasGreaterThanEqual(horasMinimas)).thenReturn(asignacionesEsperadas);
        
        // When
        List<Asignacion> resultado = asignacionService.buscarAsignacionesConHorasMinimas(horasMinimas);
        
        // Then
        assertEquals(1, resultado.size());
        assertEquals(asignacionesEsperadas, resultado);
        verify(asignacionRepository).findByHorasAsignadasGreaterThanEqual(horasMinimas);
    }

    @Test
    void deberiaContarAsignacionesPorTrabajador() {
        // Given
        String dniTrabajador = "12345678Z";
        Long conteoEsperado = 3L;
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(asignacionRepository.countByTrabajador(trabajadorMock)).thenReturn(conteoEsperado);
        
        // When
        Long resultado = asignacionService.contarAsignacionesPorTrabajador(dniTrabajador);
        
        // Then
        assertEquals(conteoEsperado, resultado);
        verify(asignacionRepository).countByTrabajador(trabajadorMock);
    }

    @Test
    void deberiaContarAsignacionesPorSeccion() {
        // Given
        String nombreSeccion = "Horno";
        Long conteoEsperado = 5L;
        
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.countBySeccion(seccionMock)).thenReturn(conteoEsperado);
        
        // When
        Long resultado = asignacionService.contarAsignacionesPorSeccion(nombreSeccion);
        
        // Then
        assertEquals(conteoEsperado, resultado);
        verify(asignacionRepository).countBySeccion(seccionMock);
    }

    @Test
    void deberiaVerificarSiExisteAsignacion() {
        // Given
        String dniTrabajador = "12345678Z";
        String nombreSeccion = "Horno";
        
        when(trabajadorRepository.findByDni(dniTrabajador)).thenReturn(Optional.of(trabajadorMock));
        when(seccionRepository.findByNombre(nombreSeccion)).thenReturn(Optional.of(seccionMock));
        when(asignacionRepository.existsByTrabajadorAndSeccion(trabajadorMock, seccionMock)).thenReturn(true);
        
        // When
        boolean resultado = asignacionService.existeAsignacion(dniTrabajador, nombreSeccion);
        
        // Then
        assertTrue(resultado);
        verify(asignacionRepository).existsByTrabajadorAndSeccion(trabajadorMock, seccionMock);
    }

}
