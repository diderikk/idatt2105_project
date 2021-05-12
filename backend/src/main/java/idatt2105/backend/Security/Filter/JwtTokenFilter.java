package idatt2105.backend.Security.Filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import idatt2105.backend.Component.JwtComponent;

/**
 * Filter for verifying a token given in request header
 */
public class JwtTokenFilter extends OncePerRequestFilter{

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    /**
     * Verifies the JWT token from request, and returns a 403 if it has expired
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        LOGGER.info("doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) is called");
        

        String token = request.getHeader("Authorization");
        JwtComponent.verifyToken(token);
            // Sends the request to the next filter, which will be exception-handler and then the controller
        filterChain.doFilter(request, response);

    }
    
}
