package tech.zetapioneers.loan_application.filters;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.zetapioneers.loan_application.concreteservice.JwtServiceImpl;
import tech.zetapioneers.loan_application.exceptions.InvalidTokenException;
import tech.zetapioneers.loan_application.exceptions.TokenExpiredException;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtService;

    public JwtAuthenticationFilter(JwtServiceImpl jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                jwtService.verifyAndGet(token).ifPresent(claims -> {
                    // You can set more authorities if you have roles
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    claims.get("sub"),
                                    null,
                                    Collections.emptyList()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                });
            }
            catch(InvalidTokenException | TokenExpiredException exception){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + exception.getMessage() + "\"}");
                response.getWriter().flush();
                return;
            }
            catch (Exception exception){
                response.setStatus(HttpStatus.CONFLICT.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + exception.getMessage() + "\"}");
                response.getWriter().flush();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
