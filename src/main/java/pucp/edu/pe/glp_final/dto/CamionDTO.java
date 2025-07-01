package pucp.edu.pe.glp_final.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pucp.edu.pe.glp_final.model.enums.EstadoCamion;
import pucp.edu.pe.glp_final.model.enums.TipoCamion;

import java.time.LocalDateTime;

@Data
public class CamionDTO {
    private String codigo;
    private TipoCamion tipo;
    private UbicacionDTO ubicacion;
    private EstadoCamion estado;
    private Double nivelGlpActual;
    private Double nivelCombustibleActual;
    private Double capacidadTanqueGLP;
    private Double pesoTara;
    private Boolean enMantenimiento;
    private Boolean averiado;
    private String motivoEstado;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaUltimoMantenimiento;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaProximoMantenimiento;
}