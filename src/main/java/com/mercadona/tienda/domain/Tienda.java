package com.mercadona.tienda.domain;

import java.util.Objects;

public class Tienda {
    
    private final String codigo;
    private final String nombre;
    
    public Tienda(String codigo, String nombre) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede ser nulo o vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        this.codigo = codigo;
        this.nombre = nombre;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tienda tienda = (Tienda) obj;
        return Objects.equals(codigo, tienda.codigo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
