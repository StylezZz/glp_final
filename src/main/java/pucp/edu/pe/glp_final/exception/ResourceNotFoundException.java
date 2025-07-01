package pucp.edu.pe.glp_final.exception;

/**
 * Excepción para recursos no encontrados
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}