package pucp.edu.pe.glp_final.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rutas")
@Data
@NoArgsConstructor
public class RutaEntity {
    @Id
    private String id;

    @Column(name = "codigo_camion", nullable = false)
    private String codigoCamion;

    @Column(name = "origen_x", nullable = false)
    private Integer origenX;

    @Column(name = "origen_y", nullable = false)
    private Integer origenY;

    @Column(name = "destino_x")
    private Integer destinoX;

    @Column(name = "destino_y")
    private Integer destinoY;

    @Column(name = "hora_inicio")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin_estimada")
    private LocalDateTime horaFinEstimada;

    @Column(name = "hora_fin_real")
    private LocalDateTime horaFinReal;

    @Column(name = "distancia_total")
    private Double distanciaTotal = 0.0;

    @Column(name = "consumo_combustible")
    private Double consumoCombustible = 0.0;

    @Column(name = "completada")
    private Boolean completada = false;

    @Column(name = "cancelada")
    private Boolean cancelada = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Relaciones
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RutaPedidoEntity> pedidosAsignados;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RutaNodoEntity> nodos;
}