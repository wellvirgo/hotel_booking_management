package vn.dangthehao.hotel_booking_management.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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

    String[] PUBLIC_POST_ENDPOINT = {"/api/auth/sessions", "/api/auth/tokens", "/api/users",
            "/api/auth/passwords/resets/**"};
    String[] PUBLIC_GET_ENDPOINT = {"/avatars/**"};
    String[] PUBLIC_PUT_ENDPOINT={"/api/auth/passwords/resets"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorized -> authorized
                        // Public post endpoint
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINT).permitAll()

                        // Public get endpoint
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINT).permitAll()

                        // Public put endpoint
                        .requestMatchers(HttpMethod.PUT, PUBLIC_PUT_ENDPOINT).permitAll()

                        // Admin endpoint
                        .requestMatchers("/api/admin/**", "/actuator/**")
                        .hasRole(Authorities.ROLE_ADMIN.replace("ROLE_", ""))

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
}
