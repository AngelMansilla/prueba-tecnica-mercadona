package com.mercadona.asignacion.domain;

import com.mercadona.tienda.domain.Seccion;
import com.mercadona.trabajador.domain.Trabajador;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id", nullable = false)
    private Trabajador trabajador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;
    
    @Column(name = "horas_asignadas", nullable = false)
    private int horasAsignadas;

    protected Asignacion() {
        // Constructor vacío requerido por JPA
    }

    public Asignacion(Trabajador trabajador, Seccion seccion, int horasAsignadas) {
        if (trabajador == null) {
            throw new IllegalArgumentException("El trabajador no puede ser nulo");
        }
        if (seccion == null) {
            throw new IllegalArgumentException("La sección no puede ser nula");
        }
        if (horasAsignadas <= 0) {
            throw new IllegalArgumentException("Las horas asignadas deben ser positivas");
        }

        this.trabajador = trabajador;
        this.seccion = seccion;
        this.horasAsignadas = horasAsignadas;
    }

    public Long getId() {
        return id;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public int getHorasAsignadas() {
        return horasAsignadas;
    }

    public void setHorasAsignadas(int horasAsignadas) {
        if (horasAsignadas < 1 || horasAsignadas > 8) {
            throw new IllegalArgumentException("Las horas asignadas deben estar entre 1 y 8");
        }
        this.horasAsignadas = horasAsignadas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asignacion that = (Asignacion) o;
        return Objects.equals(trabajador, that.trabajador) &&
               Objects.equals(seccion, that.seccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trabajador, seccion);
    }
}
