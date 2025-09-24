package com.mercadona.trabajador.application.service;

import com.mercadona.tienda.domain.Tienda;
import com.mercadona.tienda.infrastructure.repository.TiendaRepository;
import com.mercadona.trabajador.application.port.TrabajadorService;
import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.trabajador.infrastructure.repository.TrabajadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrabajadorServiceImpl implements TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final TiendaRepository tiendaRepository;

    public TrabajadorServiceImpl(TrabajadorRepository trabajadorRepository, TiendaRepository tiendaRepository) {
        this.trabajadorRepository = trabajadorRepository;
        this.tiendaRepository = tiendaRepository;
    }

    @Override
    public Trabajador crearTrabajador(String dni, String nombre, int horasDisponibles, String codigoTienda) {
        validarDniValido(dni);
        
        Tienda tienda = buscarTiendaPorCodigo(codigoTienda);
        validarDniUnico(dni);
        
        Trabajador nuevoTrabajador = new Trabajador(dni, nombre, horasDisponibles, tienda);
        return trabajadorRepository.save(nuevoTrabajador);
    }

    @Override
    public Optional<Trabajador> buscarPorDni(String dni) {
        return trabajadorRepository.findByDni(dni);
    }

    @Override
    public List<Trabajador> buscarPorTienda(String codigoTienda) {
        return trabajadorRepository.findByCodigoTienda(codigoTienda);
    }

    @Override
    public List<Trabajador> buscarPorNombre(String nombre) {
        return trabajadorRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Trabajador> buscarConHorasMinimas(int horasMinimas) {
        return trabajadorRepository.findByHorasDisponiblesGreaterThanEqual(horasMinimas);
    }

    @Override
    public Long contarTrabajadoresPorTienda(String codigoTienda) {
        Tienda tienda = buscarTiendaPorCodigo(codigoTienda);
        return trabajadorRepository.countByTienda(tienda);
    }

    @Override
    public Integer sumarHorasDisponiblesPorTienda(String codigoTienda) {
        Tienda tienda = buscarTiendaPorCodigo(codigoTienda);
        return trabajadorRepository.sumHorasDisponiblesByTienda(tienda);
    }

    @Override
    public boolean existeTrabajador(String dni) {
        return trabajadorRepository.existsByDni(dni);
    }

    private void validarDniValido(String dni) {
        try {
            // Crear un trabajador temporal solo para validar el DNI
            new Trabajador(dni, "Temp", 8, new Tienda("TEMP", "Temp"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Tienda buscarTiendaPorCodigo(String codigoTienda) {
        return tiendaRepository.findByCodigo(codigoTienda)
            .orElseThrow(() -> new IllegalArgumentException("No existe una tienda con el código: " + codigoTienda));
    }

    private void validarDniUnico(String dni) {
        if (trabajadorRepository.existsByDni(dni)) {
            throw new IllegalArgumentException("Ya existe un trabajador con el DNI: " + dni);
        }
    }
}
