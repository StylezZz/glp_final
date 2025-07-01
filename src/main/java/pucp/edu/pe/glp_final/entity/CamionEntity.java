package pucp.edu.pe.glp_final.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import pucp.edu.pe.glp_final.model.enums.EstadoCamion;
import pucp.edu.pe.glp_final.model.enums.TipoCamion;

import java.time.LocalDateTime;

@Entity
@Table(name = "camiones")
@Data
@NoArgsConstructor
public class CamionEntity {
    @Id
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCamion tipo;

    @Column(name = "ubicacion_x", nullable = false)
    private Integer ubicacionX;

    @Column(name = "ubicacion_y", nullable = false)
    private Integer ubicacionY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCamion estado;

    @Column(name = "nivel_glp_actual")
    private Double nivelGlpActual = 0.0;

    @Column(name = "nivel_combustible_actual")
    private Double nivelCombustibleActual = 25.0;

    @Column(name = "fecha_ultimo_mantenimiento")
    private LocalDateTime fechaUltimoMantenimiento;

    @Column(name = "fecha_proximo_mantenimiento")
    private LocalDateTime fechaProximoMantenimiento;

    @Column(name = "en_mantenimiento")
    private Boolean enMantenimiento = false;

    @Column(name = "averiado")
    private Boolean averiado = false;

    @Column(name = "motivo_estado")
    private String motivoEstado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}