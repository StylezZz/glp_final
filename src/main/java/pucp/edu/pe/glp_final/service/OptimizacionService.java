package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucp.edu.pe.glp_final.algorithm.Genetico;
import pucp.edu.pe.glp_final.dto.*;
import pucp.edu.pe.glp_final.entity.*;
import pucp.edu.pe.glp_final.mapper.*;
import pucp.edu.pe.glp_final.model.*;
import pucp.edu.pe.glp_final.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimizacionService {

    private final CamionRepository camionRepository;
    private final PedidoRepository pedidoRepository;
    private final RutaRepository rutaRepository;
    private final UbicacionService ubicacionService;
    private final WebSocketService webSocketService;
    private final CamionMapper camionMapper;
    private final PedidoMapper pedidoMapper;
    private final RutaMapper rutaMapper;

    @Transactional
    public OptimizacionResponse optimizarRutas(OptimizacionRequest request) {
        long inicioTiempo = System.currentTimeMillis();

        try {
            log.info("Iniciando optimización de rutas con algoritmo: {}", request.getAlgoritmo());

            // Obtener datos actuales desde la base de datos
            List<CamionEntity> camionesEntities = camionRepository.findByEstadoId("DISPONIBLE");
            List<PedidoEntity> pedidosEntities = pedidoRepository.findPedidosPendientesOrdenados();

            if (camionesEntities.isEmpty()) {
                return crearRespuestaError("No hay camiones disponibles", inicioTiempo);
            }

            if (pedidosEntities.isEmpty()) {
                return crearRespuestaExito("No hay pedidos pendientes", new ArrayList<>(), 0.0, inicioTiempo);
            }

            // Convertir entidades a modelos para el algoritmo
            List<Camion> camionesDisponibles = convertirCamionesAModelo(camionesEntities);
            List<Pedido> pedidosPendientes = convertirPedidosAModelo(pedidosEntities);
            Mapa mapa = crearMapaActual(request.getMomentoActual());

            // Ejecutar algoritmo de optimización
            List<Ruta> rutasOptimizadas = ejecutarAlgoritmoGenetico(
                    camionesDisponibles, pedidosPendientes, mapa,
                    request.getMomentoActual(), request.getParametros()
            );

            // Guardar rutas en base de datos
            List<RutaDTO> rutasDTO = guardarRutasEnBD(rutasOptimizadas, request.getMomentoActual());

            // Calcular métricas
            double fitnessTotal = calcularFitnessTotal(rutasOptimizadas);
            int pedidosAsignados = contarPedidosAsignados(rutasOptimizadas);
            int pedidosNoAsignados = pedidosPendientes.size() - pedidosAsignados;

            // Notificar por WebSocket
            webSocketService.notificarOptimizacionCompletada(rutasDTO, pedidosAsignados);

            long tiempoEjecucion = System.currentTimeMillis() - inicioTiempo;
            log.info("Optimización completada en {}ms. Rutas generadas: {}, Pedidos asignados: {}",
                    tiempoEjecucion, rutasDTO.size(), pedidosAsignados);

            OptimizacionResponse response = new OptimizacionResponse();
            response.setExito(true);
            response.setMensaje("Optimización completada exitosamente");
            response.setRutasGeneradas(rutasDTO);
            response.setFitnessTotal(fitnessTotal);
            response.setTiempoEjecucionMs(tiempoEjecucion);
            response.setPedidosAsignados(pedidosAsignados);
            response.setPedidosNoAsignados(pedidosNoAsignados);

            return response;

        } catch (Exception e) {
            log.error("Error durante la optimización", e);
            return crearRespuestaError("Error durante optimización: " + e.getMessage(), inicioTiempo);
        }
    }

    private List<Camion> convertirCamionesAModelo(List<CamionEntity> entities) {
        return entities.stream().map(entity -> {
            Ubicacion ubicacion = new Ubicacion(
                    entity.getUbicacionActual().getX(),
                    entity.getUbicacionActual().getY()
            );

            // Determinar tipo enum
            pucp.edu.pe.glp_final.model.enums.TipoCamion tipo =
                    pucp.edu.pe.glp_final.model.enums.TipoCamion.valueOf(entity.getTipoId());

            Camion camion = new Camion(entity.getIdCamion(), tipo, ubicacion);

            // Mapear estado enum
            pucp.edu.pe.glp_final.model.enums.EstadoCamion estado =
                    pucp.edu.pe.glp_final.model.enums.EstadoCamion.valueOf(entity.getEstadoId());
            camion.setEstado(estado);

            camion.setNivelGLPActual(entity.getNivelGlpActual());
            camion.setNivelCombustibleActual(entity.getNivelCombustibleActual());
            camion.setEnMantenimiento(entity.getEnMantenimiento());
            camion.setAveriado(entity.getAveriado());

            return camion;
        }).collect(Collectors.toList());
    }

    private List<Pedido> convertirPedidosAModelo(List<PedidoEntity> entities) {
        return entities.stream().map(entity -> {
            Ubicacion ubicacion = new Ubicacion(entity.getX(), entity.getY());

            Pedido pedido = new Pedido(
                    entity.getIdPedido(),
                    entity.getIdCliente(),
                    ubicacion,
                    entity.getCantidadGlp(),
                    entity.getHoraRecepcion(),
                    entity.getTiempoLimiteEntrega()
            );

            pedido.setHoraEntregaProgramada(entity.getHoraEntregaProgramada());
            pedido.setHoraEntregaReal(entity.getHoraEntregaReal());
            if (entity.getCamionAsignado() != null) {
                pedido.setCamionAsignado(entity.getCamionAsignado().getIdCamion());
            }
            pedido.setEntregado(entity.getEntregado());

            return pedido;
        }).collect(Collectors.toList());
    }

    private Mapa crearMapaActual(LocalDateTime momento) {
        Mapa mapa = new Mapa(70, 50); // Según enunciado

        // Crear almacenes
        Almacen central = new Almacen("CENTRAL", new Ubicacion(12, 8),
                pucp.edu.pe.glp_final.model.enums.TipoAlmacen.PRINCIPAL, Double.MAX_VALUE);

        Almacen norte = new Almacen("NORTE", new Ubicacion(42, 42),
                pucp.edu.pe.glp_final.model.enums.TipoAlmacen.INTERMEDIO, 160.0);

        Almacen este = new Almacen("ESTE", new Ubicacion(63, 3),
                pucp.edu.pe.glp_final.model.enums.TipoAlmacen.INTERMEDIO, 160.0);

        mapa.getAlmacenes().add(central);
        mapa.getAlmacenes().add(norte);
        mapa.getAlmacenes().add(este);

        return mapa;
    }

    private List<Ruta> ejecutarAlgoritmoGenetico(List<Camion> camiones, List<Pedido> pedidos,
                                                 Mapa mapa, LocalDateTime momento,
                                                 ParametrosOptimizacionDTO parametros) {

        Genetico algoritmo;

        if (parametros != null) {
            algoritmo = new Genetico(
                    parametros.getTamañoPoblacion() != null ? parametros.getTamañoPoblacion() : 150,
                    parametros.getNumGeneraciones() != null ? parametros.getNumGeneraciones() : 100,
                    parametros.getTasaMutacion() != null ? parametros.getTasaMutacion() : 0.08,
                    parametros.getTasaCruce() != null ? parametros.getTasaCruce() : 0.8,
                    parametros.getElitismo() != null ? parametros.getElitismo() : 10
            );
        } else {
            algoritmo = new Genetico(); // Parámetros por defecto
        }

        return algoritmo.optimizarRutas(camiones, pedidos, mapa, momento);
    }

    @Transactional
    private List<RutaDTO> guardarRutasEnBD(List<Ruta> rutas, LocalDateTime momentoActual) {
        List<RutaDTO> rutasDTO = new ArrayList<>();

        for (Ruta ruta : rutas) {
            // Crear entidad ruta
            RutaEntity rutaEntity = new RutaEntity();
            rutaEntity.setId(UUID.randomUUID().toString());

            // Buscar camión asignado
            Optional<CamionEntity> camionOpt = camionRepository.findById(ruta.getCodigoCamion());
            if (camionOpt.isPresent()) {
                rutaEntity.setCodigoCamion(camionOpt.get());
            }

            // Crear ubicaciones origen y destino
            UbicacionEntity origenEntity = ubicacionService.obtenerOCrearUbicacion(
                    ruta.getOrigen().getX(), ruta.getOrigen().getY());
            rutaEntity.setOrigen(origenEntity);

            if (ruta.getDestino() != null) {
                UbicacionEntity destinoEntity = ubicacionService.obtenerOCrearUbicacion(
                        ruta.getDestino().getX(), ruta.getDestino().getY());
                rutaEntity.setDestino(destinoEntity);
            }

            rutaEntity.setHoraInicio(momentoActual);
            rutaEntity.setHoraFinEstimada(ruta.getHoraFinEstimada());
            rutaEntity.setDistanciaTotal(ruta.getDistanciaTotal());
            rutaEntity.setConsumoCombustible(ruta.getConsumoCombustible());
            rutaEntity.setCompletada(false);
            rutaEntity.setCancelada(false);
            rutaEntity.setRequiereReabastecimiento(ruta.isRequiereReabastecimiento());
            rutaEntity.setFactibilidadEncontrada(ruta.isFactibilidadEncontrada());
            rutaEntity.setMovimientoGenerado(false);

            rutaEntity = rutaRepository.save(rutaEntity);

            // Asignar pedidos a camiones en la BD
            for (Pedido pedido : ruta.getPedidosAsignados()) {
                Optional<PedidoEntity> pedidoOpt = pedidoRepository.findById(pedido.getId());
                if (pedidoOpt.isPresent() && camionOpt.isPresent()) {
                    PedidoEntity pedidoEntity = pedidoOpt.get();
                    pedidoEntity.setCamionAsignado(camionOpt.get());
                    pedidoRepository.save(pedidoEntity);
                }
            }

            // Convertir a DTO
            RutaDTO rutaDTO = rutaMapper.toDTO(rutaEntity);

            // Agregar pedidos asignados al DTO
            List<PedidoDTO> pedidosDTO = ruta.getPedidosAsignados().stream()
                    .map(pedido -> {
                        PedidoDTO dto = new PedidoDTO();
                        dto.setId(pedido.getId());
                        dto.setIdCliente(pedido.getIdCliente());
                        dto.setUbicacion(new UbicacionDTO(pedido.getUbicacion().getX(), pedido.getUbicacion().getY()));
                        dto.setCantidadGlp(pedido.getCantidadGLP());
                        dto.setHorasLimiteEntrega((int) pedido.getTiempoLimiteEntrega().toHours());
                        dto.setHoraRecepcion(pedido.getHoraRecepcion());
                        dto.setCamionAsignado(ruta.getCodigoCamion());
                        dto.setEntregado(pedido.isEntregado());
                        return dto;
                    })
                    .collect(Collectors.toList());
            rutaDTO.setPedidosAsignados(pedidosDTO);

            rutasDTO.add(rutaDTO);
        }

        return rutasDTO;
    }

    private OptimizacionResponse crearRespuestaError(String mensaje, long inicioTiempo) {
        OptimizacionResponse response = new OptimizacionResponse();
        response.setExito(false);
        response.setMensaje(mensaje);
        response.setRutasGeneradas(new ArrayList<>());
        response.setTiempoEjecucionMs(System.currentTimeMillis() - inicioTiempo);
        response.setPedidosAsignados(0);
        response.setPedidosNoAsignados(0);
        return response;
    }

    private OptimizacionResponse crearRespuestaExito(String mensaje, List<RutaDTO> rutas,
                                                     double fitness, long inicioTiempo) {
        OptimizacionResponse response = new OptimizacionResponse();
        response.setExito(true);
        response.setMensaje(mensaje);
        response.setRutasGeneradas(rutas);
        response.setFitnessTotal(fitness);
        response.setTiempoEjecucionMs(System.currentTimeMillis() - inicioTiempo);
        response.setPedidosAsignados(0);
        response.setPedidosNoAsignados(0);
        return response;
    }

    private double calcularFitnessTotal(List<Ruta> rutas) {
        return rutas.stream()
                .mapToDouble(r -> r.getDistanciaTotal() + r.getConsumoCombustible())
                .sum();
    }

    private int contarPedidosAsignados(List<Ruta> rutas) {
        return rutas.stream()
                .mapToInt(r -> r.getPedidosAsignados().size())
                .sum();
    }
}
