package id.ac.ui.cs.gatherlove.campaigndonationwallet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidationService jwtValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtValidationService.validateToken(token)) {
            try {
                // Extract the JWT object from the token
                Jwt jwt = jwtValidationService.parseJwt(token);

                // Extract email from JWT claims
                String email = jwt.getClaimAsString("email");

                // Extract roles from JWT claims
                List<String> roles = jwt.getClaimAsStringList("roles");

                // Create authorities from roles
                Collection<GrantedAuthority> authorities = roles.stream()
//                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .map(role -> {
                            // Pastikan role memiliki prefix ROLE_
                            String authorityName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                            log.debug("Creating authority: {}", authorityName);
                            return new SimpleGrantedAuthority(authorityName);
                        })
                        .collect(Collectors.toList());

                // Create Authentication object
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities, email);
                log.debug("Authorities granted: {}", authorities);

                // Set Authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("User {} authenticated with roles {}", email, roles);
            } catch (Exception e) {
                log.error("Authentication error: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}