package idatt2105.backend.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import idatt2105.backend.Model.User;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("loadUserByEmail(String email) was called with email: {}", email);
        
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            String role = (user.isAdmin()) ? "ROLE_ADMIN" : "ROLE_USER";
            List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(role));
            UserSecurityDetails userSecurityDetails = new UserSecurityDetails();
            userSecurityDetails.setEmail(user.getEmail());
            userSecurityDetails.setPassword(user.getHash());
            userSecurityDetails.setGrantedAuthorities(grantedAuthorities);
            userSecurityDetails.setUserId(user.getUserId());
            return userSecurityDetails;
        }
        else{
            LOGGER.warn("Could not find user with email: {}. Throwing exception", email);
            throw new UsernameNotFoundException("Email: " + email + "was not found");
        }

    }
}