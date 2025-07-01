package pucp.edu.pe.glp_final.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pucp.edu.pe.glp_final.dto.AlmacenDTO;
import pucp.edu.pe.glp_final.entity.AlmacenEntity;
import pucp.edu.pe.glp_final.mapper.EntityMapper;
import pucp.edu.pe.glp_final.model.Almacen;
import pucp.edu.pe.glp_final.model.enums.TipoAlmacen;
import pucp.edu.pe.glp_final.repository.AlmacenRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlmacenService {

    private final AlmacenRepository almacenRepository;
    private final EntityMapper entityMapper;

    @Transactional(readOnly = true)
    public List<AlmacenDTO> obtenerTodos() {
        List<AlmacenEntity> entities = almacenRepository.findAll();
        return entityMapper.toAlmacenDTOList(entities);
    }

    @Transactional(readOnly = true)
    public Optional<AlmacenDTO> obtenerPorId(String id) {
        return almacenRepository.findById(id)
                .map(entityMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<AlmacenDTO> obtenerPorTipo(TipoAlmacen tipo) {
        List<AlmacenEntity> entities = almacenRepository.findByTipo(tipo);
        return entityMapper.toAlmacenDTOList(entities);
    }

    @Transactional(readOnly = true)
    public Optional<AlmacenDTO> obtenerCentral() {
        List<AlmacenEntity> entities = almacenRepository.findByTipo(TipoAlmacen.PRINCIPAL);
        return entities.stream().findFirst().map(entityMapper::toDTO);
    }

    @Transactional
    public void inicializarAlmacenes() {
        log.info("Inicializando almacenes...");

        // Almacén Central
        if (!almacenRepository.existsById("CENTRAL")) {
            AlmacenEntity central = new AlmacenEntity();
            central.setId("CENTRAL");
            central.setUbicacionX(12);
            central.setUbicacionY(8);
            central.setTipo(TipoAlmacen.PRINCIPAL);
            central.setCapacidadMaxima(Double.MAX_VALUE);
            central.setNivelActual(Double.MAX_VALUE);
            almacenRepository.save(central);
        }

        // Almacén Norte
        if (!almacenRepository.existsById("NORTE")) {
            AlmacenEntity norte = new AlmacenEntity();
            norte.setId("NORTE");
            norte.setUbicacionX(42);
            norte.setUbicacionY(42);
            norte.setTipo(TipoAlmacen.INTERMEDIO);
            norte.setCapacidadMaxima(160.0);
            norte.setNivelActual(160.0);
            almacenRepository.save(norte);
        }

        // Almacén Este
        if (!almacenRepository.existsById("ESTE")) {
            AlmacenEntity este = new AlmacenEntity();
            este.setId("ESTE");
            este.setUbicacionX(63);
            este.setUbicacionY(3);
            este.setTipo(TipoAlmacen.INTERMEDIO);
            este.setCapacidadMaxima(160.0);
            este.setNivelActual(160.0);
            almacenRepository.save(este);
        }

        log.info("Almacenes inicializados");
    }

    @Transactional
    public void recargarAlmacenesIntermedios() {
        List<AlmacenEntity> intermedios = almacenRepository.findByTipo(TipoAlmacen.INTERMEDIO);
        for (AlmacenEntity almacen : intermedios) {
            almacen.setNivelActual(almacen.getCapacidadMaxima());
            almacenRepository.save(almacen);
        }
        log.info("Almacenes intermedios recargados");
    }

    @Transactional
    public boolean extraer(String almacenId, Double cantidad) {
        return almacenRepository.findById(almacenId)
                .map(entity -> {
                    if (entity.getTipo() == TipoAlmacen.PRINCIPAL ||
                            entity.getNivelActual() >= cantidad) {

                        if (entity.getTipo() != TipoAlmacen.PRINCIPAL) {
                            entity.setNivelActual(entity.getNivelActual() - cantidad);
                        }
                        almacenRepository.save(entity);
                        log.debug("Extraídos {}m³ del almacén {}", cantidad, almacenId);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }
}
