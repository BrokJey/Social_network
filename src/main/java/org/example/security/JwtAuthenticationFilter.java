package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final  CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //1 Заголовок
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUsername(token);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                        var userDetails = userDetailsService.loadUserByUsername(username);

                        var auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(auth);

                        log.info("Info: Пользователь {} успешно аутенфицирован", username);
                    }
                } else {
                    log.warn("Невалидный JWT-токен");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Невалидный токен");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка JwtAuthenticationFilter: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ошибка авторизации");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
