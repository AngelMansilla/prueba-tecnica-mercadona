package com.mercadona.trabajador.infrastructure.repository;

import com.mercadona.trabajador.domain.Trabajador;
import com.mercadona.tienda.domain.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Long>, JpaSpecificationExecutor<Trabajador> {

    Optional<Trabajador> findByDni(String dni);

    boolean existsByDni(String dni);

    List<Trabajador> findByTienda(Tienda tienda);

    @Query("SELECT t FROM Trabajador t WHERE t.tienda.codigo = :codigoTienda")
    List<Trabajador> findByCodigoTienda(@Param("codigoTienda") String codigoTienda);

    @Query("SELECT t FROM Trabajador t WHERE t.horasDisponibles >= :horasMinimas")
    List<Trabajador> findByHorasDisponiblesGreaterThanEqual(@Param("horasMinimas") int horasMinimas);

    @Query("SELECT t FROM Trabajador t WHERE LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Trabajador> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT COUNT(t) FROM Trabajador t WHERE t.tienda = :tienda")
    Long countByTienda(@Param("tienda") Tienda tienda);

    @Query("SELECT SUM(t.horasDisponibles) FROM Trabajador t WHERE t.tienda = :tienda")
    Integer sumHorasDisponiblesByTienda(@Param("tienda") Tienda tienda);

    void deleteByTiendaId(Long tiendaId);
}
