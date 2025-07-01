
package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.CamionEntity;
import pucp.edu.pe.glp_final.model.enums.EstadoCamion;
import pucp.edu.pe.glp_final.model.enums.TipoCamion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CamionRepository extends JpaRepository<CamionEntity, String> {

    List<CamionEntity> findByEstado(EstadoCamion estado);

    List<CamionEntity> findByTipo(TipoCamion tipo);

    List<CamionEntity> findByEstadoIn(List<EstadoCamion> estados);

    @Query("SELECT c FROM CamionEntity c WHERE c.fechaProximoMantenimiento <= :fecha AND c.enMantenimiento = false")
    List<CamionEntity> findCamionesParaMantenimiento(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT c FROM CamionEntity c WHERE c.averiado = true")
    List<CamionEntity> findCamionesAveriados();

    @Query("SELECT c FROM CamionEntity c WHERE c.estado = 'DISPONIBLE' ORDER BY c.nivelCombustibleActual DESC")
    List<CamionEntity> findDisponiblesOrdenadosPorCombustible();
}