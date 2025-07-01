package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pucp.edu.pe.glp_final.dto.BloqueoDTO;
import pucp.edu.pe.glp_final.model.Bloqueo;
import pucp.edu.pe.glp_final.model.Mapa;
import pucp.edu.pe.glp_final.model.Ubicacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapaService {

    private final AlmacenService almacenService;

    /**
     * Obtiene el mapa actual con bloqueos filtrados
     */
    public Mapa obtenerMapaActual(LocalDateTime momento) {
        Mapa mapa = new Mapa(); // 70x50 km por defecto

        // Filtrar bloqueos para el día actual
        LocalDate fecha = momento.toLocalDate();
        mapa.filtrarBloqueosParaFecha(fecha, fecha);

        log.debug("Mapa generado para momento: {}", momento);
        return mapa;
    }

    /**
     * Obtiene el mapa para un rango de fechas específico
     */
    public Mapa obtenerMapaParaRango(LocalDate fechaInicio, LocalDate fechaFin) {
        Mapa mapa = new Mapa();
        mapa.filtrarBloqueosParaFecha(fechaInicio, fechaFin);

        log.debug("Mapa generado para rango: {} - {}", fechaInicio, fechaFin);
        return mapa;
    }

    /**
     * Carga bloqueos desde archivo o base de datos
     */
    public void cargarBloqueos(List<BloqueoDTO> bloqueosDTO) {
        // Aquí se implementaría la carga de bloqueos
        // Por ahora es un stub
        log.info("Cargando {} bloqueos", bloqueosDTO.size());
    }

    /**
     * Verifica si una ubicación es válida en el mapa
     */
    public boolean esUbicacionValida(int x, int y) {
        return x >= 0 && x <= 70 && y >= 0 && y <= 50;
    }

    /**
     * Calcula la distancia Manhattan entre dos ubicaciones
     */
    public int calcularDistancia(Ubicacion origen, Ubicacion destino) {
        return Math.abs(origen.getX() - destino.getX()) +
                Math.abs(origen.getY() - destino.getY());
    }
}
