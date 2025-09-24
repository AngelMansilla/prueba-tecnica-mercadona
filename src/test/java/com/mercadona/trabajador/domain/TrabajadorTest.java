package com.mercadona.trabajador.domain;

import com.mercadona.tienda.domain.Tienda;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrabajadorTest {

    @Test
    void deberiaCrearTrabajadorConDatosBasicos() {
        String dni = "12345678Z"; // DNI válido
        String nombre = "Juan Perez";
        int horasDisponibles = 8;
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        
        Trabajador trabajador = new Trabajador(dni, nombre, horasDisponibles, tienda);
        
        assertEquals(dni, trabajador.getDni());
        assertEquals(nombre, trabajador.getNombre());
        assertEquals(horasDisponibles, trabajador.getHorasDisponibles());
        assertEquals(tienda, trabajador.getTienda());
    }

    @Test
    void deberiaFallarSiDniEsNull() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador(null, "Juan", 8, tienda);
        });
    }

    @Test
    void deberiaFallarSiDniEstaVacio() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("", "Juan", 8, tienda);
        });
    }

    @Test
    void deberiaFallarSiDniTieneFormatoIncorrecto() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("12345678", "Juan Perez", 8, tienda); // Sin letra
        });
        assertEquals("El documento debe tener formato válido (DNI: 8 dígitos + letra, NIE: X/Y/Z + 7 dígitos + letra)", exception.getMessage());
    }

    @Test
    void deberiaFallarSiDniTieneLetraIncorrecta() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("12345678X", "Juan Perez", 8, tienda); // Letra incorrecta para este número
        });
        assertEquals("El documento debe tener formato válido (DNI: 8 dígitos + letra, NIE: X/Y/Z + 7 dígitos + letra)", exception.getMessage());
    }

    @Test
    void deberiaAceptarDniConFormatoCorrecto() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        // DNI válido: 12345678Z (calculado con algoritmo de módulo 23)
        Trabajador trabajador = new Trabajador("12345678Z", "Juan Perez", 8, tienda);
        assertEquals("12345678Z", trabajador.getDni());
    }

    @Test
    void deberiaAceptarNieConFormatoCorrecto() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        // NIE válido: X1234567L (calculado con algoritmo NIE)
        Trabajador trabajador = new Trabajador("X1234567L", "Ana García", 8, tienda);
        assertEquals("X1234567L", trabajador.getDni());
    }

    @Test
    void deberiaFallarSiNieTieneLetraIncorrecta() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("X1234567Z", "Ana García", 8, tienda); // Letra incorrecta para NIE
        });
        assertEquals("El documento debe tener formato válido (DNI: 8 dígitos + letra, NIE: X/Y/Z + 7 dígitos + letra)", exception.getMessage());
    }

    @Test
    void deberiaFallarSiNombreEsNull() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("12345678Z", null, 8, tienda);
        });
    }

    @Test
    void deberiaFallarSiHorasExcedenMaximo() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("12345678Z", "Juan", 9, tienda);
        });
    }

    @Test
    void deberiaFallarSiHorasSonNegativas() {
        Tienda tienda = new Tienda("T001", "Tienda Centro");
        assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("12345678Z", "Juan", -1, tienda);
        });
    }

    @Test
    void deberiaFallarSiTiendaEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Trabajador("12345678Z", "Juan", 8, null);
        });
    }

    @Test
    void deberiaTenerEqualsYHashCodeCorrectamente() {
        Tienda tienda1 = new Tienda("T001", "Tienda Centro");
        
        Trabajador trabajador1 = new Trabajador("12345678Z", "Juan", 8, tienda1);
        Trabajador trabajador2 = new Trabajador("87654321X", "Pedro", 6, tienda1);
        
        assertNotEquals(trabajador1, trabajador2);
        assertNotEquals(trabajador1.hashCode(), trabajador2.hashCode());
        
        assertNotEquals(trabajador1, null);
        
        assertNotEquals(trabajador1, "string");
    }
}
