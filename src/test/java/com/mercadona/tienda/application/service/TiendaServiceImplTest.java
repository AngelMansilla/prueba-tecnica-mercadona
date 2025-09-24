package com.mercadona.tienda.application.service;

import com.mercadona.tienda.application.port.TiendaService;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
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
class TiendaServiceImplTest {

    @Mock
    private TiendaRepository tiendaRepository;

    private TiendaService tiendaService;

    @BeforeEach
    void setUp() {
        tiendaService = new TiendaServiceImpl(tiendaRepository);
    }

    @Test
    void deberiaCrearTiendaCuandoDatosValidos() {
        // Given
        String codigo = "T001";
        String nombre = "Tienda Centro";
        Tienda tiendaEsperada = new Tienda(codigo, nombre);
        
        when(tiendaRepository.existsByCodigo(codigo)).thenReturn(false);
        when(tiendaRepository.save(any(Tienda.class))).thenReturn(tiendaEsperada);
        
        // When
        Tienda resultado = tiendaService.crearTienda(codigo, nombre);
        
        // Then
        assertNotNull(resultado);
        assertEquals(codigo, resultado.getCodigo());
        assertEquals(nombre, resultado.getNombre());
        
        verify(tiendaRepository).existsByCodigo(codigo);
        verify(tiendaRepository).save(any(Tienda.class));
    }

    @Test
    void deberiaFallarAlCrearTiendaCuandoCodigoYaExiste() {
        // Given
        String codigo = "T001";
        String nombre = "Tienda Centro";
        
        when(tiendaRepository.existsByCodigo(codigo)).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.crearTienda(codigo, nombre)
        );
        
        assertEquals("Ya existe una tienda con el código: T001", exception.getMessage());
        verify(tiendaRepository).existsByCodigo(codigo);
        verify(tiendaRepository, never()).save(any(Tienda.class));
    }

    @Test
    void deberiaFallarAlCrearTiendaCuandoCodigoNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.crearTienda(null, "Nombre")
        );
        
        assertEquals("El código no puede ser nulo o vacío", exception.getMessage());
        verify(tiendaRepository, never()).existsByCodigo(any());
        verify(tiendaRepository, never()).save(any(Tienda.class));
    }

    @Test
    void deberiaFallarAlCrearTiendaCuandoCodigoVacio() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.crearTienda("", "Nombre")
        );
        
        assertEquals("El código no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaFallarAlCrearTiendaCuandoNombreNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.crearTienda("T001", null)
        );
        
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void deberiaBuscarTiendaPorCodigo() {
        // Given
        String codigo = "T001";
        Tienda tiendaEsperada = new Tienda(codigo, "Tienda Centro");
        
        when(tiendaRepository.findByCodigo(codigo)).thenReturn(Optional.of(tiendaEsperada));
        
        // When
        Optional<Tienda> resultado = tiendaService.buscarPorCodigo(codigo);
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals(codigo, resultado.get().getCodigo());
        verify(tiendaRepository).findByCodigo(codigo);
    }

    @Test
    void deberiaRetornarVacioSiTiendaNoExiste() {
        // Given
        String codigo = "INEXISTENTE";
        
        when(tiendaRepository.findByCodigo(codigo)).thenReturn(Optional.empty());
        
        // When
        Optional<Tienda> resultado = tiendaService.buscarPorCodigo(codigo);
        
        // Then
        assertFalse(resultado.isPresent());
        verify(tiendaRepository).findByCodigo(codigo);
    }

    @Test
    void deberiaBuscarTiendasPorNombre() {
        // Given
        String nombre = "Centro";
        List<Tienda> tiendasEsperadas = Arrays.asList(
            new Tienda("T001", "Tienda Centro Madrid"),
            new Tienda("T002", "Tienda Centro Barcelona")
        );
        
        when(tiendaRepository.findByNombreContainingIgnoreCase(nombre)).thenReturn(tiendasEsperadas);
        
        // When
        List<Tienda> resultado = tiendaService.buscarPorNombre(nombre);
        
        // Then
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(t -> t.getNombre().contains("Centro")));
        verify(tiendaRepository).findByNombreContainingIgnoreCase(nombre);
    }

    @Test
    void deberiaObtenerTodasLasTiendas() {
        // Given
        List<Tienda> tiendasEsperadas = Arrays.asList(
            new Tienda("T001", "Tienda 1"),
            new Tienda("T002", "Tienda 2")
        );
        
        when(tiendaRepository.findAll()).thenReturn(tiendasEsperadas);
        
        // When
        List<Tienda> resultado = tiendaService.obtenerTodasLasTiendas();
        
        // Then
        assertEquals(2, resultado.size());
        verify(tiendaRepository).findAll();
    }

    @Test
    void deberiaContarTiendas() {
        // Given
        Long expectedCount = 5L;
        
        when(tiendaRepository.countTotalTiendas()).thenReturn(expectedCount);
        
        // When
        Long resultado = tiendaService.contarTiendas();
        
        // Then
        assertEquals(expectedCount, resultado);
        verify(tiendaRepository).countTotalTiendas();
    }

    @Test
    void deberiaVerificarExistenciaDeTienda() {
        // Given
        String codigo = "T001";
        
        when(tiendaRepository.existsByCodigo(codigo)).thenReturn(true);
        
        // When
        boolean resultado = tiendaService.existeTienda(codigo);
        
        // Then
        assertTrue(resultado);
        verify(tiendaRepository).existsByCodigo(codigo);
    }
}
