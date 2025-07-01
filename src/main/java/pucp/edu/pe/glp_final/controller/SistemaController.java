package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucp.edu.pe.glp_final.mapper.AveriaMapper;
import pucp.edu.pe.glp_final.mapper.MantenimientoMapper;
import pucp.edu.pe.glp_final.service.InicializacionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador para endpoints de sistema y salud
 */
@RestController
@RequestMapping("/sistema")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SistemaController {

    private final InicializacionService inicializacionService;

    @GetMapping("/salud")
    public ResponseEntity<Map<String, Object>> verificarSalud() {
        Map<String, Object> salud = Map.of(
                "estado", "OK",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0",
                "descripcion", "Sistema PLG - Distribución de GLP",
                "baseDatos", "Conectada",
                "websocket", "Activo"
        );

        return ResponseEntity.ok(salud);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> obtenerInfo() {
        Map<String, Object> info = Map.of(
                "aplicacion", "PLG Final",
                "version", "1.0.0",
                "descripcion", "Sistema de optimización de rutas para distribución de GLP",
                "universidad", "PUCP",
                "curso", "1INF54 Proyecto de Diseño y Desarrollo de Software",
                "semestre", "2025-1",
                "algoritmos", Map.of(
                        "genetico", "Implementado",
                        "colonia_hormigas", "Planificado"
                ),
                "escenarios", Map.of(
                        "dia_a_dia", "Implementado",
                        "simulacion_semanal", "Implementado",
                        "colapso", "Implementado"
                ),
                "baseDatos", "Esquema adaptado a especificaciones"
        );

        return ResponseEntity.ok(info);
    }

    @GetMapping("/configuracion")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracion() {
        Map<String, Object> config = Map.of(
                "ciudad", Map.of(
                        "ancho", 70,
                        "alto", 50,
                        "unidad", "km"
                ),
                "almacenes", Map.of(
                        "central", Map.of("x", 12, "y", 8, "tipo", "PRINCIPAL"),
                        "norte", Map.of("x", 42, "y", 42, "tipo", "INTERMEDIO", "capacidad", 160),
                        "este", Map.of("x", 63, "y", 3, "tipo", "INTERMEDIO", "capacidad", 160)
                ),
                "flota", Map.of(
                        "TA", Map.of("cantidad", 2, "capacidad", 25, "peso_tara", 2.5),
                        "TB", Map.of("cantidad", 4, "capacidad", 15, "peso_tara", 2.0),
                        "TC", Map.of("cantidad", 4, "capacidad", 10, "peso_tara", 1.5),
                        "TD", Map.of("cantidad", 10, "capacidad", 5, "peso_tara", 1.0)
                ),
                "parametros", Map.of(
                        "velocidad_promedio", "50 km/h",
                        "tiempo_descarga", "15 minutos",
                        "tiempo_mantenimiento_rutina", "15 minutos",
                        "tiempo_mantenimiento_preventivo", "24 horas",
                        "capacidad_combustible", "25 galones"
                ),
                "averias", Map.of(
                        "TI1", "2h inmovilización",
                        "TI2", "2h inmovilización + 1 turno taller",
                        "TI3", "4h inmovilización + 3 días taller"
                )
        );

        return ResponseEntity.ok(config);
    }

    @PostMapping("/reinicializar")
    public ResponseEntity<Map<String, Object>> reinicializarDatos() {
        try {
            log.warn("Solicitud de reinicialización del sistema recibida");

            inicializacionService.inicializarDatosMaestros();

            Map<String, Object> resultado = Map.of(
                    "mensaje", "Datos reinicializados exitosamente",
                    "timestamp", LocalDateTime.now(),
                    "tablas_afectadas", List.of("camion", "almacen", "ubicacion", "tipo_incidente", "turno", "mapa")
            );

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("Error durante reinicialización", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error durante reinicialización: " + e.getMessage()));
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> stats = Map.of(
                "timestamp", LocalDateTime.now(),
                "sistema", Map.of(
                        "uptime", "Activo desde inicio",
                        "memoria_usada", "Información no disponible",
                        "threads_activos", Thread.activeCount()
                ),
                "operacion", Map.of(
                        "optimizaciones_ejecutadas", "Contador no implementado",
                        "simulaciones_activas", 0,
                        "websocket_conexiones", "Contador no implementado"
                )
        );

        return ResponseEntity.ok(stats);
    }
}
