package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pucp.edu.pe.glp_final.dto.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notifica actualización de estado de camiones
     */
    public void notificarEstadoCamiones(List<CamionDTO> camiones) {
        messagingTemplate.convertAndSend("/topic/camiones/estado", camiones);
        log.debug("Estado de {} camiones enviado por WebSocket", camiones.size());
    }

    /**
     * Notifica nuevos pedidos
     */
    public void notificarNuevoPedido(PedidoDTO pedido) {
        messagingTemplate.convertAndSend("/topic/pedidos/nuevo", pedido);
        log.info("Nuevo pedido {} notificado por WebSocket", pedido.getId());
    }

    /**
     * Notifica pedidos entregados
     */
    public void notificarPedidoEntregado(PedidoDTO pedido) {
        messagingTemplate.convertAndSend("/topic/pedidos/entregado", pedido);
        log.info("Entrega de pedido {} notificada por WebSocket", pedido.getId());
    }

    /**
     * Notifica optimización completada
     */
    public void notificarOptimizacionCompletada(List<RutaDTO> rutas, int pedidosAsignados) {
        OptimizacionEventoDTO evento = new OptimizacionEventoDTO();
        evento.setTimestamp(LocalDateTime.now());
        evento.setRutasGeneradas(rutas.size());
        evento.setPedidosAsignados(pedidosAsignados);
        evento.setRutas(rutas);

        messagingTemplate.convertAndSend("/topic/optimizacion/completada", evento);
        log.info("Optimización con {} rutas notificada por WebSocket", rutas.size());
    }

    /**
     * Notifica estado de simulación
     */
    public void notificarEstadoSimulacion(EstadoSimulacionDTO estado) {
        messagingTemplate.convertAndSend("/topic/simulacion/estado", estado);
        log.debug("Estado de simulación enviado por WebSocket");
    }

    /**
     * Notifica progreso de rutas en tiempo real
     */
    public void notificarProgresoRutas(List<MonitoreoRutaDTO> progreso) {
        messagingTemplate.convertAndSend("/topic/rutas/progreso", progreso);
        log.debug("Progreso de {} rutas enviado por WebSocket", progreso.size());
    }

    /**
     * Notifica averías de camiones
     */
    public void notificarAveria(CamionDTO camion, String tipoAveria) {
        AveriaEventoDTO evento = new AveriaEventoDTO();
        evento.setTimestamp(LocalDateTime.now());
        evento.setCamion(camion);
        evento.setTipoAveria(tipoAveria);
        evento.setUbicacion(camion.getUbicacion());

        messagingTemplate.convertAndSend("/topic/camiones/averia", evento);
        log.warn("Avería {} en camión {} notificada por WebSocket", tipoAveria, camion.getCodigo());
    }

    /**
     * Notifica bloqueos activos
     */
    public void notificarBloqueosActivos(List<BloqueoDTO> bloqueos) {
        messagingTemplate.convertAndSend("/topic/mapa/bloqueos", bloqueos);
        log.info("{} bloqueos activos notificados por WebSocket", bloqueos.size());
    }

    /**
     * Notifica errores del sistema
     */
    public void notificarError(String mensaje, String detalles) {
        ErrorEventoDTO evento = new ErrorEventoDTO();
        evento.setTimestamp(LocalDateTime.now());
        evento.setMensaje(mensaje);
        evento.setDetalles(detalles);

        messagingTemplate.convertAndSend("/topic/sistema/error", evento);
        log.error("Error notificado por WebSocket: {}", mensaje);
    }

    // DTOs para eventos específicos
    public static class OptimizacionEventoDTO {
        private LocalDateTime timestamp;
        private int rutasGeneradas;
        private int pedidosAsignados;
        private List<RutaDTO> rutas;

        // getters y setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public int getRutasGeneradas() { return rutasGeneradas; }
        public void setRutasGeneradas(int rutasGeneradas) { this.rutasGeneradas = rutasGeneradas; }
        public int getPedidosAsignados() { return pedidosAsignados; }
        public void setPedidosAsignados(int pedidosAsignados) { this.pedidosAsignados = pedidosAsignados; }
        public List<RutaDTO> getRutas() { return rutas; }
        public void setRutas(List<RutaDTO> rutas) { this.rutas = rutas; }
    }

    public static class AveriaEventoDTO {
        private LocalDateTime timestamp;
        private CamionDTO camion;
        private String tipoAveria;
        private UbicacionDTO ubicacion;

        // getters y setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public CamionDTO getCamion() { return camion; }
        public void setCamion(CamionDTO camion) { this.camion = camion; }
        public String getTipoAveria() { return tipoAveria; }
        public void setTipoAveria(String tipoAveria) { this.tipoAveria = tipoAveria; }
        public UbicacionDTO getUbicacion() { return ubicacion; }
        public void setUbicacion(UbicacionDTO ubicacion) { this.ubicacion = ubicacion; }
    }

    public static class ErrorEventoDTO {
        private LocalDateTime timestamp;
        private String mensaje;
        private String detalles;

        // getters y setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public String getDetalles() { return detalles; }
        public void setDetalles(String detalles) { this.detalles = detalles; }
    }
}