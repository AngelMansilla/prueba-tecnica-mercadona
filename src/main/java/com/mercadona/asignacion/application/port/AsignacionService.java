package com.mercadona.asignacion.application.port;

import com.mercadona.asignacion.domain.Asignacion;

import java.util.List;
import java.util.Optional;

public interface AsignacionService {

    Asignacion crearAsignacion(String dniTrabajador, String nombreSeccion, int horasAsignadas);

    Optional<Asignacion> buscarAsignacion(String dniTrabajador, String nombreSeccion);

    List<Asignacion> buscarAsignacionesPorTrabajador(String dniTrabajador);

    List<Asignacion> buscarAsignacionesPorSeccion(String nombreSeccion);

    List<Asignacion> buscarAsignacionesPorTienda(String codigoTienda);

    List<Asignacion> buscarAsignacionesConHorasMinimas(int horasMinimas);

    Integer calcularHorasTotalesAsignadasPorTienda(String codigoTienda);

    Integer calcularHorasTotalesAsignadasPorSeccion(String nombreSeccion);

    Long contarAsignacionesPorTrabajador(String dniTrabajador);

    Long contarAsignacionesPorSeccion(String nombreSeccion);

    boolean existeAsignacion(String dniTrabajador, String nombreSeccion);

    void eliminarAsignacion(String dniTrabajador, String nombreSeccion);
}
