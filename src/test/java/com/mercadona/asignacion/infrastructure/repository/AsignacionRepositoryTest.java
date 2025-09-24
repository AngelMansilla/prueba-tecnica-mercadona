package com.mercadona.asignacion.infrastructure.repository;

import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.trabajador.infrastructure.repository.TrabajadorRepository;
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

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AsignacionRepositoryTest {

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
    private AsignacionRepository asignacionRepository;

    @Autowired
    private TiendaRepository tiendaRepository;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private EntityManager entityManager;

    private Tienda tienda;
    private Trabajador trabajador1;
    private Trabajador trabajador2;
    private Seccion seccionHorno;
    private Seccion seccionCajas;
    private Seccion seccionPescaderia;

    @BeforeEach
    void setUp() {
        // Crear tienda
        tienda = tiendaRepository.save(new Tienda("T001", "Tienda Test"));

        // Crear trabajadores
        trabajador1 = trabajadorRepository.save(new Trabajador("12345678Z", "Juan Perez", 8, tienda));
        trabajador2 = trabajadorRepository.save(new Trabajador("87654321X", "Ana Garcia", 6, tienda));

        // Obtener secciones creadas por Flyway
        seccionHorno = entityManager.createQuery("SELECT s FROM Seccion s WHERE s.nombre = 'Horno'", Seccion.class)
                .getSingleResult();
        seccionCajas = entityManager.createQuery("SELECT s FROM Seccion s WHERE s.nombre = 'Cajas'", Seccion.class)
                .getSingleResult();
        seccionPescaderia = entityManager.createQuery("SELECT s FROM Seccion s WHERE s.nombre = 'Pescadería'", Seccion.class)
                .getSingleResult();
    }

    @Test
    void deberiaPersistirYRecuperarAsignacion() {
        // Given
        Asignacion asignacion = new Asignacion(trabajador1, seccionHorno, 4);
        
        // When
        Asignacion asignacionGuardada = asignacionRepository.save(asignacion);
        
        // Then
        assertNotNull(asignacionGuardada.getId());
        assertEquals(trabajador1.getId(), asignacionGuardada.getTrabajador().getId());
        assertEquals(seccionHorno.getId(), asignacionGuardada.getSeccion().getId());
        assertEquals(4, asignacionGuardada.getHorasAsignadas());
    }

    @Test
    void deberiaEncontrarAsignacionesPorTrabajador() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador1, seccionCajas, 2));
        asignacionRepository.save(new Asignacion(trabajador2, seccionPescaderia, 3));
        
        // When
        List<Asignacion> asignacionesTrabajador1 = asignacionRepository.findByTrabajador(trabajador1);
        List<Asignacion> asignacionesTrabajador2 = asignacionRepository.findByTrabajador(trabajador2);
        
        // Then
        assertEquals(2, asignacionesTrabajador1.size());
        assertEquals(1, asignacionesTrabajador2.size());
        assertTrue(asignacionesTrabajador1.stream()
                .allMatch(a -> a.getTrabajador().getId().equals(trabajador1.getId())));
    }

    @Test
    void deberiaEncontrarAsignacionesPorSeccion() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador2, seccionHorno, 3));
        asignacionRepository.save(new Asignacion(trabajador1, seccionCajas, 2));
        
        // When
        List<Asignacion> asignacionesHorno = asignacionRepository.findBySeccion(seccionHorno);
        List<Asignacion> asignacionesCajas = asignacionRepository.findBySeccion(seccionCajas);
        
        // Then
        assertEquals(2, asignacionesHorno.size());
        assertEquals(1, asignacionesCajas.size());
        assertTrue(asignacionesHorno.stream()
                .allMatch(a -> a.getSeccion().getId().equals(seccionHorno.getId())));
    }

    @Test
    void deberiaEncontrarAsignacionEspecifica() {
        // Given
        Asignacion asignacion = asignacionRepository.save(new Asignacion(trabajador1, seccionPescaderia, 5));
        
        // When
        Optional<Asignacion> encontrada = asignacionRepository.findByTrabajadorAndSeccion(trabajador1, seccionPescaderia);
        Optional<Asignacion> noEncontrada = asignacionRepository.findByTrabajadorAndSeccion(trabajador2, seccionPescaderia);
        
        // Then
        assertTrue(encontrada.isPresent());
        assertEquals(asignacion.getId(), encontrada.get().getId());
        assertFalse(noEncontrada.isPresent());
    }

    @Test
    void deberiaVerificarExistenciaDeAsignacion() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        
        // When & Then
        assertTrue(asignacionRepository.existsByTrabajadorAndSeccion(trabajador1, seccionHorno));
        assertFalse(asignacionRepository.existsByTrabajadorAndSeccion(trabajador2, seccionHorno));
        assertFalse(asignacionRepository.existsByTrabajadorAndSeccion(trabajador1, seccionCajas));
    }

    @Test
    void deberiaBuscarPorCodigoTienda() {
        // Given
        Tienda otraTienda = tiendaRepository.save(new Tienda("T002", "Otra Tienda"));
        Trabajador trabajadorOtraTienda = trabajadorRepository.save(
                new Trabajador("11111111H", "Pedro Lopez", 7, otraTienda));
        
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador2, seccionCajas, 3));
        asignacionRepository.save(new Asignacion(trabajadorOtraTienda, seccionPescaderia, 5));
        
        // When
        List<Asignacion> asignacionesTienda1 = asignacionRepository.findByCodigoTienda("T001");
        List<Asignacion> asignacionesTienda2 = asignacionRepository.findByCodigoTienda("T002");
        
        // Then
        assertEquals(2, asignacionesTienda1.size());
        assertEquals(1, asignacionesTienda2.size());
        assertTrue(asignacionesTienda1.stream()
                .allMatch(a -> a.getTrabajador().getTienda().getCodigo().equals("T001")));
    }

    @Test
    void deberiaBuscarPorNombreSeccion() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador2, seccionHorno, 3));
        asignacionRepository.save(new Asignacion(trabajador1, seccionCajas, 2));
        
        // When
        List<Asignacion> asignacionesHorno = asignacionRepository.findByNombreSeccion("Horno");
        List<Asignacion> asignacionesCajas = asignacionRepository.findByNombreSeccion("Cajas");
        
        // Then
        assertEquals(2, asignacionesHorno.size());
        assertEquals(1, asignacionesCajas.size());
        assertTrue(asignacionesHorno.stream()
                .allMatch(a -> a.getSeccion().getNombre().equals("Horno")));
    }

    @Test
    void deberiaBuscarPorHorasMinimas() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 2));
        asignacionRepository.save(new Asignacion(trabajador2, seccionCajas, 4));
        asignacionRepository.save(new Asignacion(trabajador1, seccionPescaderia, 6));
        
        // When
        List<Asignacion> asignacionesMinimo4h = asignacionRepository.findByHorasAsignadasGreaterThanEqual(4);
        List<Asignacion> asignacionesMinimo5h = asignacionRepository.findByHorasAsignadasGreaterThanEqual(5);
        
        // Then
        assertEquals(2, asignacionesMinimo4h.size());
        assertEquals(1, asignacionesMinimo5h.size());
        assertTrue(asignacionesMinimo4h.stream().allMatch(a -> a.getHorasAsignadas() >= 4));
        assertTrue(asignacionesMinimo5h.stream().allMatch(a -> a.getHorasAsignadas() >= 5));
    }

    @Test
    void deberiaSumarHorasPorTienda() {
        // Given
        Tienda otraTienda = tiendaRepository.save(new Tienda("T003", "Tercera Tienda"));
        Trabajador trabajadorOtraTienda = trabajadorRepository.save(
                new Trabajador("22222222J", "Maria Rodriguez", 8, otraTienda));
        
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador2, seccionCajas, 3));
        asignacionRepository.save(new Asignacion(trabajadorOtraTienda, seccionPescaderia, 5));
        
        // When
        Integer horasTienda1 = asignacionRepository.sumHorasAsignadasByCodigoTienda("T001");
        Integer horasTienda3 = asignacionRepository.sumHorasAsignadasByCodigoTienda("T003");
        
        // Then
        assertEquals(7, horasTienda1); // 4 + 3
        assertEquals(5, horasTienda3);
    }

    @Test
    void deberiaSumarHorasPorSeccion() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador2, seccionHorno, 3));
        asignacionRepository.save(new Asignacion(trabajador1, seccionCajas, 2));
        
        // When
        Integer horasHorno = asignacionRepository.sumHorasAsignadasBySeccion(seccionHorno);
        Integer horasCajas = asignacionRepository.sumHorasAsignadasBySeccion(seccionCajas);
        
        // Then
        assertEquals(7, horasHorno); // 4 + 3
        assertEquals(2, horasCajas);
    }

    @Test
    void deberiaContarAsignacionesPorTrabajador() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador1, seccionCajas, 2));
        asignacionRepository.save(new Asignacion(trabajador2, seccionPescaderia, 3));
        
        // When
        Long countTrabajador1 = asignacionRepository.countByTrabajador(trabajador1);
        Long countTrabajador2 = asignacionRepository.countByTrabajador(trabajador2);
        
        // Then
        assertEquals(2L, countTrabajador1);
        assertEquals(1L, countTrabajador2);
    }

    @Test
    void deberiaContarAsignacionesPorSeccion() {
        // Given
        asignacionRepository.save(new Asignacion(trabajador1, seccionHorno, 4));
        asignacionRepository.save(new Asignacion(trabajador2, seccionHorno, 3));
        asignacionRepository.save(new Asignacion(trabajador1, seccionCajas, 2));
        
        // When
        Long countHorno = asignacionRepository.countBySeccion(seccionHorno);
        Long countCajas = asignacionRepository.countBySeccion(seccionCajas);
        Long countPescaderia = asignacionRepository.countBySeccion(seccionPescaderia);
        
        // Then
        assertEquals(2L, countHorno);
        assertEquals(1L, countCajas);
        assertEquals(0L, countPescaderia);
    }

    @Test
    void deberiaPersistirAsignacionConDatosCompletos() {
        // Given
        Trabajador juanPerez = trabajadorRepository.save(
                new Trabajador("33333333P", "Juan Perez", 8, tienda));
        
        // When
        Asignacion asignacion = asignacionRepository.save(
                new Asignacion(juanPerez, seccionHorno, 4));
        
        // Then
        assertNotNull(asignacion.getId());
        assertEquals("Juan Perez", asignacion.getTrabajador().getNombre());
        assertEquals("Horno", asignacion.getSeccion().getNombre());
        assertEquals(4, asignacion.getHorasAsignadas());
        assertEquals(8, asignacion.getTrabajador().getHorasDisponibles());
        assertEquals(8, asignacion.getSeccion().getHorasNecesarias());
    }
}
