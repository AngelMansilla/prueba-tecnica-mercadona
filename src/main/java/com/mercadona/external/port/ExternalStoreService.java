package com.mercadona.external.port;

import com.mercadona.external.dto.ExternalStoreDto;
import java.util.Optional;

public interface ExternalStoreService {
    
    /**
     * Busca información de una tienda en la API externa por nombre
     * Busca coincidencias en el campo 'description' de la API
     */
    Optional<ExternalStoreDto> buscarTiendaPorNombre(String nombreTienda);
}
