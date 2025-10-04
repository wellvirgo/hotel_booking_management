package vn.dangthehao.hotel_booking_management.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.model.User;

@Component
public class JwtProvider {
  private final JWSSigner signer;
  private final String issuer;
  private final long acTokenValidDuration;

  public JwtProvider(
      @Value("${jwt.private-key}") String privateKey,
      @Value("${jwt.issuer}") String issuer,
      @Value("${jwt.valid-duration}") long validDuration) {

    if (issuer == null || issuer.isBlank())
      throw new IllegalArgumentException("JWT issuer must not be null or blank");

    if (validDuration < 0)
      throw new IllegalArgumentException("JWT issuer must be greater than zero");

    try {
      this.signer = new MACSigner(privateKey.getBytes(StandardCharsets.UTF_8));
    } catch (KeyLengthException e) {
      throw new IllegalArgumentException("JWT private key must be at least 64 characters");
    }

    this.issuer = issuer;
    this.acTokenValidDuration = validDuration;
  }

  public String generateToken(User user) {
    Instant now = Instant.now();

    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .subject(user.getId().toString())
            .issuer(this.issuer)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(this.acTokenValidDuration)))
            .claim("scope", user.getRole().getRoleName())
            .claim("version", user.getTokenVersion())
            .build();

    return buildToken(claimsSet);
  }

  private String buildToken(JWTClaimsSet claimsSet) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
    Payload payload = new Payload(claimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(this.signer);
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }

    return jwsObject.serialize();
  }
}
