package idatt2105.backend.Configuration;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;

import idatt2105.backend.Model.User;
import idatt2105.backend.Repository.UserRepository;

/**
 * Configuration class for configuring a root user
 */
@Configuration
@PropertySource("classpath:config.properties")
public class RootUserConfiguration {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Value("${root.email}")
    private String email;
    
    @Value("${root.password}")
    private String password;
    /**
     * Adds a root user if does not exist
     * @return
     */
    @Bean
    public CommandLineRunner run(){
        return args -> {
            Optional<User> optionalUser = userRepository.findUserByEmail(email);
            if(!optionalUser.isPresent()){
                User user = new User();
                user.setAdmin(true);
                user.setEmail(email);
                user.setExpirationDate(null);
                user.setFirstName("firstName");
                user.setLastName("lastName");
                user.setPhoneNumber("12345678");
                user.setHash(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        };
    }
}
