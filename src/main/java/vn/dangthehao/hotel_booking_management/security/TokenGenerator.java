package vn.dangthehao.hotel_booking_management.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Component
public class TokenGenerator {
    private final JWSSigner signer;
    private final String issuer;
    private final long acTokenValidDuration;
    private final long rfTokenValidDuration;

    public TokenGenerator(
            @Value("${jwt.private_key}") String privateKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access_token_valid_duration}") long acTokenValidDuration,
            @Value("${jwt.refresh_token_valid_duration}") long rfTokenValidDuration
    ) {
        if (privateKey == null || privateKey.length() < 64) {
            throw new IllegalArgumentException("Private key is must be at least 64 characters");
        }

        try {
            this.signer = new MACSigner(privateKey.getBytes(StandardCharsets.UTF_8));
            this.issuer = issuer;
            this.acTokenValidDuration = acTokenValidDuration;
            this.rfTokenValidDuration = rfTokenValidDuration;
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateAccessToken(User user) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .claim("userID", user.getId())
                .subject(user.getUsername())
                .issuer(this.issuer)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(this.acTokenValidDuration)))
                .claim("scope", buildScope(user))
                .build();

        return buildToken(claimsSet);
    }

    public String generateRefreshToken(User user) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .claim("userID", user.getId())
                .issuer(this.issuer)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusSeconds(this.rfTokenValidDuration)))
                .build();

        return buildToken(claimsSet);
    }

    public Map<String, String> generateTokenPair(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }

    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(user.getRole().getRoleName());
        user.getRole().getPermissions()
                .forEach(permission -> joiner.add(permission.getPermissionName()));

        return String.valueOf(joiner);
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
