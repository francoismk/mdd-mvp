package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.AuthResponseDTO;
import com.example.mdd_backend.errors.exceptions.AuthenticationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Service for JWT token generation and management.
 *
 * Generates JWT tokens with 24-hour expiration for authentication.
 */
@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(
        JWTService.class
    );

    private final JwtEncoder jwtEncoder;

    public JWTService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Generates JWT token from authenticated user.
     *
     * @param authentication Spring Security authentication object
     * @return JWT token with 24-hour validity
     * @throws AuthenticationException On token generation failure
     */
    public AuthResponseDTO getToken(Authentication authentication) {
        try {
            return generateToken(authentication.getName());
        } catch (Exception e) {
            logger.error(
                "Error generating JWT token for user: {}",
                authentication.getName(),
                e
            );
            throw new AuthenticationException("Failed to generate JWT token");
        }
    }

    /**
     * Generates JWT token from user identifier (for registration).
     *
     * @param userIdentifier User email or username
     * @return JWT token with 24-hour validity
     * @throws AuthenticationException On token generation failure
     */
    public AuthResponseDTO getTokenFromUserIdentifier(String userIdentifier) {
        try {
            return generateToken(userIdentifier);
        } catch (Exception e) {
            logger.error(
                "Error generating JWT token for user identifier: {}",
                userIdentifier,
                e
            );
            throw new AuthenticationException("failed to generate JWT token");
        }
    }

    private AuthResponseDTO generateToken(String subject) {
        Instant now = Instant.now();
        // JwtClaimSet = body of the JWT
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.DAYS))
            .subject(subject)
            .build();

        // build the JWT with the claims and header
        JwtEncoderParameters parameters = JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.HS256).build(),
            claimsSet
        );

        // final JWT token
        String token = jwtEncoder.encode(parameters).getTokenValue();
        return new AuthResponseDTO(token);
    }
}
