package com.mercadona.tienda.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tiendas")
public class Tienda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    protected Tienda() {
        // Constructor vacío requerido por JPA
    }
    
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
    
    public Long getId() {
        return id;
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
