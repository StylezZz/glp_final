package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import pucp.edu.pe.glp_final.dto.CamionDTO;
import pucp.edu.pe.glp_final.dto.PedidoDTO;
import pucp.edu.pe.glp_final.service.CamionService;
import pucp.edu.pe.glp_final.service.PedidoService;
import pucp.edu.pe.glp_final.service.SimulacionService;

import java.util.List;

/**
 * Controlador WebSocket para comunicación en tiempo real
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final CamionService camionService;
    private final PedidoService pedidoService;
    private final SimulacionService simulacionService;

    /**
     * Endpoint para suscribirse al estado de los camiones
     */
    @SubscribeMapping("/camiones/estado")
    public List<CamionDTO> suscribirEstadoCamiones() {
        log.debug("Cliente suscrito a estado de camiones");
        return camionService.obtenerTodos();
    }

    /**
     * Endpoint para suscribirse a pedidos pendientes
     */
    @SubscribeMapping("/pedidos/pendientes")
    public List<PedidoDTO> suscribirPedidosPendientes() {
        log.debug("Cliente suscrito a pedidos pendientes");
        return pedidoService.obtenerPendientes();
    }

    /**
     * Endpoint para suscribirse al estado de la simulación
     */
    @SubscribeMapping("/simulacion/estado")
    public Object suscribirEstadoSimulacion() {
        log.debug("Cliente suscrito a estado de simulación");
        if (simulacionService.estaActiva()) {
            return simulacionService.obtenerEstadoActual();
        } else {
            return "No hay simulación activa";
        }
    }

    /**
     * Maneja mensajes de ping desde el cliente
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing(String message) {
        log.debug("Ping recibido: {}", message);
        return "pong-" + System.currentTimeMillis();
    }

    /**
     * Maneja solicitudes de actualización de estado
     */
    @MessageMapping("/solicitar-estado")
    @SendTo("/topic/estado-general")
    public EstadoGeneralDTO solicitarEstado() {
        log.debug("Solicitud de estado general recibida");

        EstadoGeneralDTO estado = new EstadoGeneralDTO();
        estado.setCamiones(camionService.obtenerTodos());
        estado.setPedidosPendientes(pedidoService.obtenerPendientes());
        estado.setSimulacionActiva(simulacionService.estaActiva());

        if (simulacionService.estaActiva()) {
            estado.setEstadoSimulacion(simulacionService.obtenerEstadoActual());
        }

        return estado;
    }

    /**
     * DTO para estado general del sistema
     */
    public static class EstadoGeneralDTO {
        private List<CamionDTO> camiones;
        private List<PedidoDTO> pedidosPendientes;
        private boolean simulacionActiva;
        private Object estadoSimulacion;

        // Getters y setters
        public List<CamionDTO> getCamiones() { return camiones; }
        public void setCamiones(List<CamionDTO> camiones) { this.camiones = camiones; }
        public List<PedidoDTO> getPedidosPendientes() { return pedidosPendientes; }
        public void setPedidosPendientes(List<PedidoDTO> pedidosPendientes) { this.pedidosPendientes = pedidosPendientes; }
        public boolean isSimulacionActiva() { return simulacionActiva; }
        public void setSimulacionActiva(boolean simulacionActiva) { this.simulacionActiva = simulacionActiva; }
        public Object getEstadoSimulacion() { return estadoSimulacion; }
        public void setEstadoSimulacion(Object estadoSimulacion) { this.estadoSimulacion = estadoSimulacion; }
    }
}
