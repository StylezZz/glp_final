package pucp.edu.pe.glp_final.dto;

import lombok.Data;

@Data
public class MonitoreoRutaDTO {
    private String rutaId;
    private String codigoCamion;
    private UbicacionDTO posicionActual;
    private Double progreso;
    private String estadoActual;
    private Integer pedidosEntregados;
    private Integer pedidosTotales;
    private Double consumoCombustible;
    private Double distanciaRecorrida;
}