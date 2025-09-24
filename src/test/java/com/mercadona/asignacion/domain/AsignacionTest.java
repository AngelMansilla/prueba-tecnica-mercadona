package com.mercadona.asignacion.domain;

import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.trabajador.domain.Trabajador;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AsignacionTest {

    @Test
    void deberiaCrearAsignacionConDatosValidos() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        int horasAsignadas = 4;
        
        Asignacion asignacion = new Asignacion(trabajador, seccion, horasAsignadas);
        
        assertEquals(trabajador, asignacion.getTrabajador());
        assertEquals(seccion, asignacion.getSeccion());
        assertEquals(horasAsignadas, asignacion.getHorasAsignadas());
    }

    @Test
    void deberiaFallarSiTrabajadorEsNull() {
        Seccion seccion = new Seccion("Horno", 8);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Asignacion(null, seccion, 4);
        });
        assertEquals("El trabajador no puede ser nulo", exception.getMessage());
    }

    @Test
    void deberiaFallarSiSeccionEsNull() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Asignacion(trabajador, null, 4);
        });
        assertEquals("La sección no puede ser nula", exception.getMessage());
    }

    @Test
    void deberiaFallarSiHorasAsignadasSonCero() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Asignacion(trabajador, seccion, 0);
        });
        assertEquals("Las horas asignadas deben ser positivas", exception.getMessage());
    }

    @Test
    void deberiaFallarSiHorasAsignadasSonNegativas() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion seccion = new Seccion("Horno", 8);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Asignacion(trabajador, seccion, -2);
        });
        assertEquals("Las horas asignadas deben ser positivas", exception.getMessage());
    }

    @Test
    void deberiaTenerEqualsBasadoEnTrabajadorYSeccion() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador1 = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Trabajador trabajador2 = new Trabajador("87654321X", "Ana Garcia", 6, tienda);
        Seccion seccion1 = new Seccion("Horno", 8);
        Seccion seccion2 = new Seccion("Cajas", 16);
        
        // Misma asignación (mismo trabajador + misma sección)
        Asignacion asignacion1 = new Asignacion(trabajador1, seccion1, 4);
        Asignacion asignacionIgual = new Asignacion(trabajador1, seccion1, 6); // Horas diferentes
        
        // Asignaciones diferentes
        Asignacion asignacion2 = new Asignacion(trabajador2, seccion1, 4); // Diferente trabajador
        Asignacion asignacion3 = new Asignacion(trabajador1, seccion2, 4); // Diferente sección
        
        assertEquals(asignacion1, asignacionIgual);
        assertNotEquals(asignacion1, asignacion2);
        assertNotEquals(asignacion1, asignacion3);
        assertEquals(asignacion1.hashCode(), asignacionIgual.hashCode());
    }

    @Test
    void deberiaPermitirEjemploDelEnunciado() {
        // Ejemplo del enunciado: "2 horas en pescadería y 6 horas en horno"
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        Seccion pescaderia = new Seccion("Pescadería", 16);
        Seccion horno = new Seccion("Horno", 8);
        
        Asignacion asignacion1 = new Asignacion(trabajador, pescaderia, 2);
        Asignacion asignacion2 = new Asignacion(trabajador, horno, 6);
        
        assertEquals(2, asignacion1.getHorasAsignadas());
        assertEquals(6, asignacion2.getHorasAsignadas());
    }
}
