package br.com.CurriculoAi.security;

import br.com.CurriculoAi.dto.auth.TokenDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String keySecret;
    @Value("${api.security.token.issuer}")
    private String issuer;

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(keySecret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenDetails generateToken(UserDetailsImpl userDetails){

        Instant now = Instant.now();
        Instant expiration = now.plus(2, ChronoUnit.HOURS);

        String token = Jwts.builder()
                .issuer(issuer)
                .signWith(getSigningKey())
                .subject(userDetails.getUsername()) // User's email
                .claim("id", userDetails.getUserId())
                .claim("name", userDetails.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .compact();

        return new TokenDetails(token, expiration);
    }

    public String validateTokenAndGetSubject(String token){

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean isTokenValid(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch (ExpiredJwtException e){
            return false;
        }
        catch (JwtException e){
            return false;
        }
    }
}
