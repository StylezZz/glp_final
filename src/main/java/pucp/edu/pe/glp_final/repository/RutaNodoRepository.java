package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.RutaNodoEntity;

import java.util.List;

@Repository
public interface RutaNodoRepository extends JpaRepository<RutaNodoEntity, Long> {

    List<RutaNodoEntity> findByRutaId(String rutaId);

    @Query("SELECT rn FROM RutaNodoEntity rn WHERE rn.ruta.id = :rutaId ORDER BY rn.ordenSecuencia")
    List<RutaNodoEntity> findByRutaIdOrdenados(@Param("rutaId") String rutaId);

    @Query("SELECT rn FROM RutaNodoEntity rn WHERE rn.ruta.id = :rutaId AND rn.esParada = true ORDER BY rn.ordenSecuencia")
    List<RutaNodoEntity> findParadasByRutaId(@Param("rutaId") String rutaId);
}