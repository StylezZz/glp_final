package pucp.edu.pe.glp_final.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
public class PedidoEntity {
    @Id
    private String id;

    @Column(name = "id_cliente", nullable = false)
    private String idCliente;

    @Column(name = "ubicacion_x", nullable = false)
    private Integer ubicacionX;

    @Column(name = "ubicacion_y", nullable = false)
    private Integer ubicacionY;

    @Column(name = "cantidad_glp", nullable = false)
    private Double cantidadGlp;

    @Column(name = "hora_recepcion", nullable = false)
    private LocalDateTime horaRecepcion;

    @Column(name = "horas_limite_entrega", nullable = false)
    private Integer horasLimiteEntrega;

    @Column(name = "hora_entrega_programada")
    private LocalDateTime horaEntregaProgramada;

    @Column(name = "hora_entrega_real")
    private LocalDateTime horaEntregaReal;

    @Column(name = "camion_asignado")
    private String camionAsignado;

    @Column(name = "entregado")
    private Boolean entregado = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Método calculado para hora límite
    public LocalDateTime getHoraLimiteEntrega() {
        return horaRecepcion.plusHours(horasLimiteEntrega);
    }
}