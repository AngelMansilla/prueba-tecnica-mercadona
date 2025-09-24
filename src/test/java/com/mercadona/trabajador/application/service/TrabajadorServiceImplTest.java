package com.mercadona.trabajador.application.service;

import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.application.port.TrabajadorService;
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
class TrabajadorServiceImplTest {

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private TiendaRepository tiendaRepository;

    private TrabajadorService trabajadorService;

    private Tienda tiendaMock;

    @BeforeEach
    void setUp() {
        trabajadorService = new TrabajadorServiceImpl(trabajadorRepository, tiendaRepository);
        tiendaMock = new Tienda("T001", "Tienda Centro");
    }

    @Test
    void deberiaCrearTrabajadorCuandoDatosValidos() {
        // Given
        String dni = "12345678Z";
        String nombre = "Juan Perez";
        int horasDisponibles = 8;
        String codigoTienda = "T001";
        
        Trabajador trabajadorEsperado = new Trabajador(dni, nombre, horasDisponibles, tiendaMock);
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tiendaMock));
        when(trabajadorRepository.existsByDni(dni)).thenReturn(false);
        when(trabajadorRepository.save(any(Trabajador.class))).thenReturn(trabajadorEsperado);
        
        // When
        Trabajador resultado = trabajadorService.crearTrabajador(dni, nombre, horasDisponibles, codigoTienda);
        
        // Then
        assertNotNull(resultado);
        assertEquals(dni, resultado.getDni());
        assertEquals(nombre, resultado.getNombre());
        assertEquals(horasDisponibles, resultado.getHorasDisponibles());
        assertEquals(tiendaMock.getId(), resultado.getTienda().getId());
        
        verify(tiendaRepository).findByCodigo(codigoTienda);
        verify(trabajadorRepository).existsByDni(dni);
        verify(trabajadorRepository).save(any(Trabajador.class));
    }

    @Test
    void deberiaFallarAlCrearTrabajadorCuandoTiendaNoExiste() {
        // Given
        String dni = "12345678Z";
        String nombre = "Juan Perez";
        int horasDisponibles = 8;
        String codigoTienda = "INEXISTENTE";
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> trabajadorService.crearTrabajador(dni, nombre, horasDisponibles, codigoTienda)
        );
        
        assertEquals("No existe una tienda con el código: INEXISTENTE", exception.getMessage());
        verify(tiendaRepository).findByCodigo(codigoTienda);
        verify(trabajadorRepository, never()).existsByDni(any());
        verify(trabajadorRepository, never()).save(any());
    }

    @Test
    void deberiaFallarAlCrearTrabajadorCuandoDniYaExiste() {
        // Given
        String dni = "12345678Z";
        String nombre = "Juan Perez";
        int horasDisponibles = 8;
        String codigoTienda = "T001";
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tiendaMock));
        when(trabajadorRepository.existsByDni(dni)).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> trabajadorService.crearTrabajador(dni, nombre, horasDisponibles, codigoTienda)
        );
        
        assertEquals("Ya existe un trabajador con el DNI: 12345678Z", exception.getMessage());
        verify(tiendaRepository).findByCodigo(codigoTienda);
        verify(trabajadorRepository).existsByDni(dni);
        verify(trabajadorRepository, never()).save(any());
    }

    @Test
    void deberiaFallarAlCrearTrabajadorCuandoDniInvalido() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> trabajadorService.crearTrabajador("INVALID", "Juan", 8, "T001")
        );
        
        assertEquals("El documento debe tener formato válido (DNI: 8 dígitos + letra, NIE: X/Y/Z + 7 dígitos + letra)", exception.getMessage());
        verify(tiendaRepository, never()).findByCodigo(any());
        verify(trabajadorRepository, never()).existsByDni(any());
        verify(trabajadorRepository, never()).save(any());
    }

    @Test
    void deberiaAceptarNieValido() {
        // Given
        String nie = "X1234567L";
        String nombre = "Ana Garcia";
        int horasDisponibles = 6;
        String codigoTienda = "T001";
        
        Trabajador trabajadorEsperado = new Trabajador(nie, nombre, horasDisponibles, tiendaMock);
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tiendaMock));
        when(trabajadorRepository.existsByDni(nie)).thenReturn(false);
        when(trabajadorRepository.save(any(Trabajador.class))).thenReturn(trabajadorEsperado);
        
        // When
        Trabajador resultado = trabajadorService.crearTrabajador(nie, nombre, horasDisponibles, codigoTienda);
        
        // Then
        assertNotNull(resultado);
        assertEquals(nie, resultado.getDni());
    }

    @Test
    void deberiaBuscarTrabajadorPorDni() {
        // Given
        String dni = "12345678Z";
        Trabajador trabajadorEsperado = new Trabajador(dni, "Juan Perez", 8, tiendaMock);
        
        when(trabajadorRepository.findByDni(dni)).thenReturn(Optional.of(trabajadorEsperado));
        
        // When
        Optional<Trabajador> resultado = trabajadorService.buscarPorDni(dni);
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals(dni, resultado.get().getDni());
        verify(trabajadorRepository).findByDni(dni);
    }

    @Test
    void deberiaBuscarTrabajadoresPorTienda() {
        // Given
        String codigoTienda = "T001";
        List<Trabajador> trabajadoresEsperados = Arrays.asList(
            new Trabajador("12345678Z", "Juan Perez", 8, tiendaMock),
            new Trabajador("87654321X", "Ana Garcia", 6, tiendaMock)
        );
        
        when(trabajadorRepository.findByCodigoTienda(codigoTienda)).thenReturn(trabajadoresEsperados);
        
        // When
        List<Trabajador> resultado = trabajadorService.buscarPorTienda(codigoTienda);
        
        // Then
        assertEquals(2, resultado.size());
        verify(trabajadorRepository).findByCodigoTienda(codigoTienda);
    }

    @Test
    void deberiaBuscarTrabajadoresPorNombre() {
        // Given
        String nombre = "Juan";
        List<Trabajador> trabajadoresEsperados = Arrays.asList(
            new Trabajador("12345678Z", "Juan Perez", 8, tiendaMock),
            new Trabajador("87654321X", "Juan Carlos", 6, tiendaMock)
        );
        
        when(trabajadorRepository.findByNombreContainingIgnoreCase(nombre)).thenReturn(trabajadoresEsperados);
        
        // When
        List<Trabajador> resultado = trabajadorService.buscarPorNombre(nombre);
        
        // Then
        assertEquals(2, resultado.size());
        verify(trabajadorRepository).findByNombreContainingIgnoreCase(nombre);
    }

    @Test
    void deberiaBuscarTrabajadoresConHorasMinimas() {
        // Given
        int horasMinimas = 6;
        List<Trabajador> trabajadoresEsperados = Arrays.asList(
            new Trabajador("12345678Z", "Juan Perez", 8, tiendaMock),
            new Trabajador("87654321X", "Ana Garcia", 6, tiendaMock)
        );
        
        when(trabajadorRepository.findByHorasDisponiblesGreaterThanEqual(horasMinimas))
            .thenReturn(trabajadoresEsperados);
        
        // When
        List<Trabajador> resultado = trabajadorService.buscarConHorasMinimas(horasMinimas);
        
        // Then
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(t -> t.getHorasDisponibles() >= horasMinimas));
        verify(trabajadorRepository).findByHorasDisponiblesGreaterThanEqual(horasMinimas);
    }

    @Test
    void deberiaContarTrabajadoresPorTienda() {
        // Given
        String codigoTienda = "T001";
        Long expectedCount = 3L;
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tiendaMock));
        when(trabajadorRepository.countByTienda(tiendaMock)).thenReturn(expectedCount);
        
        // When
        Long resultado = trabajadorService.contarTrabajadoresPorTienda(codigoTienda);
        
        // Then
        assertEquals(expectedCount, resultado);
        verify(tiendaRepository).findByCodigo(codigoTienda);
        verify(trabajadorRepository).countByTienda(tiendaMock);
    }

    @Test
    void deberiaFallarAlContarTrabajadoresSiTiendaNoExiste() {
        // Given
        String codigoTienda = "INEXISTENTE";
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> trabajadorService.contarTrabajadoresPorTienda(codigoTienda)
        );
        
        assertEquals("No existe una tienda con el código: INEXISTENTE", exception.getMessage());
        verify(tiendaRepository).findByCodigo(codigoTienda);
        verify(trabajadorRepository, never()).countByTienda(any());
    }

    @Test
    void deberiaSumarHorasDisponiblesPorTienda() {
        // Given
        String codigoTienda = "T001";
        Integer expectedSum = 14;
        
        when(tiendaRepository.findByCodigo(codigoTienda)).thenReturn(Optional.of(tiendaMock));
        when(trabajadorRepository.sumHorasDisponiblesByTienda(tiendaMock)).thenReturn(expectedSum);
        
        // When
        Integer resultado = trabajadorService.sumarHorasDisponiblesPorTienda(codigoTienda);
        
        // Then
        assertEquals(expectedSum, resultado);
        verify(tiendaRepository).findByCodigo(codigoTienda);
        verify(trabajadorRepository).sumHorasDisponiblesByTienda(tiendaMock);
    }

    @Test
    void deberiaVerificarExistenciaDeTrabajador() {
        // Given
        String dni = "12345678Z";
        
        when(trabajadorRepository.existsByDni(dni)).thenReturn(true);
        
        // When
        boolean resultado = trabajadorService.existeTrabajador(dni);
        
        // Then
        assertTrue(resultado);
        verify(trabajadorRepository).existsByDni(dni);
    }
}
