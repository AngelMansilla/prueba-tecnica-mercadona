package com.mercadona.tienda.application.service;

import com.mercadona.tienda.application.port.TiendaService;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.infrastructure.repository.TrabajadorRepository;
import com.mercadona.asignacion.infrastructure.repository.AsignacionRepository;
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
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class TiendaServiceImplTest {

    @Mock
    private TiendaRepository tiendaRepository;
    
    @Mock
    private TrabajadorRepository trabajadorRepository;
    
    @Mock
    private AsignacionRepository asignacionRepository;

    private TiendaService tiendaService;

    @BeforeEach
    void setUp() {
        tiendaService = new TiendaServiceImpl(tiendaRepository, trabajadorRepository, asignacionRepository);
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
    void deberiaFallarCuandoCodigoTiendaFormatoInvalido() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            tiendaService.crearTienda("X001", "Tienda Test");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            tiendaService.crearTienda("T1000", "Tienda Test");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            tiendaService.crearTienda("T00", "Tienda Test");
        });
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

    @Test
    void deberiaActualizarTiendaCuandoExiste() {
        // Given
        String codigo = "T001";
        String nuevoNombre = "Tienda Centro Actualizada";
        Tienda tiendaExistente = new Tienda(codigo, "Tienda Centro");
        Tienda tiendaActualizada = new Tienda(codigo, nuevoNombre);
        
        when(tiendaRepository.findByCodigo(codigo)).thenReturn(Optional.of(tiendaExistente));
        when(tiendaRepository.save(any(Tienda.class))).thenReturn(tiendaActualizada);
        
        // When
        Tienda resultado = tiendaService.actualizarTienda(codigo, nuevoNombre);
        
        // Then
        assertEquals(codigo, resultado.getCodigo());
        assertEquals(nuevoNombre, resultado.getNombre());
        verify(tiendaRepository).findByCodigo(codigo);
        verify(tiendaRepository).save(any(Tienda.class));
    }

    @Test
    void deberiaFallarActualizarTiendaCuandoNoExiste() {
        // Given
        String codigo = "INEXISTENTE";
        String nuevoNombre = "Nuevo Nombre";
        
        when(tiendaRepository.findByCodigo(codigo)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.actualizarTienda(codigo, nuevoNombre)
        );
        
        assertEquals("No se encontró la tienda con código: " + codigo, exception.getMessage());
        verify(tiendaRepository).findByCodigo(codigo);
        verify(tiendaRepository, never()).save(any());
    }

    @Test
    void deberiaFallarActualizarTiendaCuandoNombreVacio() {
        // Given
        String codigo = "T001";
        String nombreVacio = "";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.actualizarTienda(codigo, nombreVacio)
        );
        
        assertEquals("El nombre no puede ser nulo o vacío", exception.getMessage());
        verify(tiendaRepository, never()).findByCodigo(any());
        verify(tiendaRepository, never()).save(any());
    }

    @Test
    void deberiaEliminarTiendaCuandoExiste() {
        // Given
        String codigo = "T001";
        Tienda tiendaExistente = new Tienda(codigo, "Tienda Centro");
        tiendaExistente.setId(1L); // Simulamos que tiene ID
        
        when(tiendaRepository.findByCodigo(codigo)).thenReturn(Optional.of(tiendaExistente));
        doNothing().when(tiendaRepository).deleteById(any(Long.class));
        
        // When
        tiendaService.eliminarTienda(codigo);
        
        // Then
        verify(tiendaRepository).findByCodigo(codigo);
        verify(tiendaRepository).deleteById(1L);
    }

    @Test
    void deberiaFallarEliminarTiendaCuandoNoExiste() {
        // Given
        String codigo = "INEXISTENTE";
        
        when(tiendaRepository.findByCodigo(codigo)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tiendaService.eliminarTienda(codigo)
        );
        
        assertEquals("No se encontró la tienda con código: " + codigo, exception.getMessage());
        verify(tiendaRepository).findByCodigo(codigo);
        verify(tiendaRepository, never()).deleteById(any());
    }
}
