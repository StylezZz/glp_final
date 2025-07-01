package pucp.edu.pe.glp_final.dto;

import lombok.Data;
import pucp.edu.pe.glp_final.model.enums.TipoAlmacen;

@Data
public class AlmacenDTO {
    private String id;
    private UbicacionDTO ubicacion;
    private TipoAlmacen tipo;
    private Double capacidadMaxima;
    private Double nivelActual;
    private Double porcentajeOcupacion;
}