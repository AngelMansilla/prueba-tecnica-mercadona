package com.mercadona.tienda.application.service;

import com.mercadona.tienda.application.port.TiendaService;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.infrastructure.repository.TrabajadorRepository;
import com.mercadona.asignacion.infrastructure.repository.AsignacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TiendaServiceImpl implements TiendaService {

    private final TiendaRepository tiendaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final AsignacionRepository asignacionRepository;

    public TiendaServiceImpl(TiendaRepository tiendaRepository, 
                           TrabajadorRepository trabajadorRepository,
                           AsignacionRepository asignacionRepository) {
        this.tiendaRepository = tiendaRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.asignacionRepository = asignacionRepository;
    }

    @Override
    public Tienda crearTienda(String codigo, String nombre) {
        validarDatosCreacion(codigo, nombre);
        validarFormatoCodigo(codigo);
        validarCodigoUnico(codigo);
        
        Tienda nuevaTienda = new Tienda(codigo, nombre);
        return tiendaRepository.save(nuevaTienda);
    }

    @Override
    public Optional<Tienda> buscarPorCodigo(String codigo) {
        return tiendaRepository.findByCodigo(codigo);
    }

    @Override
    public List<Tienda> buscarPorNombre(String nombre) {
        return tiendaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Tienda> obtenerTodasLasTiendas() {
        return tiendaRepository.findAll();
    }

    @Override
    public Long contarTiendas() {
        return tiendaRepository.countTotalTiendas();
    }

    @Override
    public boolean existeTienda(String codigo) {
        return tiendaRepository.existsByCodigo(codigo);
    }

    @Override
    public Tienda actualizarTienda(String codigo, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        
        Tienda tienda = tiendaRepository.findByCodigo(codigo)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la tienda con código: " + codigo));
            
        tienda.setNombre(nuevoNombre);
        return tiendaRepository.save(tienda);
    }

    @Override
    @Transactional
    public void eliminarTienda(String codigo) {
        Tienda tienda = tiendaRepository.findByCodigo(codigo)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la tienda con código: " + codigo));
            
        // Eliminación en cascada:
        // 1. Eliminar todas las asignaciones de trabajadores de esta tienda
        asignacionRepository.deleteByTrabajadorTiendaId(tienda.getId());
        
        // 2. Eliminar todos los trabajadores de esta tienda
        trabajadorRepository.deleteByTiendaId(tienda.getId());
        
        // 3. Eliminar la tienda
        tiendaRepository.deleteById(tienda.getId());
    }

    private void validarDatosCreacion(String codigo, String nombre) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede ser nulo o vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
    }

    private void validarCodigoUnico(String codigo) {
        if (tiendaRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Ya existe una tienda con el código: " + codigo);
        }
    }

    private void validarFormatoCodigo(String codigo) {
        if (!codigo.matches("T\\d{3}")) {
            throw new IllegalArgumentException("El código debe tener el formato T001-T999");
        }
    }
}
