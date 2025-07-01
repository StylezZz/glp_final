package pucp.edu.pe.glp_final.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RutaDTO {
    private String id;
    private String codigoCamion;
    private UbicacionDTO origen;
    private UbicacionDTO destino;
    private Double distanciaTotal;
    private Double consumoCombustible;
    private Boolean completada;
    private Boolean cancelada;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaFinEstimada;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaFinReal;

    private List<PedidoDTO> pedidosAsignados;
    private List<UbicacionDTO> secuenciaNodos;
    private List<UbicacionDTO> secuenciaParadas;
}