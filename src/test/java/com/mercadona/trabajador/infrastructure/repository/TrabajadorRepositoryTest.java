package com.mercadona.trabajador.infrastructure.repository;

import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.domain.Trabajador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrabajadorRepositoryTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("mercadona_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private TiendaRepository tiendaRepository;

    private Tienda tiendaCentro;
    private Tienda tiendaNorte;

    @BeforeEach
    void setUp() {
        tiendaCentro = tiendaRepository.save(new Tienda("T001", "Tienda Centro"));
        tiendaNorte = tiendaRepository.save(new Tienda("T002", "Tienda Norte"));
    }

    @Test
    void deberiaPersistirYRecuperarTrabajador() {
        // Given
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tiendaCentro);
        
        // When
        Trabajador trabajadorGuardado = trabajadorRepository.save(trabajador);
        
        // Then
        assertNotNull(trabajadorGuardado.getId());
        assertEquals("12345678Z", trabajadorGuardado.getDni());
        assertEquals("Juan Perez", trabajadorGuardado.getNombre());
        assertEquals(8, trabajadorGuardado.getHorasDisponibles());
        assertEquals(tiendaCentro.getId(), trabajadorGuardado.getTienda().getId());
    }

    @Test
    void deberiaEncontrarTrabajadorPorDni() {
        // Given
        Trabajador trabajador = new Trabajador("87654321X", "Ana Garcia", 6, tiendaNorte);
        trabajadorRepository.save(trabajador);
        
        // When
        Optional<Trabajador> encontrado = trabajadorRepository.findByDni("87654321X");
        
        // Then
        assertTrue(encontrado.isPresent());
        assertEquals("Ana Garcia", encontrado.get().getNombre());
        assertEquals(tiendaNorte.getId(), encontrado.get().getTienda().getId());
    }

    @Test
    void deberiaValidarDniUnico() {
        // Given
        Trabajador trabajador1 = new Trabajador("11111111H", "Primer Trabajador", 8, tiendaCentro);
        trabajadorRepository.save(trabajador1);
        
        // When & Then
        Trabajador trabajador2 = new Trabajador("11111111H", "Segundo Trabajador", 6, tiendaNorte);
        assertThrows(Exception.class, () -> {
            trabajadorRepository.save(trabajador2);
            trabajadorRepository.flush();
        });
    }

    @Test
    void deberiaEncontrarTrabajadoresPorTienda() {
        // Given
        trabajadorRepository.save(new Trabajador("33333333P", "Trabajador Centro 1", 8, tiendaCentro));
        trabajadorRepository.save(new Trabajador("66666666Q", "Trabajador Centro 2", 6, tiendaCentro));
        trabajadorRepository.save(new Trabajador("Y9342088P", "Trabajador Norte 1", 7, tiendaNorte));
        
        // When
        List<Trabajador> trabajadoresCentro = trabajadorRepository.findByTienda(tiendaCentro);
        List<Trabajador> trabajadoresNorte = trabajadorRepository.findByTienda(tiendaNorte);
        
        // Then
        assertEquals(2, trabajadoresCentro.size());
        assertEquals(1, trabajadoresNorte.size());
        assertTrue(trabajadoresCentro.stream().allMatch(t -> t.getTienda().getId().equals(tiendaCentro.getId())));
    }

    @Test
    void deberiaEncontrarTrabajadoresPorCodigoTienda() {
        // Given
        trabajadorRepository.save(new Trabajador("66666666Q", "Trabajador Test", 5, tiendaCentro));
        
        // When
        List<Trabajador> trabajadores = trabajadorRepository.findByCodigoTienda("T001");
        
        // Then
        assertEquals(1, trabajadores.size());
        assertEquals("Trabajador Test", trabajadores.get(0).getNombre());
    }

    @Test
    void deberiaBuscarPorHorasDisponibles() {
        // Given - Contar estado inicial
        int countInicialMinimo6h = trabajadorRepository.findByHorasDisponiblesGreaterThanEqual(6).size();
        
        // Añadir datos específicos del test
        trabajadorRepository.save(new Trabajador("Y1234567X", "Trabajador 4h", 4, tiendaCentro)); 
        trabajadorRepository.save(new Trabajador("X1234567L", "Trabajador 6h", 6, tiendaCentro)); 
        trabajadorRepository.save(new Trabajador("77777777B", "Trabajador 8h", 8, tiendaNorte)); 
        
        // When
        List<Trabajador> trabajadoresConMinimo6h = trabajadorRepository.findByHorasDisponiblesGreaterThanEqual(6);
        
        // Then - Verificar incremento exacto (+2 trabajadores >= 6h)
        assertEquals(countInicialMinimo6h + 2, trabajadoresConMinimo6h.size());
        assertTrue(trabajadoresConMinimo6h.stream().allMatch(t -> t.getHorasDisponibles() >= 6));
    }

    @Test
    void deberiaBuscarPorNombreParcial() {
        // Given - Contar estado inicial
        int countInicialCarlos = trabajadorRepository.findByNombreContainingIgnoreCase("carlos").size();
        int countInicialAna = trabajadorRepository.findByNombreContainingIgnoreCase("ANA").size();
        
        // Añadir datos específicos del test
        trabajadorRepository.save(new Trabajador("12345678Z", "Juan Carlos Lopez", 8, tiendaCentro)); // Contiene "carlos"
        trabajadorRepository.save(new Trabajador("Y1234567X", "Ana Maria Gonzalez", 7, tiendaNorte)); // Contiene "ANA"
        trabajadorRepository.save(new Trabajador("77777777B", "Carlos Rodriguez", 6, tiendaCentro)); // Contiene "carlos"
        
        // When
        List<Trabajador> trabajadoresCarlos = trabajadorRepository.findByNombreContainingIgnoreCase("carlos");
        List<Trabajador> trabajadoresAna = trabajadorRepository.findByNombreContainingIgnoreCase("ANA");
        
        // Then - Verificar incrementos exactos
        assertEquals(countInicialCarlos + 2, trabajadoresCarlos.size()); // +2 con "carlos"
        assertEquals(countInicialAna + 1, trabajadoresAna.size()); // +1 con "ANA"
    }

    @Test
    void deberiaContarTrabajadoresPorTienda() {
        // Given
        trabajadorRepository.save(new Trabajador("Z1234567R", "Contador 1", 8, tiendaCentro));
        trabajadorRepository.save(new Trabajador("11111111H", "Contador 2", 7, tiendaCentro));
        trabajadorRepository.save(new Trabajador("77876826E", "Contador 3", 6, tiendaNorte));
        
        // When
        Long totalCentro = trabajadorRepository.countByTienda(tiendaCentro);
        Long totalNorte = trabajadorRepository.countByTienda(tiendaNorte);
        
        // Then
        assertEquals(2L, totalCentro);
        assertEquals(1L, totalNorte);
    }

    @Test
    void deberiaSumarHorasDisponiblesPorTienda() {
        // Given
        trabajadorRepository.save(new Trabajador("33333333P", "Sumador 1", 8, tiendaCentro));
        trabajadorRepository.save(new Trabajador("89772617Y", "Sumador 2", 6, tiendaCentro));
        trabajadorRepository.save(new Trabajador("86254467M", "Sumador 3", 4, tiendaNorte));
        
        // When
        Integer horasCentro = trabajadorRepository.sumHorasDisponiblesByTienda(tiendaCentro);
        Integer horasNorte = trabajadorRepository.sumHorasDisponiblesByTienda(tiendaNorte);
        
        // Then
        assertEquals(14, horasCentro); // 8 + 6
        assertEquals(4, horasNorte);
    }

    @Test
    void deberiaValidarDniNieEspanoles() {
        // Given 
        Trabajador trabajadorDni = new Trabajador("12345678Z", "Trabajador DNI", 8, tiendaCentro);
        Trabajador trabajadorNie = new Trabajador("X1234567L", "Trabajador NIE", 7, tiendaNorte);
        
        // When & Then
        assertDoesNotThrow(() -> {
            trabajadorRepository.save(trabajadorDni);
            trabajadorRepository.save(trabajadorNie);
        });
        
        assertTrue(trabajadorRepository.existsByDni("12345678Z"));
        assertTrue(trabajadorRepository.existsByDni("X1234567L"));
    }
}
