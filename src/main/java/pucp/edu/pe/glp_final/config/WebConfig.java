package pucp.edu.pe.glp_final.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración web para CORS y WebSockets
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebConfig implements WebMvcConfigurer, WebSocketMessageBrokerConfigurer {

    /**
     * Configuración de CORS para permitir requests desde el frontend
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Configuración del broker de mensajes para WebSockets
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar broker simple en memoria
        config.enableSimpleBroker("/topic", "/queue");
        // Prefijo para mensajes enviados desde cliente al servidor
        config.setApplicationDestinationPrefixes("/app");
        // Prefijo para mensajes enviados a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registro de endpoints STOMP para WebSockets
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws-raw")
                .setAllowedOriginPatterns("*");
    }
}