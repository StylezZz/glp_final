package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.BloqueoEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloqueoRepository extends JpaRepository<BloqueoEntity, String> {

    List<BloqueoEntity> findByActivo(boolean activo);

    @Query("SELECT b FROM BloqueoEntity b WHERE b.activo = true AND " +
            "b.horaInicio <= :momento AND b.horaFin >= :momento")
    List<BloqueoEntity> findBloqueosActivosEnMomento(@Param("momento") LocalDateTime momento);

    @Query("SELECT b FROM BloqueoEntity b WHERE b.activo = true AND " +
            "((b.horaInicio BETWEEN :inicio AND :fin) OR " +
            "(b.horaFin BETWEEN :inicio AND :fin) OR " +
            "(b.horaInicio <= :inicio AND b.horaFin >= :fin))")
    List<BloqueoEntity> findBloqueosEnRango(@Param("inicio") LocalDateTime inicio,
                                            @Param("fin") LocalDateTime fin);
}