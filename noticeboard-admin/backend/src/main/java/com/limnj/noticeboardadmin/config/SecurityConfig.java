package com.limnj.noticeboardadmin.config;

import com.limnj.noticeboardadmin.config.handler.CustomAccessDeniedHandler;
import com.limnj.noticeboardadmin.config.filter.JwtExceptionFilter;
import com.limnj.noticeboardadmin.config.filter.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${app.cors.allowed-origins:http://localhost:9000}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, Environment environment) throws Exception {
        http
                .authorizeHttpRequests(request -> {
                    request.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    if (environment.acceptsProfiles(Profiles.of("(default | local | dev) & !prod"))) {
                        request.requestMatchers(GET, "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll();
                    }
                    request
                            .requestMatchers(POST, "/member", "/login", "/email-verify", "/qr-verify",
                                    "/resend-code", "/refresh", "/fcm/token").permitAll()
                            .requestMatchers(GET, "/actuator/health/readiness", "/templates/inventory_template.xlsx").permitAll()
                            .requestMatchers(POST, "/qr-register").authenticated()
                            .requestMatchers(GET, "/notices", "/notice/{noticeId}", "/categories")
                                    .hasAnyRole("USER", "MANAGER", "ADMIN")
                            .requestMatchers(POST, "/notice").hasAnyRole("USER", "MANAGER", "ADMIN")
                            .requestMatchers(PUT, "/notice").hasAnyRole("USER", "MANAGER", "ADMIN")
                            .requestMatchers(DELETE, "/notice/{noticeId}").hasAnyRole("USER", "MANAGER", "ADMIN")
                            .requestMatchers(GET, "/inventories", "/inventories/{inventoryName}").hasAnyRole("MANAGER", "ADMIN")
                            .requestMatchers(POST, "/category", "/inventories/parser", "/inventories/bulk").hasAnyRole("MANAGER", "ADMIN")
                            .requestMatchers(DELETE, "/category/{categoryId}").hasAnyRole("MANAGER", "ADMIN")
                            .requestMatchers(POST, "/push/all", "/push/user").hasRole("ADMIN")
                            .requestMatchers(GET, "/daily/logs/{fromDate}/{toDate}").hasRole("ADMIN")
                            .anyRequest().denyAll();
                })
                // security 6.1.0 부터 권장
                .csrf(AbstractHttpConfigurer::disable) // .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtTokenFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, exception) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler(customAccessDeniedHandler) 
                )
        ;

        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
