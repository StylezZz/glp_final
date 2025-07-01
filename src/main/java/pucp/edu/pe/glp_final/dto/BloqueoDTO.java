package pucp.edu.pe.glp_final.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BloqueoDTO {
    private String id;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private List<UbicacionDTO> nodosBloqueados;
    private Boolean activo;
}