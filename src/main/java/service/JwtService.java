package service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    @Value("${jwt.key}")
    private String SECRET;

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUser(token);
        Date expirationDate = extractExpiration(token);
        boolean isExpired = expirationDate.before(new Date());
        return username != null && userDetails.getUsername().equals(username) && !isExpired;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public Date extractExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJwt(token)
                .getBody();
        return claims.getExpiration();
    }

    public String extractUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSignKey())
                    .parseClaimsJwt(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            logger.warn(ANSI_PURPLE + "Expired JWT token{}" + ANSI_RESET, token);
            return null;
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

}
