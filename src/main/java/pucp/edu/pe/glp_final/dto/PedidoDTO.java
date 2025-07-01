package pucp.edu.pe.glp_final.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PedidoDTO {
    private String id;
    private String idCliente;
    private UbicacionDTO ubicacion;
    private Double cantidadGlp;
    private Integer horasLimiteEntrega;
    private String camionAsignado;
    private Boolean entregado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaRecepcion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaEntregaProgramada;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaEntregaReal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime horaLimiteEntrega;
}