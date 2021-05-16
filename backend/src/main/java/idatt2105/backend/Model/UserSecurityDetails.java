package idatt2105.backend.Model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Implementation of UserDetails class used for user authorities.
 * Class also sets user email as username.
 */
@NoArgsConstructor
@Data
public class UserSecurityDetails implements UserDetails {
    private String password;
    private String email;
    private long userId;
    private List<? extends GrantedAuthority> grantedAuthorities;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;
    
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }
    @Override
    public String getUsername() {
        return email;
    }

}
