package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucp.edu.pe.glp_final.dto.CrearPedidoRequest;
import pucp.edu.pe.glp_final.dto.PedidoDTO;
import pucp.edu.pe.glp_final.service.PedidoService;
import pucp.edu.pe.glp_final.service.WebSocketService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final WebSocketService webSocketService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerTodos() {
        List<PedidoDTO> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<PedidoDTO>> obtenerPendientes() {
        List<PedidoDTO> pedidos = pedidoService.obtenerPendientes();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable String id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> obtenerPorCliente(@PathVariable String idCliente) {
        List<PedidoDTO> pedidos = pedidoService.obtenerPorCliente(idCliente);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<PedidoDTO>> obtenerVencidos(@RequestParam(required = false) LocalDateTime momento) {
        LocalDateTime momentoConsulta = momento != null ? momento : LocalDateTime.now();
        List<PedidoDTO> pedidos = pedidoService.obtenerVencidos(momentoConsulta);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@RequestBody CrearPedidoRequest request) {
        try {
            PedidoDTO nuevoPedido = pedidoService.crearPedido(request);

            // Notificar por WebSocket
            webSocketService.notificarNuevoPedido(nuevoPedido);

            return ResponseEntity.ok(nuevoPedido);
        } catch (Exception e) {
            log.error("Error al crear pedido", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<PedidoDTO> asignarCamion(@PathVariable String id,
                                                   @RequestParam String codigoCamion) {
        return pedidoService.asignarCamion(id, codigoCamion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/entregar")
    public ResponseEntity<PedidoDTO> marcarEntregado(@PathVariable String id,
                                                     @RequestParam(required = false) LocalDateTime horaEntrega) {
        LocalDateTime horaEntregaFinal = horaEntrega != null ? horaEntrega : LocalDateTime.now();

        return pedidoService.marcarEntregado(id, horaEntregaFinal)
                .map(pedido -> {
                    // Notificar entrega por WebSocket
                    webSocketService.notificarPedidoEntregado(pedido);
                    return ResponseEntity.ok(pedido);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasPedidosDTO> obtenerEstadisticas() {
        EstadisticasPedidosDTO estadisticas = new EstadisticasPedidosDTO();
        estadisticas.setPendientes(pedidoService.contarPendientes());
        estadisticas.setCantidadGlpPendiente(pedidoService.sumaCantidadPendiente());
        return ResponseEntity.ok(estadisticas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        boolean eliminado = pedidoService.eliminar(id);
        return eliminado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // DTO para estadísticas
    public static class EstadisticasPedidosDTO {
        private long pendientes;
        private Double cantidadGlpPendiente;

        public long getPendientes() { return pendientes; }
        public void setPendientes(long pendientes) { this.pendientes = pendientes; }
        public Double getCantidadGlpPendiente() { return cantidadGlpPendiente; }
        public void setCantidadGlpPendiente(Double cantidadGlpPendiente) { this.cantidadGlpPendiente = cantidadGlpPendiente; }
    }
}