package com.mercadona.tienda.domain;

import java.util.Objects;

public class Seccion {
    
    private final String nombre;
    private final int horasNecesarias;
    
    public Seccion(String nombre, int horasNecesarias) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        if (horasNecesarias <= 0) {
            throw new IllegalArgumentException("Las horas necesarias deben ser positivas");
        }
        this.nombre = nombre;
        this.horasNecesarias = horasNecesarias;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getHorasNecesarias() {
        return horasNecesarias;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Seccion seccion = (Seccion) obj;
        return Objects.equals(nombre, seccion.nombre);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
