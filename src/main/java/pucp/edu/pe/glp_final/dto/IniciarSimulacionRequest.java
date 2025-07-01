package pucp.edu.pe.glp_final.dto;

import lombok.Data;
import pucp.edu.pe.glp_final.model.enums.EscenarioSimulacion;

import java.time.LocalDateTime;

@Data
public class IniciarSimulacionRequest {
    private EscenarioSimulacion escenario;
    private LocalDateTime fechaInicio;
    private Integer duracionDias;
    private Double velocidadSimulacion;
    private Boolean incluirAverias;
    private Boolean incluirMantenimientos;
}