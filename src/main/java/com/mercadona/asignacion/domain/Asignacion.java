package com.mercadona.asignacion.domain;

import com.mercadona.tienda.domain.Seccion;
import com.mercadona.trabajador.domain.Trabajador;
import java.util.Objects;

public class Asignacion {

    private final Trabajador trabajador;
    private final Seccion seccion;
    private final int horasAsignadas;

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

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public int getHorasAsignadas() {
        return horasAsignadas;
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
