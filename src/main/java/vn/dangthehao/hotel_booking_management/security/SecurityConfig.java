package vn.dangthehao.hotel_booking_management.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    CustomDecoder customDecoder;
    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    CustomAccessDeniedHandler customAccessDeniedHandler;
    TokenRevocationFilter tokenRevocationFilter;

    String[] PUBLIC_POST_ENDPOINT = {"/api/v1/auth/sessions", "/api/v1/auth/tokens", "/api/v1/users",
            "/api/v1/auth/passwords/resets/**"};
    String[] PUBLIC_GET_ENDPOINT = {"/avatars/**", "/roomTypeImages/**", "/hotelImages/**"};
    String[] PUBLIC_PUT_ENDPOINT = {"/api/v1/auth/passwords/resets"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorized -> authorized
                        // Public post endpoint
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINT).permitAll()

                        // Public get endpoint
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINT).permitAll()

                        // Public put endpoint
                        .requestMatchers(HttpMethod.PUT, PUBLIC_PUT_ENDPOINT).permitAll()

                        // Admin endpoint
                        .requestMatchers("/api/v1/admin/**", "/actuator/**")
                        .hasRole(Authorities.ROLE_ADMIN.replace("ROLE_", ""))

                        // Hotel owner endpoint
                        .requestMatchers("/api/v1/owner/**")
                        .hasRole(Authorities.ROLE_HOTEL_OWNER.replace("ROLE_", ""))

                        // Any other endpoint require authentication
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler));

        httpSecurity
                .oauth2ResourceServer(auth -> auth
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint));

        httpSecurity
                .addFilterBefore(tokenRevocationFilter, BasicAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
