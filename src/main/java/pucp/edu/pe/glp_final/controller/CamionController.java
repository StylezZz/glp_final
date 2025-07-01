package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucp.edu.pe.glp_final.dto.CamionDTO;
import pucp.edu.pe.glp_final.model.enums.EstadoCamion;
import pucp.edu.pe.glp_final.service.CamionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/camiones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CamionController {

    private final CamionService camionService;

    @GetMapping
    public ResponseEntity<List<CamionDTO>> obtenerTodos() {
        List<CamionDTO> camiones = camionService.obtenerTodos();
        return ResponseEntity.ok(camiones);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<CamionDTO> obtenerPorCodigo(@PathVariable String codigo) {
        return camionService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<CamionDTO>> obtenerDisponibles() {
        List<CamionDTO> camiones = camionService.obtenerDisponibles();
        return ResponseEntity.ok(camiones);
    }

    @PostMapping
    public ResponseEntity<CamionDTO> crear(@RequestBody CamionDTO camionDTO) {
        try {
            CamionDTO nuevoCamion = camionService.crear(camionDTO);
            return ResponseEntity.ok(nuevoCamion);
        } catch (Exception e) {
            log.error("Error al crear camión", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<CamionDTO> actualizar(@PathVariable String codigo,
                                                @RequestBody CamionDTO camionDTO) {
        return camionService.actualizar(codigo, camionDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{codigo}/estado")
    public ResponseEntity<Void> actualizarEstado(@PathVariable String codigo,
                                                 @RequestParam EstadoCamion estado,
                                                 @RequestParam(required = false) String motivo) {
        camionService.actualizarEstado(codigo, estado, motivo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{codigo}/ubicacion")
    public ResponseEntity<Void> actualizarUbicacion(@PathVariable String codigo,
                                                    @RequestParam int x,
                                                    @RequestParam int y) {
        camionService.actualizarUbicacion(codigo, x, y);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{codigo}/mantenimiento")
    public ResponseEntity<Void> programarMantenimiento(@PathVariable String codigo,
                                                       @RequestParam LocalDateTime fecha) {
        camionService.programarMantenimiento(codigo, fecha);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{codigo}/mantenimiento/iniciar")
    public ResponseEntity<Void> iniciarMantenimiento(@PathVariable String codigo) {
        camionService.iniciarMantenimiento(codigo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{codigo}/mantenimiento/finalizar")
    public ResponseEntity<Void> finalizarMantenimiento(@PathVariable String codigo) {
        camionService.finalizarMantenimiento(codigo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable String codigo) {
        boolean eliminado = camionService.eliminar(codigo);
        return eliminado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}