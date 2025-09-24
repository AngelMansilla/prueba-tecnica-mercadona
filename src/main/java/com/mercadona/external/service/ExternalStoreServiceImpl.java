package com.mercadona.external.service;

import com.mercadona.external.dto.ExternalStoreDto;
import com.mercadona.external.dto.ExternalStoreResponseDto;
import com.mercadona.external.port.ExternalStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Service
public class ExternalStoreServiceImpl implements ExternalStoreService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ExternalStoreServiceImpl(RestTemplate restTemplate, 
                                   @Value("${external.stores.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<ExternalStoreDto> buscarTiendaPorNombre(String nombreTienda) {
        try {
            // Obtener todas las tiendas y buscar por coincidencia de nombre
            String url = baseUrl + "/stores?size=50";
            ExternalStoreResponseDto response = restTemplate.getForObject(url, ExternalStoreResponseDto.class);
            
            if (response != null && response.content() != null) {
                return response.content().stream()
                    .filter(store -> coincideNombre(store.description(), nombreTienda))
                    .findFirst();
            }
            
            return Optional.empty();
            
        } catch (RestClientException e) {
            System.err.println("Error consultando API externa para tienda " + nombreTienda + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    private boolean coincideNombre(String apiDescription, String nombreTienda) {
        if (apiDescription == null || nombreTienda == null) {
            return false;
        }
        
        String apiLimpio = apiDescription.trim();
        String tiendaLimpia = nombreTienda.trim();
        
        System.out.println("  🔗 Comparando: '" + apiLimpio + "' == '" + tiendaLimpia + "' -> " + 
                          apiLimpio.equalsIgnoreCase(tiendaLimpia));
        
        return apiLimpio.equalsIgnoreCase(tiendaLimpia);
    }
}
