package idatt2105.backend.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import idatt2105.backend.Component.JwtComponent;
/**
 * Configuration class for WebSocket chat connection
 */
@Configuration
@Profile("!test")
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    /**
     * Method that configures MessageBroker to our chat endpoints
     * Setting up broker is important for secure and reliable messaging.
     * @param config
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/api/v1/chat");
        config.setApplicationDestinationPrefixes("/api/v1/chat");
    }

    /**
     * Integrates support for Stomp (Simple Text Orientated Messaging Protocol)
     * One of the main components of Spring messaging framework
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/websocket").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Configures MessageChannel class so that authorised users are able to send messages
     * @param registration
     */
    // @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authToken = accessor.getFirstNativeHeader("Authorization");
                     if(JwtComponent.verifyToken(authToken) == null) return null;
                }

                return message;
            }
        });
    }

}
