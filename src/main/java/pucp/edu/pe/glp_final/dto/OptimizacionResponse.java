package pucp.edu.pe.glp_final.dto;

import lombok.Data;

import java.util.List;

@Data
public class OptimizacionResponse {
    private Boolean exito;
    private String mensaje;
    private List<RutaDTO> rutasGeneradas;
    private Double fitnessTotal;
    private Long tiempoEjecucionMs;
    private Integer pedidosAsignados;
    private Integer pedidosNoAsignados;
}