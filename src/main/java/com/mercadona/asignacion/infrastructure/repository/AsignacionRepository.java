package com.mercadona.asignacion.infrastructure.repository;

import com.mercadona.asignacion.domain.Asignacion;
import com.mercadona.tienda.domain.Seccion;
import com.mercadona.trabajador.domain.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long>, JpaSpecificationExecutor<Asignacion> {

    List<Asignacion> findByTrabajador(Trabajador trabajador);

    List<Asignacion> findBySeccion(Seccion seccion);

    Optional<Asignacion> findByTrabajadorAndSeccion(Trabajador trabajador, Seccion seccion);

    boolean existsByTrabajadorAndSeccion(Trabajador trabajador, Seccion seccion);

    @Query("SELECT a FROM Asignacion a WHERE a.trabajador.tienda.codigo = :codigoTienda")
    List<Asignacion> findByCodigoTienda(@Param("codigoTienda") String codigoTienda);

    @Query("SELECT a FROM Asignacion a WHERE a.seccion.nombre = :nombreSeccion")
    List<Asignacion> findByNombreSeccion(@Param("nombreSeccion") String nombreSeccion);

    @Query("SELECT a FROM Asignacion a WHERE a.horasAsignadas >= :horasMinimas")
    List<Asignacion> findByHorasAsignadasGreaterThanEqual(@Param("horasMinimas") int horasMinimas);

    @Query("SELECT SUM(a.horasAsignadas) FROM Asignacion a WHERE a.trabajador.tienda.codigo = :codigoTienda")
    Integer sumHorasAsignadasByCodigoTienda(@Param("codigoTienda") String codigoTienda);

    @Query("SELECT SUM(a.horasAsignadas) FROM Asignacion a WHERE a.seccion = :seccion")
    Integer sumHorasAsignadasBySeccion(@Param("seccion") Seccion seccion);

    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.trabajador = :trabajador")
    Long countByTrabajador(@Param("trabajador") Trabajador trabajador);

    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.seccion = :seccion")
    Long countBySeccion(@Param("seccion") Seccion seccion);
}
