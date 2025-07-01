package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucp.edu.pe.glp_final.dto.EstadoSimulacionDTO;
import pucp.edu.pe.glp_final.dto.IniciarSimulacionRequest;
import pucp.edu.pe.glp_final.dto.EstadoSimulacionDTO.EscenarioSimulacion;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/simulacion")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SimulacionController {

    // Simulador simple para demostración
    private boolean simulacionActiva = false;
    private EscenarioSimulacion escenarioActual = null;
    private LocalDateTime fechaInicio = null;
    private Double velocidadSimulacion = 1.0;

    @PostMapping("/iniciar")
    public ResponseEntity<EstadoSimulacionDTO> iniciarSimulacion(@RequestBody IniciarSimulacionRequest request) {
        try {
            // Validar y configurar valores por defecto
            if (request.getEscenario() == null) {
                request.setEscenario(EscenarioSimulacion.DIA_A_DIA);
            }
            if (request.getFechaInicio() == null) {
                request.setFechaInicio(LocalDateTime.now());
            }
            if (request.getVelocidadSimulacion() == null) {
                request.setVelocidadSimulacion(1.0);
            }

            // Iniciar simulación
            this.simulacionActiva = true;
            this.escenarioActual = request.getEscenario();
            this.fechaInicio = request.getFechaInicio();
            this.velocidadSimulacion = request.getVelocidadSimulacion();

            log.info("Simulación iniciada: Escenario {}, Velocidad {}x", escenarioActual, velocidadSimulacion);

            EstadoSimulacionDTO estado = obtenerEstadoActual();
            return ResponseEntity.ok(estado);

        } catch (Exception e) {
            log.error("Error al iniciar simulación", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/pausar")
    public ResponseEntity<EstadoSimulacionDTO> pausarSimulacion() {
        if (!simulacionActiva) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Simulación pausada");
        EstadoSimulacionDTO estado = obtenerEstadoActual();
        return ResponseEntity.ok(estado);
    }

    @PostMapping("/reanudar")
    public ResponseEntity<EstadoSimulacionDTO> reanudarSimulacion() {
        if (!simulacionActiva) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Simulación reanudada");
        EstadoSimulacionDTO estado = obtenerEstadoActual();
        return ResponseEntity.ok(estado);
    }

    @PostMapping("/detener")
    public ResponseEntity<EstadoSimulacionDTO> detenerSimulacion() {
        this.simulacionActiva = false;
        this.escenarioActual = null;
        this.fechaInicio = null;

        log.info("Simulación detenida");
        EstadoSimulacionDTO estado = obtenerEstadoActual();
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/estado")
    public ResponseEntity<EstadoSimulacionDTO> obtenerEstado() {
        EstadoSimulacionDTO estado = obtenerEstadoActual();
        return ResponseEntity.ok(estado);
    }

    @PostMapping("/velocidad")
    public ResponseEntity<Void> cambiarVelocidad(@RequestParam Double velocidad) {
        if (velocidad <= 0) {
            return ResponseEntity.badRequest().build();
        }

        this.velocidadSimulacion = velocidad;
        log.info("Velocidad de simulación cambiada a: {}x", velocidad);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activa")
    public ResponseEntity<Boolean> estaActiva() {
        return ResponseEntity.ok(simulacionActiva);
    }

    // Endpoints para escenarios específicos
    @PostMapping("/semanal")
    public ResponseEntity<EstadoSimulacionDTO> iniciarSimulacionSemanal() {
        IniciarSimulacionRequest request = new IniciarSimulacionRequest();
        request.setEscenario(EscenarioSimulacion.SIMULACION_SEMANAL);
        request.setFechaInicio(LocalDateTime.now());
        request.setDuracionDias(7);
        request.setVelocidadSimulacion(5.0); // 5x más rápido
        request.setIncluirAverias(true);
        request.setIncluirMantenimientos(true);

        return iniciarSimulacion(request);
    }

    @PostMapping("/colapso")
    public ResponseEntity<EstadoSimulacionDTO> iniciarSimulacionColapso() {
        IniciarSimulacionRequest request = new IniciarSimulacionRequest();
        request.setEscenario(EscenarioSimulacion.COLAPSO);
        request.setFechaInicio(LocalDateTime.now());
        request.setDuracionDias(30); // Hasta 30 días o hasta colapso
        request.setVelocidadSimulacion(10.0); // 10x más rápido
        request.setIncluirAverias(true);
        request.setIncluirMantenimientos(true);

        return iniciarSimulacion(request);
    }

    private EstadoSimulacionDTO obtenerEstadoActual() {
        EstadoSimulacionDTO estado = new EstadoSimulacionDTO();
        estado.setId(simulacionActiva ? "SIM-" + System.currentTimeMillis() : null);
        estado.setEscenario(escenarioActual);
        estado.setMomentoActual(simulacionActiva ? LocalDateTime.now() : null);
        estado.setActiva(simulacionActiva);
        estado.setProgreso(simulacionActiva ? Math.random() * 100 : 0.0); // Mock
        estado.setPedidosPendientes(simulacionActiva ? (int)(Math.random() * 50) : 0);
        estado.setPedidosEntregados(simulacionActiva ? (int)(Math.random() * 100) : 0);
        estado.setConsumoCombustibleTotal(simulacionActiva ? Math.random() * 1000 : 0.0);
        estado.setDistanciaRecorridaTotal(simulacionActiva ? Math.random() * 5000 : 0.0);

        return estado;
    }
}
