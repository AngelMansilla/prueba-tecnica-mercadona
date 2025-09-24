package com.mercadona.tienda.infrastructure.repository;

import com.mercadona.tienda.domain.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda, Long>, JpaSpecificationExecutor<Tienda> {

    Optional<Tienda> findByCodigo(String codigo);


    boolean existsByCodigo(String codigo);


    @Query("SELECT t FROM Tienda t WHERE LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    java.util.List<Tienda> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT COUNT(t) FROM Tienda t")
    Long countTotalTiendas();
}
