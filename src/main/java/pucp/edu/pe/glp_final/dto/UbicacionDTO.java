
package pucp.edu.pe.glp_final.dto;

import lombok.Data;

@Data
public class UbicacionDTO {
    private Integer x;
    private Integer y;

    public UbicacionDTO() {}

    public UbicacionDTO(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }
}