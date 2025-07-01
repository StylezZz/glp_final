package pucp.edu.pe.glp_final.exception;

/**
 * Excepción para errores de simulación
 */
public class SimulationException extends RuntimeException {
    public SimulationException(String message) {
        super(message);
    }

    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }
}