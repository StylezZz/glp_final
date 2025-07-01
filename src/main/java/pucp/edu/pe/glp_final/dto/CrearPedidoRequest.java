package pucp.edu.pe.glp_final.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrearPedidoRequest {
    private String idCliente;
    private UbicacionDTO ubicacion;
    private Double cantidadGlp;
    private Integer horasLimiteEntrega;
    private LocalDateTime horaRecepcion;
}