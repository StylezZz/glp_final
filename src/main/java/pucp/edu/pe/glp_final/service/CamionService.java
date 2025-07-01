package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucp.edu.pe.glp_final.dto.CamionDTO;
import pucp.edu.pe.glp_final.entity.CamionEntity;
import pucp.edu.pe.glp_final.mapper.EntityMapper;
import pucp.edu.pe.glp_final.model.Camion;
import pucp.edu.pe.glp_final.model.Ubicacion;
import pucp.edu.pe.glp_final.model.enums.EstadoCamion;
import pucp.edu.pe.glp_final.model.enums.TipoCamion;
import pucp.edu.pe.glp_final.repository.CamionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamionService {

    private final CamionRepository camionRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public List<CamionDTO> obtenerTodos() {
        List<CamionEntity> entities = camionRepository.findAll();
        return entityMapper.toCamionDTOList(entities);
    }

    @Transactional(readOnly = true)
    public Optional<CamionDTO> obtenerPorCodigo(String codigo) {
        return camionRepository.findById(codigo)
                .map(entityMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<CamionDTO> obtenerDisponibles() {
        List<CamionEntity> entities = camionRepository.findByEstado(EstadoCamion.DISPONIBLE);
        return entityMapper.toCamionDTOList(entities);
    }

    @Transactional(readOnly = true)
    public List<Camion> obtenerCamionesModelo() {
        List<CamionEntity> entities = camionRepository.findAll();
        return entityMapper.toCamionModelList(entities);
    }

    @Transactional(readOnly = true)
    public List<Camion> obtenerDisponiblesModelo() {
        List<CamionEntity> entities = camionRepository.findByEstado(EstadoCamion.DISPONIBLE);
        return entityMapper.toCamionModelList(entities);
    }

    @Transactional
    public CamionDTO crear(CamionDTO camionDTO) {
        CamionEntity entity = entityMapper.toEntity(camionDTO);
        entity = camionRepository.save(entity);
        log.info("Camión creado: {}", entity.getCodigo());
        return entityMapper.toDTO(entity);
    }

    @Transactional
    public Optional<CamionDTO> actualizar(String codigo, CamionDTO camionDTO) {
        return camionRepository.findById(codigo)
                .map(entity -> {
                    entity.setUbicacionX(camionDTO.getUbicacion().getX());
                    entity.setUbicacionY(camionDTO.getUbicacion().getY());
                    entity.setEstado(camionDTO.getEstado());
                    entity.setNivelGlpActual(camionDTO.getNivelGlpActual());
                    entity.setNivelCombustibleActual(camionDTO.getNivelCombustibleActual());
                    entity.setEnMantenimiento(camionDTO.getEnMantenimiento());
                    entity.setAveriado(camionDTO.getAveriado());
                    entity.setMotivoEstado(camionDTO.getMotivoEstado());

                    entity = camionRepository.save(entity);
                    log.info("Camión actualizado: {}", codigo);
                    return entityMapper.toDTO(entity);
                });
    }

    @Transactional
    public void actualizarEstado(String codigo, EstadoCamion estado, String motivo) {
        camionRepository.findById(codigo)
                .ifPresent(entity -> {
                    entity.setEstado(estado);
                    entity.setMotivoEstado(motivo);
                    camionRepository.save(entity);
                    log.info("Estado de camión {} actualizado a: {}", codigo, estado);
                });
    }

    @Transactional
    public void actualizarUbicacion(String codigo, int x, int y) {
        camionRepository.findById(codigo)
                .ifPresent(entity -> {
                    entity.setUbicacionX(x);
                    entity.setUbicacionY(y);
                    camionRepository.save(entity);
                    log.debug("Ubicación de camión {} actualizada a: ({},{})", codigo, x, y);
                });
    }

    @Transactional
    public void programarMantenimiento(String codigo, LocalDateTime fecha) {
        camionRepository.findById(codigo)
                .ifPresent(entity -> {
                    entity.setFechaProximoMantenimiento(fecha);
                    camionRepository.save(entity);
                    log.info("Mantenimiento programado para camión {} en fecha: {}", codigo, fecha);
                });
    }

    @Transactional
    public void iniciarMantenimiento(String codigo) {
        camionRepository.findById(codigo)
                .ifPresent(entity -> {
                    entity.setEnMantenimiento(true);
                    entity.setEstado(EstadoCamion.EN_MANTENIMIENTO);
                    entity.setFechaUltimoMantenimiento(LocalDateTime.now());
                    entity.setMotivoEstado("Mantenimiento preventivo iniciado");
                    camionRepository.save(entity);
                    log.info("Mantenimiento iniciado para camión: {}", codigo);
                });
    }

    @Transactional
    public void finalizarMantenimiento(String codigo) {
        camionRepository.findById(codigo)
                .ifPresent(entity -> {
                    entity.setEnMantenimiento(false);
                    entity.setEstado(EstadoCamion.DISPONIBLE);
                    entity.setMotivoEstado("Disponible después de mantenimiento");
                    // Recarga completa después del mantenimiento
                    entity.setNivelCombustibleActual(25.0);
                    camionRepository.save(entity);
                    log.info("Mantenimiento finalizado para camión: {}", codigo);
                });
    }

    @Transactional
    public boolean eliminar(String codigo) {
        if (camionRepository.existsById(codigo)) {
            camionRepository.deleteById(codigo);
            log.info("Camión eliminado: {}", codigo);
            return true;
        }
        return false;
    }

    @Transactional
    public void inicializarFlota() {
        log.info("Inicializando flota de camiones...");

        // Ubicación central por defecto
        Ubicacion ubicacionCentral = new Ubicacion(12, 8);

        // Crear camiones tipo TA (2 unidades)
        for (int i = 1; i <= 2; i++) {
            CamionEntity camion = new CamionEntity();
            camion.setCodigo(String.format("TA%02d", i));
            camion.setTipo(TipoCamion.TA);
            camion.setUbicacionX(ubicacionCentral.getX());
            camion.setUbicacionY(ubicacionCentral.getY());
            camion.setEstado(EstadoCamion.DISPONIBLE);
            camion.setNivelGlpActual(0.0);
            camion.setNivelCombustibleActual(25.0);
            camion.setEnMantenimiento(false);
            camion.setAveriado(false);

            if (!camionRepository.existsById(camion.getCodigo())) {
                camionRepository.save(camion);
            }
        }

        // Crear camiones tipo TB (4 unidades)
        for (int i = 1; i <= 4; i++) {
            CamionEntity camion = new CamionEntity();
            camion.setCodigo(String.format("TB%02d", i));
            camion.setTipo(TipoCamion.TB);
            camion.setUbicacionX(ubicacionCentral.getX());
            camion.setUbicacionY(ubicacionCentral.getY());
            camion.setEstado(EstadoCamion.DISPONIBLE);
            camion.setNivelGlpActual(0.0);
            camion.setNivelCombustibleActual(25.0);
            camion.setEnMantenimiento(false);
            camion.setAveriado(false);

            if (!camionRepository.existsById(camion.getCodigo())) {
                camionRepository.save(camion);
            }
        }

        // Crear camiones tipo TC (4 unidades)
        for (int i = 1; i <= 4; i++) {
            CamionEntity camion = new CamionEntity();
            camion.setCodigo(String.format("TC%02d", i));
            camion.setTipo(TipoCamion.TC);
            camion.setUbicacionX(ubicacionCentral.getX());
            camion.setUbicacionY(ubicacionCentral.getY());
            camion.setEstado(EstadoCamion.DISPONIBLE);
            camion.setNivelGlpActual(0.0);
            camion.setNivelCombustibleActual(25.0);
            camion.setEnMantenimiento(false);
            camion.setAveriado(false);

            if (!camionRepository.existsById(camion.getCodigo())) {
                camionRepository.save(camion);
            }
        }

        // Crear camiones tipo TD (10 unidades)
        for (int i = 1; i <= 10; i++) {
            CamionEntity camion = new CamionEntity();
            camion.setCodigo(String.format("TD%02d", i));
            camion.setTipo(TipoCamion.TD);
            camion.setUbicacionX(ubicacionCentral.getX());
            camion.setUbicacionY(ubicacionCentral.getY());
            camion.setEstado(EstadoCamion.DISPONIBLE);
            camion.setNivelGlpActual(0.0);
            camion.setNivelCombustibleActual(25.0);
            camion.setEnMantenimiento(false);
            camion.setAveriado(false);

            if (!camionRepository.existsById(camion.getCodigo())) {
                camionRepository.save(camion);
            }
        }

        log.info("Flota de camiones inicializada: 20 camiones totales");
    }
}
