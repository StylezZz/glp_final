package pucp.edu.pe.glp_final.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pucp.edu.pe.glp_final.dto.AlmacenDTO;
import pucp.edu.pe.glp_final.service.AlmacenService;

import java.util.List;

@RestController
@RequestMapping("/almacenes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AlmacenController {

    private final AlmacenService almacenService;

    @GetMapping
    public ResponseEntity<List<AlmacenDTO>> obtenerTodos() {
        List<AlmacenDTO> almacenes = almacenService.obtenerTodos();
        return ResponseEntity.ok(almacenes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlmacenDTO> obtenerPorId(@PathVariable String id) {
        return almacenService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<AlmacenDTO>> obtenerPorTipo(@PathVariable String tipo) {
        List<AlmacenDTO> almacenes = almacenService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(almacenes);
    }

    @GetMapping("/central")
    public ResponseEntity<AlmacenDTO> obtenerCentral() {
        return almacenService.obtenerCentral()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/recargar")
    public ResponseEntity<Void> recargarAlmacenesIntermedios() {
        almacenService.recargarAlmacenesIntermedios();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/extraer")
    public ResponseEntity<ExtraerResponse> extraer(@PathVariable String id,
                                                   @RequestParam Double cantidad) {
        boolean exito = almacenService.extraer(id, cantidad);

        ExtraerResponse response = new ExtraerResponse();
        response.setExito(exito);
        response.setMensaje(exito ? "Extracción exitosa" : "Cantidad insuficiente en almacén");

        return ResponseEntity.ok(response);
    }

    // DTO para respuesta de extracción
    public static class ExtraerResponse {
        private boolean exito;
        private String mensaje;

        public boolean isExito() { return exito; }
        public void setExito(boolean exito) { this.exito = exito; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }
}
