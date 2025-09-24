package com.mercadona.asignacion.application.service;

import com.mercadona.asignacion.application.port.AsignacionService;
import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.asignacion.infrastructure.repository.AsignacionRepository;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.trabajador.infrastructure.repository.TrabajadorRepository;
import org.springframework.stereotype.Service;

import com.mercadona.tienda.infrastructure.repository.SeccionRepository;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final SeccionRepository seccionRepository;

    public AsignacionServiceImpl(AsignacionRepository asignacionRepository, 
                               TrabajadorRepository trabajadorRepository,
                               SeccionRepository seccionRepository) {
        this.asignacionRepository = asignacionRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.seccionRepository = seccionRepository;
    }

    @Override
    public Asignacion crearAsignacion(String dniTrabajador, String nombreSeccion, int horasAsignadas) {
        Trabajador trabajador = buscarTrabajadorPorDni(dniTrabajador);
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        
        validarAsignacionUnica(trabajador, seccion);
        validarHorasDisponibles(trabajador, horasAsignadas);
        
        Asignacion nuevaAsignacion = new Asignacion(trabajador, seccion, horasAsignadas);
        return asignacionRepository.save(nuevaAsignacion);
    }

    @Override
    public Optional<Asignacion> buscarAsignacion(String dniTrabajador, String nombreSeccion) {
        Trabajador trabajador = buscarTrabajadorPorDni(dniTrabajador);
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        
        return asignacionRepository.findByTrabajadorAndSeccion(trabajador, seccion);
    }

    @Override
    public List<Asignacion> buscarAsignacionesPorTrabajador(String dniTrabajador) {
        Trabajador trabajador = buscarTrabajadorPorDni(dniTrabajador);
        return asignacionRepository.findByTrabajador(trabajador);
    }

    @Override
    public List<Asignacion> buscarAsignacionesPorSeccion(String nombreSeccion) {
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        return asignacionRepository.findBySeccion(seccion);
    }

    @Override
    public List<Asignacion> buscarAsignacionesPorTienda(String codigoTienda) {
        return asignacionRepository.findByCodigoTienda(codigoTienda);
    }

    @Override
    public List<Asignacion> buscarAsignacionesConHorasMinimas(int horasMinimas) {
        return asignacionRepository.findByHorasAsignadasGreaterThanEqual(horasMinimas);
    }

    @Override
    public Integer calcularHorasTotalesAsignadasPorTienda(String codigoTienda) {
        Integer horas = asignacionRepository.sumHorasAsignadasByCodigoTienda(codigoTienda);
        return horas != null ? horas : 0;
    }

    @Override
    public Integer calcularHorasTotalesAsignadasPorSeccion(String nombreSeccion) {
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        Integer horas = asignacionRepository.sumHorasAsignadasBySeccion(seccion);
        return horas != null ? horas : 0;
    }

    @Override
    public Long contarAsignacionesPorTrabajador(String dniTrabajador) {
        Trabajador trabajador = buscarTrabajadorPorDni(dniTrabajador);
        return asignacionRepository.countByTrabajador(trabajador);
    }

    @Override
    public Long contarAsignacionesPorSeccion(String nombreSeccion) {
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        return asignacionRepository.countBySeccion(seccion);
    }

    @Override
    public boolean existeAsignacion(String dniTrabajador, String nombreSeccion) {
        Trabajador trabajador = buscarTrabajadorPorDni(dniTrabajador);
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        
        return asignacionRepository.existsByTrabajadorAndSeccion(trabajador, seccion);
    }

    @Override
    public void eliminarAsignacion(String dniTrabajador, String nombreSeccion) {
        Trabajador trabajador = buscarTrabajadorPorDni(dniTrabajador);
        Seccion seccion = buscarSeccionPorNombre(nombreSeccion);
        
        Asignacion asignacion = asignacionRepository.findByTrabajadorAndSeccion(trabajador, seccion)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe una asignación para el trabajador " + dniTrabajador + " en la sección " + nombreSeccion));
        
        asignacionRepository.deleteById(asignacion.getId());
    }

    private Trabajador buscarTrabajadorPorDni(String dni) {
        return trabajadorRepository.findByDni(dni)
            .orElseThrow(() -> new IllegalArgumentException("No existe un trabajador con el DNI: " + dni));
    }

    private Seccion buscarSeccionPorNombre(String nombre) {
        return seccionRepository.findByNombre(nombre)
            .orElseThrow(() -> new IllegalArgumentException("No existe una sección con el nombre: " + nombre));
    }

    private void validarAsignacionUnica(Trabajador trabajador, Seccion seccion) {
        if (asignacionRepository.existsByTrabajadorAndSeccion(trabajador, seccion)) {
            throw new IllegalArgumentException(
                "Ya existe una asignación para el trabajador " + trabajador.getDni() + " en la sección " + seccion.getNombre());
        }
    }

    private void validarHorasDisponibles(Trabajador trabajador, int horasAsignadas) {
        if (horasAsignadas > trabajador.getHorasDisponibles()) {
            throw new IllegalArgumentException(
                "Las horas asignadas (" + horasAsignadas + ") no pueden superar las horas disponibles del trabajador (" + trabajador.getHorasDisponibles() + ")");
        }
    }
}
