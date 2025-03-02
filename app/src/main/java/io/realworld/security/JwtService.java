package io.realworld.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public final class JwtService {

    private static final Duration EXPIRE_DURATION = Duration.ofMinutes(5);
    private static final String ISSUER = "realworld-armeria-server";
    private static final String AUDIENCE = "realworld-armeria-client";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    @Inject
    public JwtService(final RSAPublicKey rsaPublicKey, final RSAPrivateKey rsaPrivateKey) {
        algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        verifier = JWT.require(algorithm)
                      .withIssuer(ISSUER)
                      .withAudience(AUDIENCE)
                      .build();
    }

    public String createToken(final String userId) {
        final Instant now = Instant.now();
        final String jwtId = UUID.randomUUID().toString();

        return JWT.create()
                  .withIssuer(ISSUER)
                  .withAudience(AUDIENCE)
                  .withSubject(userId)
                  .withIssuedAt(now)
                  .withExpiresAt(now.plus(EXPIRE_DURATION))
                  .withJWTId(jwtId)
                  .sign(algorithm);
    }

    public boolean verifyToken(final String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    public String getUserId(final String token) {
        final DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }
}
