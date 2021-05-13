package idatt2105.backend.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import idatt2105.backend.Component.CORSConfigComponent;
import idatt2105.backend.Security.Filter.JwtUsernameAndPasswordAuthenticationFilter;

@Configuration
@Profile("test")
public class WebSecurityForTests extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and()
        .requiresChannel().anyRequest().requiresSecure().and()
        .addFilterBefore(new CORSConfigComponent(), JwtUsernameAndPasswordAuthenticationFilter.class);
    }
}
