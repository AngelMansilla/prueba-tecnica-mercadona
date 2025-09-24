package com.mercadona.tienda.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeccionTest {

    @Test
    void deberiaCrearSeccionHornoConOchoHoras() {
        String nombre = "Horno";
        int horasNecesarias = 8; 
        
        Seccion seccion = new Seccion(nombre, horasNecesarias);
        
        assertEquals(nombre, seccion.getNombre());
        assertEquals(horasNecesarias, seccion.getHorasNecesarias());
    }

    @Test
    void deberiaCrearSeccionCajasConDieciseisHoras() {
        String nombre = "Cajas";
        int horasNecesarias = 16; 
        
        Seccion seccion = new Seccion(nombre, horasNecesarias);
        
        assertEquals(nombre, seccion.getNombre());
        assertEquals(horasNecesarias, seccion.getHorasNecesarias());
    }

    @Test
    void deberiaFallarSiNombreEsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Seccion(null, 8);
        });
    }

    @Test
    void deberiaFallarSiNombreEstaVacio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Seccion("", 8);
        });
    }

    @Test
    void deberiaFallarSiHorasEsCero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Seccion("Pescadería", 0);
        });
    }

    @Test
    void deberiaFallarSiHorasEsNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Seccion("Verduras", -1);
        });
    }

    @Test
    void deberiaCrearTodasLasSeccionesDelEnunciado() {
        Seccion horno = new Seccion("Horno", 8);          
        Seccion cajas = new Seccion("Cajas", 16);          
        Seccion pescaderia = new Seccion("Pescadería", 16);
        Seccion verduras = new Seccion("Verduras", 16);
        Seccion drogueria = new Seccion("Droguería", 16);

        assertEquals("Horno", horno.getNombre());
        assertEquals(8, horno.getHorasNecesarias());
        
        assertEquals("Cajas", cajas.getNombre());
        assertEquals(16, cajas.getHorasNecesarias());
        
        assertEquals("Pescadería", pescaderia.getNombre());
        assertEquals(16, pescaderia.getHorasNecesarias());
    }

    @Test
    void deberiaTenerEqualsBasadoEnNombre() {
        Seccion cajas1 = new Seccion("Cajas", 16);
        Seccion cajas2 = new Seccion("Cajas", 8); 
        Seccion horno = new Seccion("Horno", 8);

        assertEquals(cajas1, cajas2); 
        assertNotEquals(cajas1, horno); 
        assertEquals(cajas1.hashCode(), cajas2.hashCode());
    }
}
