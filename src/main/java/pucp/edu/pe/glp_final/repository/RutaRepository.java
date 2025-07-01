package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.RutaEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<RutaEntity, String> {

    List<RutaEntity> findByCodigoCamion(String codigoCamion);

    List<RutaEntity> findByCompletada(boolean completada);

    @Query("SELECT r FROM RutaEntity r WHERE r.completada = false AND r.cancelada = false")
    List<RutaEntity> findRutasActivas();

    @Query("SELECT r FROM RutaEntity r WHERE r.horaInicio BETWEEN :inicio AND :fin")
    List<RutaEntity> findRutasEnRangoFecha(@Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM RutaEntity r WHERE r.codigoCamion = :camion AND r.completada = false")
    List<RutaEntity> findRutasActivasPorCamion(@Param("camion") String codigoCamion);

    @Query("SELECT COUNT(r) FROM RutaEntity r WHERE r.completada = false")
    long countRutasActivas();
}