package pucp.edu.pe.glp_final.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pucp.edu.pe.glp_final.model.enums.TipoAlmacen;

@Entity
@Table(name = "almacenes")
@Data
@NoArgsConstructor
public class AlmacenEntity {
    @Id
    private String id;

    @Column(name = "ubicacion_x", nullable = false)
    private Integer ubicacionX;

    @Column(name = "ubicacion_y", nullable = false)
    private Integer ubicacionY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAlmacen tipo;

    @Column(name = "capacidad_maxima", nullable = false)
    private Double capacidadMaxima;

    @Column(name = "nivel_actual", nullable = false)
    private Double nivelActual;
}