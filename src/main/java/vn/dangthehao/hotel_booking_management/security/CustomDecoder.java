package vn.dangthehao.hotel_booking_management.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;


@Slf4j
@Component
public class CustomDecoder implements JwtDecoder {
    private final NimbusJwtDecoder nimbusJwtDecoder;

    public CustomDecoder(@Value("${jwt.private_key}") String privateKey,
                         @Value("${jwt.issuer}") String issuer) {
        if (privateKey == null || privateKey.length() < 64) {
            throw new IllegalArgumentException("Private key is must be at least 64 characters");
        }
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(privateKey.getBytes(StandardCharsets.UTF_8), "HS512");
        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();

        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(issuer);
        this.nimbusJwtDecoder.setJwtValidator(validator);
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        return this.nimbusJwtDecoder.decode(token);
    }
}
