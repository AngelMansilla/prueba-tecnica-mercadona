package com.mercadona.tienda.application.port;

import com.mercadona.tienda.domain.Tienda;

import java.util.List;
import java.util.Optional;

public interface TiendaService {

    Tienda crearTienda(String codigo, String nombre);

    Optional<Tienda> buscarPorCodigo(String codigo);

    List<Tienda> buscarPorNombre(String nombre);

    List<Tienda> obtenerTodasLasTiendas();

    Long contarTiendas();

    boolean existeTienda(String codigo);

    Tienda actualizarTienda(String codigo, String nuevoNombre);

    void eliminarTienda(String codigo);
}
