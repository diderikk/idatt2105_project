package idatt2105.backend.Configuration;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfiguration {
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

}
