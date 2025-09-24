package com.mercadona.tienda.infrastructure.repository;

import com.mercadona.tienda.domain.Tienda;
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
class TiendaRepositoryTest {

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
    private TiendaRepository tiendaRepository;

    @Test
    void deberiaPersistirYRecuperarTienda() {
        // Given
        Tienda tienda = new Tienda("T001", "Tienda Centro Valencia");
        
        // When
        Tienda tiendaGuardada = tiendaRepository.save(tienda);
        
        // Then
        assertNotNull(tiendaGuardada.getId());
        assertEquals("T001", tiendaGuardada.getCodigo());
        assertEquals("Tienda Centro Valencia", tiendaGuardada.getNombre());
    }

    @Test
    void deberiaEncontrarTiendaPorCodigo() {
        // Given
        Tienda tienda = new Tienda("T002", "Tienda Norte Madrid");
        tiendaRepository.save(tienda);
        
        // When
        Optional<Tienda> encontrada = tiendaRepository.findByCodigo("T002");
        
        // Then
        assertTrue(encontrada.isPresent());
        assertEquals("Tienda Norte Madrid", encontrada.get().getNombre());
    }

    @Test
    void deberiaRetornarVacioSiCodigoNoExiste() {
        // When
        Optional<Tienda> encontrada = tiendaRepository.findByCodigo("INEXISTENTE");
        
        // Then
        assertFalse(encontrada.isPresent());
    }

    @Test
    void deberiaVerificarExistenciaPorCodigo() {
        // Given
        Tienda tienda = new Tienda("T003", "Tienda Sur Sevilla");
        tiendaRepository.save(tienda);
        
        // When & Then
        assertTrue(tiendaRepository.existsByCodigo("T003"));
        assertFalse(tiendaRepository.existsByCodigo("INEXISTENTE"));
    }

    @Test
    void deberiaBuscarPorNombreParcial() {
        // Given
        tiendaRepository.save(new Tienda("T004", "Tienda Centro Barcelona"));
        tiendaRepository.save(new Tienda("T005", "Tienda Norte Barcelona"));
        tiendaRepository.save(new Tienda("T006", "Tienda Sur Madrid"));
        
        // When
        List<Tienda> tiendasBarcelona = tiendaRepository.findByNombreContainingIgnoreCase("barcelona");
        List<Tienda> tiendasCentro = tiendaRepository.findByNombreContainingIgnoreCase("CENTRO");
        
        // Then
        assertEquals(2, tiendasBarcelona.size());
        assertEquals(1, tiendasCentro.size());
        assertTrue(tiendasCentro.get(0).getNombre().contains("Centro"));
    }

    @Test
    void deberiaContarTotalTiendas() {
        // Given
        tiendaRepository.save(new Tienda("T007", "Tienda Test 1"));
        tiendaRepository.save(new Tienda("T008", "Tienda Test 2"));
        tiendaRepository.save(new Tienda("T009", "Tienda Test 3"));
        
        // When
        Long total = tiendaRepository.countTotalTiendas();
        
        // Then
        assertTrue(total >= 3); // Puede haber más por otros tests
    }

    @Test
    void deberiaRespetarConstraintDeCodigoUnico() {
        // Given
        Tienda tienda1 = new Tienda("T010", "Primera Tienda");
        tiendaRepository.save(tienda1);
        
        // When & Then
        Tienda tienda2 = new Tienda("T010", "Segunda Tienda");
        assertThrows(Exception.class, () -> {
            tiendaRepository.save(tienda2);
            tiendaRepository.flush(); // Forzar la validación
        });
    }
}
