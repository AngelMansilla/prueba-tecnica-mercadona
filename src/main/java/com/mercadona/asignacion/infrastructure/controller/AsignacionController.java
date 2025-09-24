package com.mercadona.asignacion.infrastructure.controller;

import com.mercadona.asignacion.application.port.AsignacionService;
import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.asignacion.infrastructure.controller.dto.ActualizarHorasDto;
import com.mercadona.asignacion.infrastructure.controller.dto.AsignacionDto;
import com.mercadona.asignacion.infrastructure.controller.dto.AsignacionResponseDto;
import com.mercadona.asignacion.infrastructure.controller.dto.HorasSeccionDto;
import com.mercadona.asignacion.infrastructure.controller.dto.HorasTiendaDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    @PostMapping
    public ResponseEntity<AsignacionResponseDto> crearAsignacion(@Valid @RequestBody AsignacionDto asignacionDto) {
        Asignacion asignacionCreada = asignacionService.crearAsignacion(
            asignacionDto.dniTrabajador(),
            asignacionDto.nombreSeccion(),
            asignacionDto.horasAsignadas()
        );
        
        AsignacionResponseDto respuesta = new AsignacionResponseDto(
            asignacionCreada.getTrabajador().getDni(),
            asignacionCreada.getTrabajador().getNombre(),
            asignacionCreada.getSeccion().getNombre(),
            asignacionCreada.getHorasAsignadas(),
            asignacionCreada.getTrabajador().getTienda().getCodigo()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/trabajador/{dni}")
    public ResponseEntity<List<AsignacionResponseDto>> buscarAsignacionesPorTrabajador(@PathVariable String dni) {
        List<Asignacion> asignaciones = asignacionService.buscarAsignacionesPorTrabajador(dni);
        
        List<AsignacionResponseDto> asignacionesDto = asignaciones.stream()
                .map(asignacion -> new AsignacionResponseDto(
                    asignacion.getTrabajador().getDni(),
                    asignacion.getTrabajador().getNombre(),
                    asignacion.getSeccion().getNombre(),
                    asignacion.getHorasAsignadas(),
                    asignacion.getTrabajador().getTienda().getCodigo()
                ))
                .toList();
        
        return ResponseEntity.ok(asignacionesDto);
    }

    @GetMapping("/seccion/{nombreSeccion}")
    public ResponseEntity<List<AsignacionResponseDto>> buscarAsignacionesPorSeccion(@PathVariable String nombreSeccion) {
        List<Asignacion> asignaciones = asignacionService.buscarAsignacionesPorSeccion(nombreSeccion);
        
        List<AsignacionResponseDto> asignacionesDto = asignaciones.stream()
                .map(asignacion -> new AsignacionResponseDto(
                    asignacion.getTrabajador().getDni(),
                    asignacion.getTrabajador().getNombre(),
                    asignacion.getSeccion().getNombre(),
                    asignacion.getHorasAsignadas(),
                    asignacion.getTrabajador().getTienda().getCodigo()
                ))
                .toList();
        
        return ResponseEntity.ok(asignacionesDto);
    }

    @GetMapping("/tienda/{codigoTienda}")
    public ResponseEntity<List<AsignacionResponseDto>> buscarAsignacionesPorTienda(@PathVariable String codigoTienda) {
        List<Asignacion> asignaciones = asignacionService.buscarAsignacionesPorTienda(codigoTienda);
        
        List<AsignacionResponseDto> asignacionesDto = asignaciones.stream()
                .map(asignacion -> new AsignacionResponseDto(
                    asignacion.getTrabajador().getDni(),
                    asignacion.getTrabajador().getNombre(),
                    asignacion.getSeccion().getNombre(),
                    asignacion.getHorasAsignadas(),
                    asignacion.getTrabajador().getTienda().getCodigo()
                ))
                .toList();
        
        return ResponseEntity.ok(asignacionesDto);
    }

    @PutMapping("/trabajador/{dni}/seccion/{nombreSeccion}")
    public ResponseEntity<AsignacionResponseDto> actualizarHorasAsignacion(
            @PathVariable String dni, 
            @PathVariable String nombreSeccion, 
            @Valid @RequestBody ActualizarHorasDto actualizarHorasDto) {
        
        Asignacion asignacionActualizada = asignacionService.actualizarHorasAsignacion(
            dni, nombreSeccion, actualizarHorasDto.nuevasHoras());
        
        AsignacionResponseDto responseDto = new AsignacionResponseDto(
            asignacionActualizada.getTrabajador().getDni(),
            asignacionActualizada.getTrabajador().getNombre(),
            asignacionActualizada.getSeccion().getNombre(),
            asignacionActualizada.getHorasAsignadas(),
            asignacionActualizada.getTrabajador().getTienda().getCodigo()
        );
        
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/trabajador/{dni}/seccion/{nombreSeccion}")
    public ResponseEntity<Void> eliminarAsignacion(@PathVariable String dni, @PathVariable String nombreSeccion) {
        asignacionService.eliminarAsignacion(dni, nombreSeccion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tienda/{codigoTienda}/horas")
    public ResponseEntity<HorasTiendaDto> calcularHorasPorTienda(@PathVariable String codigoTienda) {
        Integer horasTotales = asignacionService.calcularHorasTotalesAsignadasPorTienda(codigoTienda);
        
        HorasTiendaDto respuesta = new HorasTiendaDto(codigoTienda, horasTotales);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/seccion/{nombreSeccion}/horas")
    public ResponseEntity<HorasSeccionDto> calcularHorasPorSeccion(@PathVariable String nombreSeccion) {
        Integer horasTotales = asignacionService.calcularHorasTotalesAsignadasPorSeccion(nombreSeccion);
        
        HorasSeccionDto respuesta = new HorasSeccionDto(nombreSeccion, horasTotales);
        return ResponseEntity.ok(respuesta);
    }
}
