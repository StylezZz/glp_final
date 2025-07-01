package pucp.edu.pe.glp_final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pucp.edu.pe.glp_final.entity.PedidoEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, String> {

    List<PedidoEntity> findByEntregado(boolean entregado);

    List<PedidoEntity> findByIdCliente(String idCliente);

    List<PedidoEntity> findByCamionAsignado(String camionAsignado);

    @Query("SELECT p FROM PedidoEntity p WHERE p.entregado = false ORDER BY p.horaRecepcion ASC")
    List<PedidoEntity> findPedidosPendientesOrdenados();

    @Query("SELECT p FROM PedidoEntity p WHERE p.entregado = false AND " +
            "p.horaRecepcion + INTERVAL p.horasLimiteEntrega HOUR < :momento")
    List<PedidoEntity> findPedidosVencidos(@Param("momento") LocalDateTime momento);

    @Query("SELECT p FROM PedidoEntity p WHERE p.entregado = false AND " +
            "p.horaRecepcion + INTERVAL p.horasLimiteEntrega HOUR BETWEEN :inicio AND :fin")
    List<PedidoEntity> findPedidosConVencimientoEntre(@Param("inicio") LocalDateTime inicio,
                                                      @Param("fin") LocalDateTime fin);

    @Query("SELECT p FROM PedidoEntity p WHERE p.horaRecepcion BETWEEN :inicio AND :fin")
    List<PedidoEntity> findPedidosEnRangoFecha(@Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(p) FROM PedidoEntity p WHERE p.entregado = false")
    long countPedidosPendientes();

    @Query("SELECT SUM(p.cantidadGlp) FROM PedidoEntity p WHERE p.entregado = false")
    Double sumCantidadGlpPendiente();
}