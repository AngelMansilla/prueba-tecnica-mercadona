package com.mercadona.tienda.application.service;

import com.mercadona.tienda.application.port.TiendaService;
import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TiendaServiceImpl implements TiendaService {

    private final TiendaRepository tiendaRepository;

    public TiendaServiceImpl(TiendaRepository tiendaRepository) {
        this.tiendaRepository = tiendaRepository;
    }

    @Override
    public Tienda crearTienda(String codigo, String nombre) {
        validarDatosCreacion(codigo, nombre);
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
}
