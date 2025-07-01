package pucp.edu.pe.glp_final.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OptimizacionRequest {
    private LocalDateTime momentoActual;
    private Boolean forzarReplanificacion;
    private String algoritmo; // "GENETICO" o "COLONIA_HORMIGAS"
    private ParametrosOptimizacionDTO parametros;
}