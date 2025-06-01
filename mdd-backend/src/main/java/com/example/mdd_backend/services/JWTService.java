package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.JWTResponseDTO;
import com.example.mdd_backend.errors.exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    private final JwtEncoder jwtEncoder;

    public JWTService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // While authentication
    public JWTResponseDTO getToken(Authentication authentication) {
        // authentication.getName() return the email of the user
        try {
            return generateToken(authentication.getName());
        } catch (Exception e) {
            logger.error("Error generating JWT token for user: {}", authentication.getName(), e);
            throw new AuthenticationException("Failed to generate JWT token");
        }
    }

    // while registration
    public JWTResponseDTO getTokenFromUserIdentifier(String userIdentifier) {

        try {
            return generateToken(userIdentifier);
        } catch (Exception e) {
            logger.error("Error generating JWT token for user identifier: {}", userIdentifier, e);
            throw new AuthenticationException("failed to generate JWT token");
        }

    }

    private JWTResponseDTO generateToken(String subject) {
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
                JwsHeader.with(MacAlgorithm.HS256).build(), claimsSet
        );

        // final JWT token
        String token = jwtEncoder.encode(parameters).getTokenValue();
        return new JWTResponseDTO(token);
    }
}
