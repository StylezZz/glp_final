package pucp.edu.pe.glp_final.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EstadoSimulacionDTO {
    private String id;
    private EscenarioSimulacion escenario;
    private LocalDateTime momentoActual;
    private Double progreso;
    private Boolean activa;
    private List<CamionDTO> estadoCamiones;
    private List<RutaDTO> rutasActivas;
    private Integer pedidosPendientes;
    private Integer pedidosEntregados;
    private Double consumoCombustibleTotal;
    private Double distanciaRecorridaTotal;
}