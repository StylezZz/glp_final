package pucp.edu.pe.glp_final.dto;

import lombok.Data;

@Data
public class ParametrosOptimizacionDTO {
    private Integer tamañoPoblacion;
    private Integer numGeneraciones;
    private Double tasaMutacion;
    private Double tasaCruce;
    private Integer elitismo;

    // Para Colonia de Hormigas
    private Integer numHormigas;
    private Double alpha;
    private Double beta;
    private Double rho;
    private Double Q;
}