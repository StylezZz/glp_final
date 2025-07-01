package pucp.edu.pe.glp_final.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import pucp.edu.pe.glp_final.dto.*;
import pucp.edu.pe.glp_final.entity.*;
import pucp.edu.pe.glp_final.model.*;
import pucp.edu.pe.glp_final.model.enums.TipoCamion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========================================
    // CAMION MAPPERS
    // ========================================

    public CamionDTO toDTO(CamionEntity entity) {
        if (entity == null) return null;

        CamionDTO dto = new CamionDTO();
        dto.setCodigo(entity.getCodigo());
        dto.setTipo(entity.getTipo());
        dto.setUbicacion(new UbicacionDTO(entity.getUbicacionX(), entity.getUbicacionY()));
        dto.setEstado(entity.getEstado());
        dto.setNivelGlpActual(entity.getNivelGlpActual());
        dto.setNivelCombustibleActual(entity.getNivelCombustibleActual());
        dto.setEnMantenimiento(entity.getEnMantenimiento());
        dto.setAveriado(entity.getAveriado());
        dto.setMotivoEstado(entity.getMotivoEstado());
        dto.setFechaUltimoMantenimiento(entity.getFechaUltimoMantenimiento());
        dto.setFechaProximoMantenimiento(entity.getFechaProximoMantenimiento());

        // Calcular capacidades según tipo
        dto.setCapacidadTanqueGLP(getCapacidadPorTipo(entity.getTipo()));
        dto.setPesoTara(getPesoTaraPorTipo(entity.getTipo()));

        return dto;
    }

    public CamionEntity toEntity(CamionDTO dto) {
        if (dto == null) return null;

        CamionEntity entity = new CamionEntity();
        entity.setCodigo(dto.getCodigo());
        entity.setTipo(dto.getTipo());
        entity.setUbicacionX(dto.getUbicacion().getX());
        entity.setUbicacionY(dto.getUbicacion().getY());
        entity.setEstado(dto.getEstado());
        entity.setNivelGlpActual(dto.getNivelGlpActual());
        entity.setNivelCombustibleActual(dto.getNivelCombustibleActual());
        entity.setEnMantenimiento(dto.getEnMantenimiento());
        entity.setAveriado(dto.getAveriado());
        entity.setMotivoEstado(dto.getMotivoEstado());
        entity.setFechaUltimoMantenimiento(dto.getFechaUltimoMantenimiento());
        entity.setFechaProximoMantenimiento(dto.getFechaProximoMantenimiento());

        return entity;
    }

    public Camion toModel(CamionEntity entity) {
        if (entity == null) return null;

        Ubicacion ubicacion = new Ubicacion(entity.getUbicacionX(), entity.getUbicacionY());
        Camion camion = new Camion(entity.getCodigo(), entity.getTipo(), ubicacion);

        camion.setEstado(entity.getEstado());
        camion.setNivelGLPActual(entity.getNivelGlpActual());
        camion.setNivelCombustibleActual(entity.getNivelCombustibleActual());
        camion.setEnMantenimiento(entity.getEnMantenimiento());
        camion.setAveriado(entity.getAveriado());
        camion.setMotivoEstado(entity.getMotivoEstado());
        camion.setFechaUltimoMantenimiento(entity.getFechaUltimoMantenimiento());
        camion.setFechaProximoMantenimiento(entity.getFechaProximoMantenimiento());

        return camion;
    }

    private Double getCapacidadPorTipo(TipoCamion tipo) {
        return switch (tipo) {
            case TA -> 25.0;
            case TB -> 15.0;
            case TC -> 10.0;
            case TD -> 5.0;
        };
    }

    private Double getPesoTaraPorTipo(TipoCamion tipo) {
        return switch (tipo) {
            case TA -> 2.5;
            case TB -> 2.0;
            case TC -> 1.5;
            case TD -> 1.0;
        };
    }

    // ========================================
    // PEDIDO MAPPERS
    // ========================================

    public PedidoDTO toDTO(PedidoEntity entity) {
        if (entity == null) return null;

        PedidoDTO dto = new PedidoDTO();
        dto.setId(entity.getId());
        dto.setIdCliente(entity.getIdCliente());
        dto.setUbicacion(new UbicacionDTO(entity.getUbicacionX(), entity.getUbicacionY()));
        dto.setCantidadGlp(entity.getCantidadGlp());
        dto.setHorasLimiteEntrega(entity.getHorasLimiteEntrega());
        dto.setHoraRecepcion(entity.getHoraRecepcion());
        dto.setHoraEntregaProgramada(entity.getHoraEntregaProgramada());
        dto.setHoraEntregaReal(entity.getHoraEntregaReal());
        dto.setCamionAsignado(entity.getCamionAsignado());
        dto.setEntregado(entity.getEntregado());
        dto.setHoraLimiteEntrega(entity.getHoraLimiteEntrega());

        return dto;
    }

    public PedidoEntity toEntity(PedidoDTO dto) {
        if (dto == null) return null;

        PedidoEntity entity = new PedidoEntity();
        entity.setId(dto.getId());
        entity.setIdCliente(dto.getIdCliente());
        entity.setUbicacionX(dto.getUbicacion().getX());
        entity.setUbicacionY(dto.getUbicacion().getY());
        entity.setCantidadGlp(dto.getCantidadGlp());
        entity.setHorasLimiteEntrega(dto.getHorasLimiteEntrega());
        entity.setHoraRecepcion(dto.getHoraRecepcion());
        entity.setHoraEntregaProgramada(dto.getHoraEntregaProgramada());
        entity.setHoraEntregaReal(dto.getHoraEntregaReal());
        entity.setCamionAsignado(dto.getCamionAsignado());
        entity.setEntregado(dto.getEntregado());

        return entity;
    }

    public Pedido toModel(PedidoEntity entity) {
        if (entity == null) return null;

        Ubicacion ubicacion = new Ubicacion(entity.getUbicacionX(), entity.getUbicacionY());
        Pedido pedido = new Pedido(
                entity.getId(),
                entity.getIdCliente(),
                ubicacion,
                entity.getCantidadGlp(),
                entity.getHoraRecepcion(),
                entity.getHorasLimiteEntrega()
        );

        pedido.setHoraEntregaProgramada(entity.getHoraEntregaProgramada());
        pedido.setHoraEntregaReal(entity.getHoraEntregaReal());
        pedido.setCamionAsignado(entity.getCamionAsignado());
        pedido.setEntregado(entity.getEntregado());

        return pedido;
    }

    // ========================================
    // RUTA MAPPERS
    // ========================================

    public RutaDTO toDTO(RutaEntity entity, List<RutaPedidoEntity> rutaPedidos, List<RutaNodoEntity> rutaNodos) {
        if (entity == null) return null;

        RutaDTO dto = new RutaDTO();
        dto.setId(entity.getId());
        dto.setCodigoCamion(entity.getCodigoCamion());
        dto.setOrigen(new UbicacionDTO(entity.getOrigenX(), entity.getOrigenY()));
        dto.setDestino(entity.getDestinoX() != null ?
                new UbicacionDTO(entity.getDestinoX(), entity.getDestinoY()) : null);
        dto.setDistanciaTotal(entity.getDistanciaTotal());
        dto.setConsumoCombustible(entity.getConsumoCombustible());
        dto.setCompletada(entity.getCompletada());
        dto.setCancelada(entity.getCancelada());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFinEstimada(entity.getHoraFinEstimada());
        dto.setHoraFinReal(entity.getHoraFinReal());

        // Mapear pedidos asignados
        if (rutaPedidos != null) {
            dto.setPedidosAsignados(rutaPedidos.stream()
                    .map(rp -> toDTO(rp.getPedido()))
                    .collect(Collectors.toList()));
        }

        // Mapear nodos
        if (rutaNodos != null) {
            dto.setSecuenciaNodos(rutaNodos.stream()
                    .map(rn -> new UbicacionDTO(rn.getUbicacionX(), rn.getUbicacionY()))
                    .collect(Collectors.toList()));

            dto.setSecuenciaParadas(rutaNodos.stream()
                    .filter(RutaNodoEntity::getEsParada)
                    .map(rn -> new UbicacionDTO(rn.getUbicacionX(), rn.getUbicacionY()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // ========================================
    // BLOQUEO MAPPERS
    // ========================================

    public BloqueoDTO toDTO(BloqueoEntity entity) {
        if (entity == null) return null;

        BloqueoDTO dto = new BloqueoDTO();
        dto.setId(entity.getId());
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFin(entity.getHoraFin());
        dto.setActivo(entity.getActivo());

        // Deserializar nodos bloqueados desde JSON
        try {
            List<UbicacionDTO> nodos = objectMapper.readValue(
                    entity.getNodosBloqueados(),
                    new TypeReference<List<UbicacionDTO>>() {}
            );
            dto.setNodosBloqueados(nodos);
        } catch (JsonProcessingException e) {
            dto.setNodosBloqueados(new ArrayList<>());
        }

        return dto;
    }

    public BloqueoEntity toEntity(BloqueoDTO dto) {
        if (dto == null) return null;

        BloqueoEntity entity = new BloqueoEntity();
        entity.setId(dto.getId());
        entity.setHoraInicio(dto.getHoraInicio());
        entity.setHoraFin(dto.getHoraFin());
        entity.setActivo(dto.getActivo());

        // Serializar nodos bloqueados a JSON
        try {
            String nodosJson = objectMapper.writeValueAsString(dto.getNodosBloqueados());
            entity.setNodosBloqueados(nodosJson);
        } catch (JsonProcessingException e) {
            entity.setNodosBloqueados("[]");
        }

        return entity;
    }

    // ========================================
    // ALMACEN MAPPERS
    // ========================================

    public AlmacenDTO toDTO(AlmacenEntity entity) {
        if (entity == null) return null;

        AlmacenDTO dto = new AlmacenDTO();
        dto.setId(entity.getId());
        dto.setUbicacion(new UbicacionDTO(entity.getUbicacionX(), entity.getUbicacionY()));
        dto.setTipo(entity.getTipo());
        dto.setCapacidadMaxima(entity.getCapacidadMaxima());
        dto.setNivelActual(entity.getNivelActual());

        // Calcular porcentaje de ocupación
        if (entity.getCapacidadMaxima() > 0) {
            dto.setPorcentajeOcupacion((entity.getNivelActual() / entity.getCapacidadMaxima()) * 100);
        } else {
            dto.setPorcentajeOcupacion(0.0);
        }

        return dto;
    }

    public AlmacenEntity toEntity(AlmacenDTO dto) {
        if (dto == null) return null;

        AlmacenEntity entity = new AlmacenEntity();
        entity.setId(dto.getId());
        entity.setUbicacionX(dto.getUbicacion().getX());
        entity.setUbicacionY(dto.getUbicacion().getY());
        entity.setTipo(dto.getTipo());
        entity.setCapacidadMaxima(dto.getCapacidadMaxima());
        entity.setNivelActual(dto.getNivelActual());

        return entity;
    }

    public Almacen toModel(AlmacenEntity entity) {
        if (entity == null) return null;

        Ubicacion ubicacion = new Ubicacion(entity.getUbicacionX(), entity.getUbicacionY());
        Almacen almacen = new Almacen(entity.getId(), ubicacion, entity.getTipo(), entity.getCapacidadMaxima());
        almacen.setNivelActual(entity.getNivelActual());

        return almacen;
    }

    // ========================================
    // LISTA MAPPERS
    // ========================================

    public List<CamionDTO> toCamionDTOList(List<CamionEntity> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PedidoDTO> toPedidoDTOList(List<PedidoEntity> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AlmacenDTO> toAlmacenDTOList(List<AlmacenEntity> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BloqueoDTO> toBloqueoDTOList(List<BloqueoEntity> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Camion> toCamionModelList(List<CamionEntity> entities) {
        return entities.stream().map(this::toModel).collect(Collectors.toList());
    }

    public List<Pedido> toPedidoModelList(List<PedidoEntity> entities) {
        return entities.stream().map(this::toModel).collect(Collectors.toList());
    }
}