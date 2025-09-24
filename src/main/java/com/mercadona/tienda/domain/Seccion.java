package com.mercadona.tienda.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "secciones")
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", unique = true, nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "horas_necesarias", nullable = false)
    private int horasNecesarias;
    
    protected Seccion() {
        // Constructor vacío requerido por JPA
    }
    
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

    public Long getId() {
        return id;
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
