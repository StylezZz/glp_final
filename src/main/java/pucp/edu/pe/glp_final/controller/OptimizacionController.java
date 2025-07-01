package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucp.edu.pe.glp_final.dto.OptimizacionRequest;
import pucp.edu.pe.glp_final.dto.OptimizacionResponse;
import pucp.edu.pe.glp_final.dto.ParametrosOptimizacionDTO;
import pucp.edu.pe.glp_final.service.OptimizacionService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/optimizacion")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OptimizacionController {

    private final OptimizacionService optimizacionService;

    @PostMapping("/rutas")
    public ResponseEntity<OptimizacionResponse> optimizarRutas(@RequestBody OptimizacionRequest request) {
        try {
            // Validar request
            if (request.getMomentoActual() == null) {
                request.setMomentoActual(LocalDateTime.now());
            }
            if (request.getAlgoritmo() == null) {
                request.setAlgoritmo("GENETICO");
            }

            OptimizacionResponse response = optimizacionService.optimizarRutas(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en optimización", e);

            OptimizacionResponse errorResponse = new OptimizacionResponse();
            errorResponse.setExito(false);
            errorResponse.setMensaje("Error durante optimización: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/rutas/rapida")
    public ResponseEntity<OptimizacionResponse> optimizacionRapida() {
        OptimizacionRequest request = new OptimizacionRequest();
        request.setMomentoActual(LocalDateTime.now());
        request.setAlgoritmo("GENETICO");
        request.setForzarReplanificacion(false);

        // Parámetros para ejecución rápida
        ParametrosOptimizacionDTO parametros = new ParametrosOptimizacionDTO();
        parametros.setTamañoPoblacion(50);
        parametros.setNumGeneraciones(30);
        parametros.setTasaMutacion(0.1);
        parametros.setTasaCruce(0.7);
        parametros.setElitismo(5);
        request.setParametros(parametros);

        return optimizarRutas(request);
    }

    @PostMapping("/rutas/intensiva")
    public ResponseEntity<OptimizacionResponse> optimizacionIntensiva() {
        OptimizacionRequest request = new OptimizacionRequest();
        request.setMomentoActual(LocalDateTime.now());
        request.setAlgoritmo("GENETICO");
        request.setForzarReplanificacion(true);

        // Parámetros para ejecución intensiva
        ParametrosOptimizacionDTO parametros = new ParametrosOptimizacionDTO();
        parametros.setTamañoPoblacion(200);
        parametros.setNumGeneraciones(150);
        parametros.setTasaMutacion(0.05);
        parametros.setTasaCruce(0.85);
        parametros.setElitismo(15);
        request.setParametros(parametros);

        return optimizarRutas(request);
    }

    @GetMapping("/parametros/defecto")
    public ResponseEntity<ParametrosOptimizacionDTO> obtenerParametrosDefecto() {
        ParametrosOptimizacionDTO parametros = new ParametrosOptimizacionDTO();
        parametros.setTamañoPoblacion(150);
        parametros.setNumGeneraciones(100);
        parametros.setTasaMutacion(0.08);
        parametros.setTasaCruce(0.8);
        parametros.setElitismo(10);

        return ResponseEntity.ok(parametros);
    }
}