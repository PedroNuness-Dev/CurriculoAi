package br.com.CurriculoAi.security;

import br.com.CurriculoAi.dto.auth.TokenDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class TokenService {

    private final SecretKey secretKey;
    private final String issuer;

    public TokenService(TokenProperties tokenProperties){
        secretKey = Keys.hmacShaKeyFor(tokenProperties.secret().getBytes(StandardCharsets.UTF_8));
        issuer = tokenProperties.issuer();
    }


    public TokenDetails generateToken(UserDetailsImpl userDetails){

        Instant now = Instant.now();
        Instant expiration = now.plus(2, ChronoUnit.HOURS);

        String token = Jwts.builder()
                .issuer(issuer)
                .signWith(secretKey)
                .subject(userDetails.getUsername()) // User's email
                .claim("id", userDetails.getUserId())
                .claim("name", userDetails.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .compact();

        return new TokenDetails(token, expiration);
    }

    public Optional<String> validateTokenAndGetSubject(String token){

        try {
            Claims claims = Jwts.parser()
                    .requireIssuer(issuer)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.ofNullable(claims.getSubject());
        }
        catch (JwtException | IllegalArgumentException e){
            log.warn("Token inválido: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
