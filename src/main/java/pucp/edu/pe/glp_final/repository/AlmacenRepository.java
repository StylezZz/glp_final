package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.AlmacenEntity;
import pucp.edu.pe.glp_final.model.enums.TipoAlmacen;

import java.util.List;

@Repository
public interface AlmacenRepository extends JpaRepository<AlmacenEntity, String> {

    List<AlmacenEntity> findByTipo(TipoAlmacen tipo);

    AlmacenEntity findByTipoAndUbicacionXAndUbicacionY(TipoAlmacen tipo, Integer x, Integer y);
}