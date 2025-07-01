
package pucp.edu.pe.glp_final.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pucp.edu.pe.glp_final.service.AlmacenService;
import pucp.edu.pe.glp_final.service.CamionService;

import java.util.concurrent.Executor;

/**
 * Configuración de inicialización de datos y tareas asíncronas
 */
@Configuration
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class InitializationConfig {

    private final CamionService camionService;
    private final AlmacenService almacenService;

    /**
     * Inicialización de datos al arrancar la aplicación
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Iniciando carga de datos iniciales...");

            try {
                // Inicializar almacenes
                almacenService.inicializarAlmacenes();
                log.info("✓ Almacenes inicializados");

                // Inicializar flota de camiones
                camionService.inicializarFlota();
                log.info("✓ Flota de camiones inicializada");

                log.info("Inicialización de datos completada exitosamente");

            } catch (Exception e) {
                log.error("Error durante la inicialización de datos", e);
                throw e;
            }
        };
    }

    /**
     * Configuración del pool de threads para tareas asíncronas
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("PLG-Async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("Configurado pool de threads asíncronos: 4-8 threads");
        return executor;
    }
}