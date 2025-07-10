package hcmute.edu.vn.baiTapCuoiKyKTPM.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST APIs
                .csrf(csrf -> csrf.disable())

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authorization
                .authorizeHttpRequests(authz -> authz
                        // Allow all requests to student APIs
                        .requestMatchers("/api/students/**").permitAll()

                        // Allow all requests to other APIs (if needed)
                        .requestMatchers("/api/**").permitAll()

                        // Allow access to static resources
                        .requestMatchers("/", "/index.html", "/static/**", "/public/**").permitAll()

                        // Allow access to actuator endpoints (if using Spring Boot Actuator)
                        .requestMatchers("/actuator/**").permitAll()

                        // Require authentication for any other request
                        .anyRequest().authenticated()
                )

                // Configure session management for stateless REST APIs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Disable form login (since we're building REST APIs)
                .formLogin(form -> form.disable())

                // Disable HTTP Basic authentication
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins (for development - should be more restrictive in production)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Apply CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
