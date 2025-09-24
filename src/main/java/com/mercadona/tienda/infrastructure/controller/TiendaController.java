package com.mercadona.tienda.infrastructure.controller;

import com.mercadona.tienda.application.port.TiendaService;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.controller.dto.TiendaDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiendas")
public class TiendaController {

    private final TiendaService tiendaService;

    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    @PostMapping
    public ResponseEntity<TiendaDto> crearTienda(@Valid @RequestBody TiendaDto tiendaDto) {
        Tienda tiendaCreada = tiendaService.crearTienda(tiendaDto.codigo(), tiendaDto.nombre());
        
        TiendaDto respuesta = new TiendaDto(tiendaCreada.getCodigo(), tiendaCreada.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<TiendaDto> buscarTienda(@PathVariable String codigo) {
        return tiendaService.buscarPorCodigo(codigo)
                .map(tienda -> {
                    TiendaDto dto = new TiendaDto(tienda.getCodigo(), tienda.getNombre());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TiendaDto>> listarTiendas(
            @RequestParam(value = "nombre", required = false, defaultValue = "") String nombre) {
        
        List<Tienda> tiendas = nombre.isEmpty() ? 
            tiendaService.obtenerTodasLasTiendas() : 
            tiendaService.buscarPorNombre(nombre);
            
        List<TiendaDto> tiendasDto = tiendas.stream()
                .map(tienda -> new TiendaDto(tienda.getCodigo(), tienda.getNombre()))
                .toList();
        
        return ResponseEntity.ok(tiendasDto);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<TiendaDto> actualizarTienda(@PathVariable String codigo, 
                                                      @Valid @RequestBody TiendaDto tiendaDto) {
        Tienda tiendaActualizada = tiendaService.actualizarTienda(codigo, tiendaDto.nombre());
        
        TiendaDto respuesta = new TiendaDto(tiendaActualizada.getCodigo(), tiendaActualizada.getNombre());
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminarTienda(@PathVariable String codigo) {
        tiendaService.eliminarTienda(codigo);
        return ResponseEntity.noContent().build();
    }
}
