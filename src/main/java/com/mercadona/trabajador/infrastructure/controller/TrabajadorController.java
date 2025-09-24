package com.mercadona.trabajador.infrastructure.controller;

import com.mercadona.trabajador.application.port.TrabajadorService;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.trabajador.infrastructure.controller.dto.TrabajadorDto;
import com.mercadona.shared.infrastructure.controller.dto.EliminacionResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    public TrabajadorController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }

    @PostMapping
    public ResponseEntity<TrabajadorDto> crearTrabajador(@Valid @RequestBody TrabajadorDto trabajadorDto) {
        Trabajador trabajadorCreado = trabajadorService.crearTrabajador(
            trabajadorDto.dni(),
            trabajadorDto.nombre(),
            trabajadorDto.horasDisponibles(),
            trabajadorDto.codigoTienda()
        );
        
        TrabajadorDto respuesta = new TrabajadorDto(
            trabajadorCreado.getDni(),
            trabajadorCreado.getNombre(),
            trabajadorCreado.getHorasDisponibles(),
            trabajadorCreado.getTienda().getCodigo()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<TrabajadorDto> buscarTrabajador(@PathVariable String dni) {
        return trabajadorService.buscarPorDni(dni)
                .map(trabajador -> {
                    TrabajadorDto dto = new TrabajadorDto(
                        trabajador.getDni(),
                        trabajador.getNombre(),
                        trabajador.getHorasDisponibles(),
                        trabajador.getTienda().getCodigo()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TrabajadorDto>> listarTrabajadores(
            @RequestParam(value = "tienda", required = false) String codigoTienda,
            @RequestParam(value = "nombre", required = false, defaultValue = "") String nombre) {
        
        List<Trabajador> trabajadores;
        
        if (codigoTienda != null) {
            trabajadores = trabajadorService.buscarPorTienda(codigoTienda);
        } else {
            trabajadores = trabajadorService.buscarPorNombre(nombre);
        }
        
        List<TrabajadorDto> trabajadoresDto = trabajadores.stream()
                .map(trabajador -> new TrabajadorDto(
                    trabajador.getDni(),
                    trabajador.getNombre(),
                    trabajador.getHorasDisponibles(),
                    trabajador.getTienda().getCodigo()
                ))
                .toList();
        
        return ResponseEntity.ok(trabajadoresDto);
    }

    @PutMapping("/{dni}")
    public ResponseEntity<TrabajadorDto> actualizarTrabajador(@PathVariable String dni, 
                                                              @Valid @RequestBody TrabajadorDto trabajadorDto) {
        Trabajador trabajadorActualizado = trabajadorService.actualizarTrabajador(
            dni, 
            trabajadorDto.nombre(), 
            trabajadorDto.horasDisponibles()
        );

        TrabajadorDto respuesta = new TrabajadorDto(
            trabajadorActualizado.getDni(),
            trabajadorActualizado.getNombre(),
            trabajadorActualizado.getHorasDisponibles(),
            trabajadorActualizado.getTienda().getCodigo()
        );

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<EliminacionResponseDto> eliminarTrabajador(@PathVariable String dni) {
        trabajadorService.eliminarTrabajador(dni);
        EliminacionResponseDto respuesta = EliminacionResponseDto.crear("Trabajador", dni);
        return ResponseEntity.ok(respuesta);
    }
}
