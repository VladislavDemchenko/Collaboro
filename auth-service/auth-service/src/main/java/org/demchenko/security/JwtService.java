package org.demchenko.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * This class provides services for generating and validating JSON Web Tokens (JWTs) 
 * used for authentication in the weather application.
 *
 */
@Service
@RequiredArgsConstructor
public class JwtService {

	/**
     * The secret key used for signing and verifying JWTs. This value is injected 
     * from the application properties using Spring's `@Value` annotation.
     */
	@Value("${jwt.secret}")
	private String secret;

	public String generateToken(String username) {
	    return Jwts.builder()
	        .setSubject(username)
	        .setId(UUID.randomUUID().toString())
	        .setIssuedAt(new Date())
	        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
	        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
	        .compact();
	}

	/**
	 * Validates the given JWT token and returns the username if the token is valid.
	 *
	 * @param token The JWT token to validate.
	 * @return A {@link Mono} emitting the username if the token is valid, or an error if the token is invalid.
	 */
	public Mono<String> validateTokenAndGetUsername(String token) {
		try {
			String username = Jwts.parserBuilder()
					.setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).build()
					.parseClaimsJws(token).getBody().getSubject();
			return Mono.just(username);
		} catch (JwtException e) {
//			return Mono.error(new InvalidTokenException("Invalid JWT token"));
			throw new IllegalArgumentException("Invalid JWT token");
		}
	}
}