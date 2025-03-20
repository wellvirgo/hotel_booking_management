package vn.dangthehao.hotel_booking_management.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    String[] PUBLIC_ENDPOINT={"/api/auth/login", "/api/auth/register", "/api/auth/refresh","/avatars/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorized -> authorized
                        .requestMatchers(PUBLIC_ENDPOINT)
                        .permitAll()
                        .requestMatchers("/api/admin/**", "/actuator/**")
                        .hasRole(Authorities.ROLE_ADMIN.replace("ROLE_", ""))
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
