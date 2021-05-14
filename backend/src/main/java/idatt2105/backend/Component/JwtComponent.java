package idatt2105.backend.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import idatt2105.backend.Model.UserSecurityDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * Handler class for JWT (JSON Web Tokens)
 */
public class JwtComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtComponent.class);
    private static final byte[] jwtKey = new byte[256];
    private static SecureRandom secureRandom = null;


    private JwtComponent(){}

    /**
     * Method for verifying JWT. This is important to
     * make sure that a user has the rights to excecute 
     * authorised tasks.
     * @param token
     * @return Authentication created from the JWT
     */
    public static Authentication verifyToken(String token){
        try{
            //Parses and verifies token
            token = token.replace("Bearer ", "");

            Jws<Claims> claimsJWs = Jwts.parserBuilder()
                .setSigningKey(getJWTSigningKey())
                .build().parseClaimsJws(token);
            Claims body = claimsJWs.getBody();

            String email = body.getSubject();

            int userId = (Integer)body.get("userId");
            
            List<Map<String, String>> authoritiesMapping = (List<Map<String, String>>) body.get("authorities");

            List<GrantedAuthority> grantedAuthorities = authoritiesMapping.stream().map(auth -> new SimpleGrantedAuthority(auth.get("authority"))).collect(Collectors.toList());

            UserSecurityDetails userSecurityDetails = new UserSecurityDetails();
            userSecurityDetails.setEmail(email);
            userSecurityDetails.setUserId(userId);
            userSecurityDetails.setGrantedAuthorities(grantedAuthorities);


            // Creates an authentication from the JWT token
            Authentication authentication = new UsernamePasswordAuthenticationToken(userSecurityDetails, token, grantedAuthorities);

            // Sets authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return authentication;

        }catch(JwtException e){
            LOGGER.warn("Something went wrong trying to verify JWT token: {}\nException message: {}", token, e.getMessage());
            return null;
        }
    }

    public static byte[] getJWTSigningKey(){
        if(secureRandom == null){
            secureRandom = new SecureRandom();
            secureRandom.nextBytes(jwtKey);
        }
        return jwtKey;
    }

}

