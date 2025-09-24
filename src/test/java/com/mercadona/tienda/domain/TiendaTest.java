package com.mercadona.tienda.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TiendaTest {

    @Test
    void deberiaCrearTiendaConCodigoYNombre() {
        String codigo = "T001";
        String nombre = "Tienda Centro Valencia";
        
        Tienda tienda = new Tienda(codigo, nombre);
        
        assertEquals(codigo, tienda.getCodigo());
        assertEquals(nombre, tienda.getNombre());
    }

    @Test
    void deberiaFallarSiCodigoEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Tienda(null, "Nombre");
        });
    }

    @Test
    void deberiaFallarSiCodigoEstaVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Tienda("", "Nombre");
        });
    }

    @Test
    void deberiaFallarSiCodigoSoloTieneEspacios() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Tienda("   ", "Nombre");
        });
    }

    @Test
    void deberiaFallarSiNombreEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Tienda("T001", null);
        });
    }

    @Test
    void deberiaFallarSiNombreEstaVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Tienda("T001", "");
        });
    }

    @Test
    void deberiaTenerEqualsYHashCodeCorrectamente() {
        Tienda tienda1 = new Tienda("T001", "Tienda Centro");
        Tienda tienda2 = new Tienda("T002", "Tienda Norte");
        
        assertEquals(tienda1, tienda1);
        
        assertNotEquals(tienda1, tienda2);
        assertNotEquals(tienda1.hashCode(), tienda2.hashCode());
        
        assertNotEquals(tienda1, null);
        
        assertNotEquals(tienda1, "string");
    }
}
