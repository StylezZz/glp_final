package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucp.edu.pe.glp_final.dto.CrearPedidoRequest;
import pucp.edu.pe.glp_final.dto.PedidoDTO;
import pucp.edu.pe.glp_final.entity.PedidoEntity;
import pucp.edu.pe.glp_final.mapper.EntityMapper;
import pucp.edu.pe.glp_final.model.Pedido;
import pucp.edu.pe.glp_final.repository.PedidoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerTodos() {
        List<PedidoEntity> entities = pedidoRepository.findAll();
        return entityMapper.toPedidoDTOList(entities);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPendientes() {
        List<PedidoEntity> entities = pedidoRepository.findPedidosPendientesOrdenados();
        return entityMapper.toPedidoDTOList(entities);
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPendientesModelo() {
        List<PedidoEntity> entities = pedidoRepository.findPedidosPendientesOrdenados();
        return entityMapper.toPedidoModelList(entities);
    }

    @Transactional(readOnly = true)
    public Optional<PedidoDTO> obtenerPorId(String id) {
        return pedidoRepository.findById(id)
                .map(entityMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPorCliente(String idCliente) {
        List<PedidoEntity> entities = pedidoRepository.findByIdCliente(idCliente);
        return entityMapper.toPedidoDTOList(entities);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerVencidos(LocalDateTime momento) {
        List<PedidoEntity> entities = pedidoRepository.findPedidosVencidos(momento);
        return entityMapper.toPedidoDTOList(entities);
    }

    @Transactional
    public PedidoDTO crearPedido(CrearPedidoRequest request) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setIdCliente(request.getIdCliente());
        entity.setUbicacionX(request.getUbicacion().getX());
        entity.setUbicacionY(request.getUbicacion().getY());
        entity.setCantidadGlp(request.getCantidadGlp());
        entity.setHoraRecepcion(request.getHoraRecepcion() != null ?
                request.getHoraRecepcion() : LocalDateTime.now());
        entity.setHorasLimiteEntrega(request.getHorasLimiteEntrega());
        entity.setEntregado(false);

        entity = pedidoRepository.save(entity);
        log.info("Pedido creado: {} para cliente {}", entity.getId(), entity.getIdCliente());

        return entityMapper.toDTO(entity);
    }

    @Transactional
    public Optional<PedidoDTO> asignarCamion(String pedidoId, String codigoCamion) {
        return pedidoRepository.findById(pedidoId)
                .map(entity -> {
                    entity.setCamionAsignado(codigoCamion);
                    entity = pedidoRepository.save(entity);
                    log.info("Pedido {} asignado a camión {}", pedidoId, codigoCamion);
                    return entityMapper.toDTO(entity);
                });
    }

    @Transactional
    public Optional<PedidoDTO> marcarEntregado(String pedidoId, LocalDateTime horaEntrega) {
        return pedidoRepository.findById(pedidoId)
                .map(entity -> {
                    entity.setEntregado(true);
                    entity.setHoraEntregaReal(horaEntrega);
                    entity = pedidoRepository.save(entity);
                    log.info("Pedido {} marcado como entregado", pedidoId);
                    return entityMapper.toDTO(entity);
                });
    }

    @Transactional(readOnly = true)
    public long contarPendientes() {
        return pedidoRepository.countPedidosPendientes();
    }

    @Transactional(readOnly = true)
    public Double sumaCantidadPendiente() {
        return pedidoRepository.sumCantidadGlpPendiente();
    }

    @Transactional
    public boolean eliminar(String id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            log.info("Pedido eliminado: {}", id);
            return true;
        }
        return false;
    }
}