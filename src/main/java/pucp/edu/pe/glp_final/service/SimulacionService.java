package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pucp.edu.pe.glp_final.dto.*;
import pucp.edu.pe.glp_final.model.enums.EscenarioSimulacion;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulacionService {

    private final CamionService camionService;
    private final PedidoService pedidoService;
    private final OptimizacionService optimizacionService;
    private final WebSocketService webSocketService;

    // Estado de la simulación
    private String simulacionId;
    private EscenarioSimulacion escenarioActual;
    private LocalDateTime fechaInicio;
    private LocalDateTime momentoActualSimulacion;
    private Double velocidadSimulacion = 1.0;
    private final AtomicBoolean simulacionActiva = new AtomicBoolean(false);
    private final AtomicBoolean simulacionPausada = new AtomicBoolean(false);
    private Integer duracionDias;
    private Boolean incluirAverias = false;
    private Boolean incluirMantenimientos = false;

    // Estadísticas
    private int pedidosEntregados = 0;
    private double consumoTotalCombustible = 0.0;
    private double distanciaTotalRecorrida = 0.0;

    public EstadoSimulacionDTO iniciarSimulacion(IniciarSimulacionRequest request) {
        if (simulacionActiva.get()) {
            throw new IllegalStateException("Ya hay una simulación en curso");
        }

        this.simulacionId = UUID.randomUUID().toString();
        this.escenarioActual = request.getEscenario();
        this.fechaInicio = request.getFechaInicio();
        this.momentoActualSimulacion = request.getFechaInicio();
        this.velocidadSimulacion = request.getVelocidadSimulacion();
        this.duracionDias = request.getDuracionDias();
        this.incluirAverias = request.getIncluirAverias();
        this.incluirMantenimientos = request.getIncluirMantenimientos();

        // Reiniciar estadísticas
        this.pedidosEntregados = 0;
        this.consumoTotalCombustible = 0.0;
        this.distanciaTotalRecorrida = 0.0;

        simulacionActiva.set(true);
        simulacionPausada.set(false);

        log.info("Simulación iniciada: {} - Escenario: {} - Velocidad: {}x",
                simulacionId, escenarioActual, velocidadSimulacion);

        // Iniciar el bucle de simulación de forma asíncrona
        ejecutarSimulacionAsincrona();

        return obtenerEstadoActual();
    }

    public EstadoSimulacionDTO pausarSimulacion() {
        if (!simulacionActiva.get()) {
            throw new IllegalStateException("No hay simulación activa");
        }

        simulacionPausada.set(true);
        log.info("Simulación {} pausada", simulacionId);

        return obtenerEstadoActual();
    }

    public EstadoSimulacionDTO reanudarSimulacion() {
        if (!simulacionActiva.get()) {
            throw new IllegalStateException("No hay simulación activa");
        }

        simulacionPausada.set(false);
        log.info("Simulación {} reanudada", simulacionId);

        return obtenerEstadoActual();
    }

    public EstadoSimulacionDTO detenerSimulacion() {
        simulacionActiva.set(false);
        simulacionPausada.set(false);

        log.info("Simulación {} detenida", simulacionId);

        return obtenerEstadoActual();
    }

    public void cambiarVelocidad(Double nuevaVelocidad) {
        if (nuevaVelocidad <= 0) {
            throw new IllegalArgumentException("La velocidad debe ser mayor a 0");
        }

        this.velocidadSimulacion = nuevaVelocidad;
        log.info("Velocidad de simulación cambiada a: {}x", nuevaVelocidad);
    }

    public boolean estaActiva() {
        return simulacionActiva.get();
    }

    public EstadoSimulacionDTO obtenerEstadoActual() {
        EstadoSimulacionDTO estado = new EstadoSimulacionDTO();
        estado.setId(simulacionId);
        estado.setEscenario(escenarioActual);
        estado.setMomentoActual(momentoActualSimulacion);
        estado.setActiva(simulacionActiva.get());

        // Calcular progreso
        if (fechaInicio != null && duracionDias != null) {
            LocalDateTime fechaFin = fechaInicio.plusDays(duracionDias);
            long totalMinutos = ChronoUnit.MINUTES.between(fechaInicio, fechaFin);
            long transcurridos = ChronoUnit.MINUTES.between(fechaInicio, momentoActualSimulacion);
            estado.setProgreso(Math.min(100.0, (double) transcurridos / totalMinutos * 100));
        } else {
            estado.setProgreso(0.0);
        }

        // Obtener estado actual de camiones y rutas
        estado.setEstadoCamiones(camionService.obtenerTodos());
        estado.setPedidosPendientes((int) pedidoService.contarPendientes());
        estado.setPedidosEntregados(pedidosEntregados);
        estado.setConsumoCombustibleTotal(consumoTotalCombustible);
        estado.setDistanciaRecorridaTotal(distanciaTotalRecorrida);

        return estado;
    }

    @Async
    public void ejecutarSimulacionAsincrona() {
        try {
            while (simulacionActiva.get()) {
                if (!simulacionPausada.get()) {
                    ejecutarPasoSimulacion();
                }

                // Dormir según la velocidad de simulación
                Thread.sleep((long) (1000 / velocidadSimulacion));
            }
        } catch (InterruptedException e) {
            log.warn("Simulación interrumpida", e);
            simulacionActiva.set(false);
        } catch (Exception e) {
            log.error("Error durante simulación", e);
            simulacionActiva.set(false);
            webSocketService.notificarError("Error en simulación", e.getMessage());
        }
    }

    private void ejecutarPasoSimulacion() {
        // Avanzar tiempo de simulación
        momentoActualSimulacion = momentoActualSimulacion.plusMinutes(1);

        // Verificar si la simulación debe terminar
        if (debeTerminarSimulacion()) {
            detenerSimulacion();
            return;
        }

        // Procesar eventos según el escenario
        switch (escenarioActual) {
            case DIA_A_DIA:
                procesarDiaADia();
                break;
            case SIMULACION_SEMANAL:
            case SEMANAL:
                procesarSimulacionSemanal();
                break;
            case COLAPSO:
                procesarSimulacionColapso();
                break;
        }

        // Enviar estado actualizado por WebSocket cada 10 segundos de simulación
        if (momentoActualSimulacion.getMinute() % 10 == 0) {
            webSocketService.notificarEstadoSimulacion(obtenerEstadoActual());
        }
    }

    private boolean debeTerminarSimulacion() {
        if (duracionDias != null) {
            LocalDateTime fechaFin = fechaInicio.plusDays(duracionDias);
            if (momentoActualSimulacion.isAfter(fechaFin)) {
                log.info("Simulación terminada: duración completada");
                return true;
            }
        }

        // Para simulación de colapso, verificar condiciones de colapso
        if (escenarioActual == EscenarioSimulacion.COLAPSO) {
            long pedidosPendientes = pedidoService.contarPendientes();
            List<CamionDTO> camionesDisponibles = camionService.obtenerDisponibles();

            // Condiciones de colapso: muchos pedidos pendientes y pocos camiones disponibles
            if (pedidosPendientes > 50 && camionesDisponibles.size() < 3) {
                log.info("Simulación terminada: condiciones de colapso alcanzadas");
                return true;
            }
        }

        return false;
    }

    private void procesarDiaADia() {
        // Verificar si es hora de ejecutar optimización (cada hora)
        if (momentoActualSimulacion.getMinute() == 0) {
            ejecutarOptimizacionPeriodica();
        }

        // Procesar mantenimientos si están habilitados
        if (incluirMantenimientos && momentoActualSimulacion.getMinute() == 0) {
            procesarMantenimientos();
        }

        // Procesar averías si están habilitadas
        if (incluirAverias) {
            procesarAverias();
        }
    }

    private void procesarSimulacionSemanal() {
        // Similar a día a día pero con mayor frecuencia de eventos
        if (momentoActualSimulacion.getMinute() % 30 == 0) {
            ejecutarOptimizacionPeriodica();
        }

        if (incluirMantenimientos && momentoActualSimulacion.getHour() % 6 == 0 && momentoActualSimulacion.getMinute() == 0) {
            procesarMantenimientos();
        }

        if (incluirAverias && Math.random() < 0.05) { // 5% de probabilidad por minuto
            procesarAverias();
        }
    }

    private void procesarSimulacionColapso() {
        // Generar más pedidos y más averías para forzar el colapso
        if (momentoActualSimulacion.getMinute() % 15 == 0) {
            generarPedidosAdicionales();
        }

        if (momentoActualSimulacion.getMinute() % 20 == 0) {
            ejecutarOptimizacionPeriodica();
        }

        if (incluirAverias && Math.random() < 0.1) { // 10% de probabilidad por minuto
            procesarAverias();
        }
    }

    private void ejecutarOptimizacionPeriodica() {
        try {
            OptimizacionRequest request = new OptimizacionRequest();
            request.setMomentoActual(momentoActualSimulacion);
            request.setAlgoritmo("GENETICO");
            request.setForzarReplanificacion(false);

            optimizacionService.optimizarRutas(request);
            log.debug("Optimización periódica ejecutada en: {}", momentoActualSimulacion);

        } catch (Exception e) {
            log.error("Error en optimización periódica", e);
        }
    }

    private void procesarMantenimientos() {
        // Simular mantenimientos programados
        List<CamionDTO> camiones = camionService.obtenerTodos();
        for (CamionDTO camion : camiones) {
            if (camion.getFechaProximoMantenimiento() != null &&
                    momentoActualSimulacion.isAfter(camion.getFechaProximoMantenimiento()) &&
                    !camion.getEnMantenimiento()) {

                camionService.iniciarMantenimiento(camion.getCodigo());
                log.info("Mantenimiento iniciado para camión: {}", camion.getCodigo());
            }
        }
    }

    private void procesarAverias() {
        // Simular averías aleatorias
        List<CamionDTO> camionesEnRuta = camionService.obtenerTodos().stream()
                .filter(c -> "EN_RUTA".equals(c.getEstado().name()))
                .toList();

        for (CamionDTO camion : camionesEnRuta) {
            if (Math.random() < 0.001) { // 0.1% de probabilidad por minuto
                String tipoAveria = generarTipoAveriaAleatoria();
                camionService.actualizarEstado(camion.getCodigo(),
                        pucp.edu.pe.glp_final.model.enums.EstadoCamion.AVERIADO,
                        "Avería tipo " + tipoAveria);

                webSocketService.notificarAveria(camion, tipoAveria);
                log.warn("Avería {} simulada en camión: {}", tipoAveria, camion.getCodigo());
            }
        }
    }

    private String generarTipoAveriaAleatoria() {
        double random = Math.random();
        if (random < 0.6) return "TI1"; // 60% - Avería menor
        if (random < 0.9) return "TI2"; // 30% - Avería moderada
        return "TI3"; // 10% - Avería mayor
    }

    private void generarPedidosAdicionales() {
        // Generar pedidos adicionales para el escenario de colapso
        for (int i = 0; i < 3; i++) {
            CrearPedidoRequest request = new CrearPedidoRequest();
            request.setIdCliente("c-simulacion-" + System.currentTimeMillis() + "-" + i);
            request.setUbicacion(new UbicacionDTO(
                    (int) (Math.random() * 70),
                    (int) (Math.random() * 50)
            ));
            request.setCantidadGlp(5.0 + Math.random() * 20.0); // 5-25 m³
            request.setHorasLimiteEntrega(4 + (int) (Math.random() * 8)); // 4-12 horas
            request.setHoraRecepcion(momentoActualSimulacion);

            pedidoService.crearPedido(request);
        }

        log.debug("Generados 3 pedidos adicionales para simulación de colapso");
    }

    // Tarea programada para limpiar simulaciones antiguas
    @Scheduled(fixedRate = 60000) // Cada minuto
    public void monitorearSimulacion() {
        if (simulacionActiva.get() && !simulacionPausada.get()) {
            // Actualizar estadísticas
            actualizarEstadisticas();

            // Verificar condiciones de parada automática
            if (debeTerminarSimulacion()) {
                detenerSimulacion();
            }
        }
    }

    private void actualizarEstadisticas() {
        // Actualizar estadísticas de la simulación
        // Esto se podría hacer consultando la base de datos o manteniendo contadores
        log.debug("Estadísticas actualizadas para simulación: {}", simulacionId);
    }
}