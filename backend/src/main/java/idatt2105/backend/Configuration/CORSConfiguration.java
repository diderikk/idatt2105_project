package idatt2105.backend.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Configures CORS to correct localhost port
 */
@Configuration
public class CORSConfiguration {
    
    /**
	 * Allows CORS to frontend application
	 * @return new WebMvcConfigurer
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
				.allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedOriginPatterns("*");
			}
		};
	}
}
