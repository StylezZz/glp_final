package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.RutaPedidoEntity;

import java.util.List;

@Repository
public interface RutaPedidoRepository extends JpaRepository<RutaPedidoEntity, Long> {

    List<RutaPedidoEntity> findByRutaId(String rutaId);

    List<RutaPedidoEntity> findByPedidoId(String pedidoId);

    @Query("SELECT rp FROM RutaPedidoEntity rp WHERE rp.ruta.id = :rutaId ORDER BY rp.ordenEntrega")
    List<RutaPedidoEntity> findByRutaIdOrdenados(@Param("rutaId") String rutaId);
}