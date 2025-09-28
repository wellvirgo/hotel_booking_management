package vn.dangthehao.hotel_booking_management.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.service.UserService;

@Slf4j
@Component
public class CustomDecoder implements JwtDecoder {
  private final NimbusJwtDecoder nimbusJwtDecoder;
  private final UserService userService;

  public CustomDecoder(
      @Value("${jwt.private-key}") String privateKey,
      @Value("${jwt.issuer}") String issuer,
      UserService userService) {
    if (privateKey == null || privateKey.length() < 64) {
      throw new IllegalArgumentException("Private key is must be at least 64 characters");
    }

    this.userService = userService;

    var secretKeySpec = new SecretKeySpec(privateKey.getBytes(StandardCharsets.UTF_8), "HS512");
    this.nimbusJwtDecoder =
        NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();

    OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(issuer);
    OAuth2TokenValidator<Jwt> customValidator = tokenVersionValidator();
    Collection<OAuth2TokenValidator<Jwt>> validators = List.of(defaultValidator, customValidator);

    this.nimbusJwtDecoder.setJwtValidator(combineValidators(validators));
  }

  @Override
  public Jwt decode(String token) throws JwtException {
    return this.nimbusJwtDecoder.decode(token);
  }

  private OAuth2TokenValidator<Jwt> tokenVersionValidator() {
    return jwt -> {
      Long userId = Long.parseLong(jwt.getSubject());
      long tokenVersionInToken = jwt.getClaim("version");
      long tokenVersionInUser = this.userService.getTokenVersion(userId);

      if (tokenVersionInToken != tokenVersionInUser) {
        return OAuth2TokenValidatorResult.failure(
            new OAuth2Error("invalid_token", "Token version mismatch", null));
      }

      return OAuth2TokenValidatorResult.success();
    };
  }

  private OAuth2TokenValidator<Jwt> combineValidators(
      Collection<OAuth2TokenValidator<Jwt>> validators) {
    return new DelegatingOAuth2TokenValidator<>(validators);
  }
}
