package idatt2105.backend.Security.Filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import idatt2105.backend.Component.JwtComponent;
import idatt2105.backend.Model.UserSecurityDetails;
import idatt2105.backend.Model.DTO.UsernamePasswordDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Filter for authenticating user from username and password, if successful it creates and returns a JWT token
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUsernameAndPasswordAuthenticationFilter.class);

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Attempts to login to an existing user from request username and password
     * @param request
     * @param response
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UsernamePasswordDTO authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernamePasswordDTO.class);

            Authentication auth = new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                    authenticationRequest.getPassword());

            // Uses authentication provider to authenticate username and password
            auth = authenticationManager.authenticate(auth);

            return auth;

        } catch (IOException ex) {
            LOGGER.info("Something went wrong trying to authenticate. Exception: {}", ex.getLocalizedMessage(), ex.fillInStackTrace());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }

    /**
     * If username and password was succefully authenticated, it creates a token and returns it
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        //Logged in user
        UserSecurityDetails user = (UserSecurityDetails) authResult.getPrincipal();

        //Creates a token that will last for 30 minutes
        String token = Jwts.builder().setSubject(authResult.getName()).claim("authorities", authResult.getAuthorities())
                .claim("userId", user.getUserId())
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(Keys.hmacShaKeyFor(JwtComponent.getJWTSigningKey())).compact();

        // Writes the token and userId as JSON
        // Could used DTO and objectMapper
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().write("{\n\t\"token\": \"" + token + "\",\n\t\"userId\": " + user.getUserId() + " \n}");
        response.getWriter().flush();
    }
}

