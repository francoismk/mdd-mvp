package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.JWTResponseDTO;
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

    private final JwtEncoder jwtEncoder;

    public JWTService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // While authentication
    public JWTResponseDTO getToken(Authentication authentication) {
        // authentication.getName() return the email of the user
        return generateToken(authentication.getName());
    }

    // while registration
    public JWTResponseDTO getTokenFromUserIdentifier(String userIdentifier) {
        return generateToken(userIdentifier);
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
