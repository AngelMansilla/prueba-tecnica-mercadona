package com.mercadona;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Sistema de gestión de Mercadona",
        version = "1.0.0",
        description = "Sistema de gestión de tiendas y trabajadores para Mercadona"
    )
)
public class MercadonaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MercadonaApplication.class, args);
    }
}
