package com.mercadona.tienda.infrastructure.repository;

import com.mercadona.tienda.domain.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {
    
    Optional<Seccion> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
}
