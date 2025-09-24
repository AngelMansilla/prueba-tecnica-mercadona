package com.mercadona.reporte.application.service;

import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.asignacion.infrastructure.repository.AsignacionRepository;
import com.mercadona.reporte.application.port.ReporteService;
import com.mercadona.reporte.infrastructure.controller.dto.EstadoTiendaDto;
import com.mercadona.reporte.infrastructure.controller.dto.CoberturaHorasDto;
import com.mercadona.reporte.infrastructure.controller.dto.SeccionEstadoDto;
import com.mercadona.reporte.infrastructure.controller.dto.SeccionCoberturaDto;
import com.mercadona.reporte.infrastructure.controller.dto.TrabajadorAsignadoDto;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final TiendaRepository tiendaRepository;
    private final AsignacionRepository asignacionRepository;

    // Secciones predefinidas del sistema
    private static final List<Seccion> SECCIONES_SISTEMA = Arrays.asList(
        new Seccion("Horno", 8),
        new Seccion("Cajas", 16),
        new Seccion("Pescadería", 16),
        new Seccion("Verduras", 16),
        new Seccion("Droguería", 16)
    );

    public ReporteServiceImpl(TiendaRepository tiendaRepository, AsignacionRepository asignacionRepository) {
        this.tiendaRepository = tiendaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Override
    public EstadoTiendaDto obtenerEstadoTienda(String codigoTienda) {
        Tienda tienda = buscarTienda(codigoTienda);
        List<Asignacion> asignaciones = asignacionRepository.findByCodigoTienda(codigoTienda);
        
        List<SeccionEstadoDto> secciones = construirSeccionesEstado(asignaciones);
        
        return new EstadoTiendaDto(tienda.getCodigo(), tienda.getNombre(), secciones);
    }

    @Override
    public CoberturaHorasDto obtenerCoberturaHoras(String codigoTienda) {
        Tienda tienda = buscarTienda(codigoTienda);
        List<Asignacion> asignaciones = asignacionRepository.findByCodigoTienda(codigoTienda);
        
        List<SeccionCoberturaDto> seccionesIncompletas = construirSeccionesCobertura(asignaciones);
        int totalHorasFaltantes = seccionesIncompletas.stream()
            .mapToInt(SeccionCoberturaDto::horasFaltantes)
            .sum();
        
        return new CoberturaHorasDto(
            tienda.getCodigo(), 
            tienda.getNombre(), 
            seccionesIncompletas,
            seccionesIncompletas.size(),
            totalHorasFaltantes
        );
    }

    private Tienda buscarTienda(String codigoTienda) {
        return tiendaRepository.findByCodigo(codigoTienda)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la tienda con código: " + codigoTienda));
    }

    private List<SeccionEstadoDto> construirSeccionesEstado(List<Asignacion> asignaciones) {
        // Agrupar asignaciones por sección
        Map<String, List<Asignacion>> asignacionesPorSeccion = asignaciones.stream()
            .collect(Collectors.groupingBy(a -> a.getSeccion().getNombre()));

        return asignacionesPorSeccion.entrySet().stream()
            .map(entry -> {
                String nombreSeccion = entry.getKey();
                List<Asignacion> asignacionesSeccion = entry.getValue();
                
                List<TrabajadorAsignadoDto> trabajadores = asignacionesSeccion.stream()
                    .map(a -> new TrabajadorAsignadoDto(
                        a.getTrabajador().getDni(),
                        a.getTrabajador().getNombre(),
                        a.getHorasAsignadas()
                    ))
                    .toList();

                return new SeccionEstadoDto(nombreSeccion, trabajadores);
            })
            .toList();
    }

    private List<SeccionCoberturaDto> construirSeccionesCobertura(List<Asignacion> asignaciones) {
        // Agrupar asignaciones por sección
        Map<String, List<Asignacion>> asignacionesPorSeccion = asignaciones.stream()
            .collect(Collectors.groupingBy(a -> a.getSeccion().getNombre()));

        return SECCIONES_SISTEMA.stream()
            .map(seccion -> {
                List<Asignacion> asignacionesSeccion = asignacionesPorSeccion.getOrDefault(
                    seccion.getNombre(), 
                    List.of()
                );
                
                int horasAsignadas = asignacionesSeccion.stream()
                    .mapToInt(Asignacion::getHorasAsignadas)
                    .sum();

                int horasFaltantes = Math.max(0, seccion.getHorasNecesarias() - horasAsignadas);

                return new SeccionCoberturaDto(
                    seccion.getNombre(),
                    seccion.getHorasNecesarias(),
                    horasAsignadas,
                    horasFaltantes
                );
            })
            .filter(seccion -> seccion.horasFaltantes() > 0) // Solo secciones incompletas
            .toList();
    }
}
