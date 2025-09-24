package com.mercadona.trabajador.application.port;

import com.mercadona.trabajador.domain.Trabajador;

import java.util.List;
import java.util.Optional;

public interface TrabajadorService {

    Trabajador crearTrabajador(String dni, String nombre, int horasDisponibles, String codigoTienda);

    Optional<Trabajador> buscarPorDni(String dni);

    List<Trabajador> buscarPorTienda(String codigoTienda);

    List<Trabajador> buscarPorNombre(String nombre);

    List<Trabajador> buscarConHorasMinimas(int horasMinimas);

    Long contarTrabajadoresPorTienda(String codigoTienda);

    Integer sumarHorasDisponiblesPorTienda(String codigoTienda);

    boolean existeTrabajador(String dni);
}
