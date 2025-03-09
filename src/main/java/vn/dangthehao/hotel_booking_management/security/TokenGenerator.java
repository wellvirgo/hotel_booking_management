package vn.dangthehao.hotel_booking_management.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Component
public class TokenGenerator {
    private final JWSSigner signer;
    private final String issuer;
    private final long validDuration;

    public TokenGenerator(
            @Value("${jwt.private_key}") String privateKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.valid_duration}") long validDuration){
        if(privateKey==null||privateKey.length()<64){
            throw new IllegalArgumentException("Private key is must be at least 64 characters");
        }

        try {
            this.signer=new MACSigner(privateKey.getBytes(StandardCharsets.UTF_8));
            this.issuer=issuer;
            this.validDuration=validDuration;
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(User user){
        JWSHeader header=new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet= new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(this.issuer)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(this.validDuration, ChronoUnit.HOURS)))
                .claim("scope",buildScope(user))
                .build();
        Payload payload=new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject=new JWSObject(header,payload);
        try {
            jwsObject.sign(this.signer);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    private String buildScope(User user){
        StringJoiner joiner=new StringJoiner(" ");
        joiner.add(user.getRole().getRoleName());
        user.getRole().getPermissions()
                .forEach(permission -> joiner.add(permission.getPermissionName()));
        return String.valueOf(joiner);
    }
}
