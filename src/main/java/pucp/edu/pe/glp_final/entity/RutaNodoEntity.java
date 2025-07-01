package pucp.edu.pe.glp_final.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ruta_nodos")
@Data
@NoArgsConstructor
public class RutaNodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutaEntity ruta;

    @Column(name = "ubicacion_x", nullable = false)
    private Integer ubicacionX;

    @Column(name = "ubicacion_y", nullable = false)
    private Integer ubicacionY;

    @Column(name = "orden_secuencia", nullable = false)
    private Integer ordenSecuencia;

    @Column(name = "es_parada")
    private Boolean esParada = false;
}