package pucp.edu.pe.glp_final.exception;

/**
 * Excepción para errores de optimización
 */
public class OptimizationException extends RuntimeException {
    public OptimizationException(String message) {
        super(message);
    }

    public OptimizationException(String message, Throwable cause) {
        super(message, cause);
    }
}