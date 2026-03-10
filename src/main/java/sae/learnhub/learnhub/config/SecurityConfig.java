package sae.learnhub.learnhub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import sae.learnhub.learnhub.application.Service.CustomUserDetailsService;
import sae.learnhub.learnhub.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/debug/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                                "/swagger-resources/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cours/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Sécurisation spécifique pour la gestion des inscriptions
                        .requestMatchers(HttpMethod.PATCH, "/api/inscriptions/*/statut")
                        .hasAnyRole("ADMIN", "PROFESSEUR")
                        .requestMatchers(HttpMethod.POST, "/api/cours/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/cours/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cours/**").authenticated()
                        // Endpoints chapitres
                        .requestMatchers(HttpMethod.GET, "/api/cours/*/chapitres/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cours/*/chapitres/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/cours/*/chapitres/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cours/*/chapitres/**").authenticated()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            new ObjectMapper().writeValue(response.getOutputStream(),
                                    Map.of("error", "Non authentifié",
                                            "message",
                                            "Vous devez être connecté pour accéder à cette ressource. Veuillez vous authentifier via /api/auth/login.",
                                            "status", 401));
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            new ObjectMapper().writeValue(response.getOutputStream(),
                                    Map.of("error", "Accès refusé",
                                            "message",
                                            "Vous n'avez pas les droits nécessaires pour accéder à cette ressource.",
                                            "status", 403));
                        }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    /**
     * Disable auto-registration of JwtFilter as a servlet filter.
     * JwtFilter must ONLY run inside the Spring Security filter chain (via
     * addFilterBefore).
     * Without this, @Component causes double-registration: once as a servlet filter
     * (before Security) and once inside the Security chain. The servlet-level
     * instance
     * sets authentication, but SecurityContextHolderFilter resets the context to
     * empty
     * (STATELESS session), and OncePerRequestFilter skips the Security-chain
     * instance
     * → resulting in 401 on all protected endpoints.
     */
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter filter) {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
